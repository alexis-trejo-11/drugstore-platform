package microservice.sale_service.Service;

import at.backend.drugstore.microservice.common_models.DTOs.Employee.EmployeeDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Product.ProductDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Sale.*;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Employee.ExternalEmployeeService;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Inventory.InventoryFacadeService;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Products.ProductFacadeService;
import at.backend.drugstore.microservice.common_models.Models.Sales.SaleStatus;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.sale_service.Mappers.SaleMapper;
import microservice.sale_service.Model.PhysicalSale;
import microservice.sale_service.Model.PhysicalSaleItem;
import microservice.sale_service.Repository.SaleRepository;
import microservice.sale_service.Service.DomainServices.SaleDomainService;
import microservice.sale_service.Mappers.DtoMapper;
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
    private final ProductFacadeService productFacadeService;
    private final ExternalEmployeeService externalEmployeeService;
    private final InventoryService inventoryService;


    @Autowired
    public SaleServiceImpl(SaleMapper saleMapper,
                           SaleRepository saleRepository,
                           ProductFacadeService productFacadeService,
                           ExternalEmployeeService externalEmployeeService,
                           InventoryService inventoryService) {
        this.saleMapper = saleMapper;
        this.saleRepository = saleRepository;
        this.productFacadeService = productFacadeService;
        this.externalEmployeeService = externalEmployeeService;
        this.inventoryService = inventoryService;
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Result<CreateSaleDTO>> createSale(SaleProductsDTO saleProductsDTO) {

        CompletableFuture<List<ProductDTO>> completableFutureProducts = productFacadeService.getProductsByIds(saleProductsDTO.getProductsId());
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

                        List<PhysicalSaleItem> saleItems = SaleDomainService.createSaleItems(saleProductsDTO.getItems(), productResult, sale);
                        sale.setSaleItems(saleItems);

                        PhysicalSale saleCalculated = SaleDomainService.calculateTotal(sale);
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

            Result<String> stockResult = inventoryService.updateInventory(sale);
            if (!stockResult.isSuccess()) {
                return Result.error(stockResult.getErrorMessage());
            }

            ProcessSaleDTO processSaleDTO = DtoMapper.paidSaleToReturnDTO(sale, paySaleDTO);
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

            SaleDTO saleDTO = DtoMapper.entityToDTO(saleOptional.get());
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
                    .map(DtoMapper::entityToDTO)
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
            return SaleDomainService.createSaleSummary(sales, startOfDay, endOfDay);
        });
    }
}
