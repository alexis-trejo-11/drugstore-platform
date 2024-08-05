package microservice.sale_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Employee.EmployeeDTO;
import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import at.backend.drugstore.microservice.common_models.DTO.Sale.*;
import at.backend.drugstore.microservice.common_models.ExternalService.Employee.ExternalEmployeeService;
import at.backend.drugstore.microservice.common_models.ExternalService.Inventory.ExternalInventoryService;
import at.backend.drugstore.microservice.common_models.ExternalService.Products.ExternalProductService;
import at.backend.drugstore.microservice.common_models.Models.Sales.SaleStatus;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.sale_service.Mappers.SaleMapper;
import microservice.sale_service.Model.PhysicalSale;
import microservice.sale_service.Model.PhysicalSaleItem;
import microservice.sale_service.Repository.SaleRepository;
import microservice.sale_service.Utils.SaleCreator;
import microservice.sale_service.Utils.saleProccesor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class SaleServiceImpl implements SaleService {

    private final SaleMapper saleMapper;
    private final SaleRepository saleRepository;
    private final ExternalProductService externalProductService;
    private final ExternalEmployeeService externalEmployeeService;
    private final ExternalInventoryService externalInventoryService;
    private final SaleCreator saleCreator;


    @Autowired
    public SaleServiceImpl(SaleMapper saleMapper,
                           SaleRepository saleRepository,
                           ExternalProductService externalProductService,
                           ExternalEmployeeService externalEmployeeService,
                           ExternalInventoryService externalInventoryService,
                           SaleCreator saleCreator) {
        this.saleMapper = saleMapper;
        this.saleRepository = saleRepository;
        this.externalProductService = externalProductService;
        this.externalEmployeeService = externalEmployeeService;
        this.externalInventoryService = externalInventoryService;
        this.saleCreator = saleCreator;
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Result<CreateSaleDTO>> createSale(SaleProductsDTO saleProductsDTO) {

        CompletableFuture<List<ProductDTO>> completableFutureProducts = externalProductService.getProductsByIds(saleProductsDTO.getProductsId());
        CompletableFuture<Result<EmployeeDTO>> completableFutureEmployee = externalEmployeeService.getEmployeeBySaleProductsDTO(saleProductsDTO);

        return CompletableFuture.allOf(completableFutureProducts, completableFutureEmployee)
                .thenApplyAsync(voidResult -> {
                    Result<CreateSaleDTO> result;
                    try {
                        List<ProductDTO> productResult = completableFutureProducts.get();
                        Result<EmployeeDTO> employeeResult = completableFutureEmployee.get();

                        if (productResult == null) {
                            return Result.error("Failed to retrieve products");
                        }

                        if (!employeeResult.isSuccess()) {
                            return Result.error(employeeResult.getErrorMessage());
                        }

                        PhysicalSale sale = saleMapper.employeeDTOtoEntity(employeeResult.getData());
                        saleRepository.saveAndFlush(sale);

                        List<PhysicalSaleItem> saleItems = saleCreator.createSaleItems(saleProductsDTO.getItems(), productResult, sale);
                        sale.setSaleItems(saleItems);

                        PhysicalSale saleCalculated = calculateTotal(sale);
                        saleRepository.saveAndFlush(saleCalculated);

                        CreateSaleDTO createSaleDTO = saleMapper.saleToCreateSaleDTO(sale);
                        result = Result.success(createSaleDTO);
                    } catch (Exception e) {
                        result = Result.error("Error during sale creation: " + e.getMessage());
                    }
                    return result;
                });
    }


    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Result<ProcessSaleDTO>> paySale(PaySaleDTO paySaleDTO) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<PhysicalSale> saleOptional = saleRepository.findById(paySaleDTO.getSaleId());
            if (saleOptional.isEmpty()) {
                return Result.error("Sale With Id:" + paySaleDTO.getSaleId() + " Not Found");
            }

            PhysicalSale sale = saleOptional.get();
            BigDecimal moneyProvided = paySaleDTO.getMoneyProvided();
            BigDecimal totalToPay = sale.getTotal();

            if (totalToPay.compareTo(moneyProvided) > 0) {
                BigDecimal moneyLeft = totalToPay.subtract(moneyProvided);
                return Result.error("Not Enough Money: " + moneyLeft + "$ Left");
            }

            sale.setSaleStatus(SaleStatus.PAID);
            sale.setPayType(PhysicalSale.PayType.valueOf(paySaleDTO.getPayType()));
            saleRepository.saveAndFlush(sale);

            Result<String> stockResult = updateInventory(sale);
            if (!stockResult.isSuccess()) {
                return Result.error(stockResult.getErrorMessage());
            }

            ProcessSaleDTO processSaleDTO = saleProccesor.paidSaleToReturnDTO(sale, paySaleDTO);
            return Result.success(processSaleDTO);
        });
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Result<SaleDTO>> getSaleById(Long saleId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<PhysicalSale> saleOptional = saleRepository.findById(saleId);
            if (saleOptional.isEmpty()) {
                return Result.error("Sale With Id:" + saleId + " Not Found");
            }

            SaleDTO saleDTO = saleProccesor.saleToDTO(saleOptional.get());
            return Result.success(saleDTO);
        });
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<List<SaleDTO>> getTodaySales() {
        return CompletableFuture.supplyAsync(() -> {
            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

            List<PhysicalSale> sales = saleRepository.findPhysicalSalesByDateAndStatus(startOfDay, endOfDay, SaleStatus.PAID);
            return sales.stream()
                    .map(saleProccesor::saleToDTO)
                    .toList();
        });
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<SalesSummaryDTO> getTodaySummarySales() {
        return CompletableFuture.supplyAsync(() -> {
            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

            List<PhysicalSale> sales = saleRepository.findPhysicalSalesByDateAndStatus(startOfDay, endOfDay, SaleStatus.PAID);
            return saleCreator.createSaleSummary(sales, startOfDay, endOfDay);
        });
    }

    private PhysicalSale calculateTotal(PhysicalSale sale) {
        BigDecimal total = BigDecimal.ZERO;

        for (var saleItem : sale.getSaleItems()) {
            BigDecimal itemPrice = saleItem.getProductUnitPrice();
            int quantity = saleItem.getProductQuantity();
            total = total.add(itemPrice.multiply(BigDecimal.valueOf(quantity)));
        }

        sale.setSubtotal(total);
        sale.setTotal(total);
        sale.setDiscount(BigDecimal.ZERO);
        sale.setSaleStatus(SaleStatus.PAID);

        return sale;
    }

    private Result<String> updateInventory(PhysicalSale sale) {
        List<SaleItemDTO> saleItemDTOS = new ArrayList<>();
        for (var saleItem : sale.getSaleItems()) {
            SaleItemDTO saleItemDTO = new SaleItemDTO();
            saleItemDTO.setProductId(saleItem.getProductId());
            saleItemDTO.setProductQuantity(saleItem.getProductQuantity());
            saleItemDTOS.add(saleItemDTO);
        }

        return externalInventoryService.updateStockBySaleItemDTO(saleItemDTOS);
    }
}
