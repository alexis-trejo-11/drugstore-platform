package microservice.sale_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Employee.EmployeeDTO;
import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import at.backend.drugstore.microservice.common_models.DTO.Sale.*;
import at.backend.drugstore.microservice.common_models.ExternalService.Employee.ExternalEmployeeService;
import at.backend.drugstore.microservice.common_models.ExternalService.Inventory.ExternalInventoryService;
import at.backend.drugstore.microservice.common_models.ExternalService.Products.ExternalProductService;
import at.backend.drugstore.microservice.common_models.Models.Sales.SaleStatus;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.sale_service.Model.PhysicalSale;
import microservice.sale_service.Model.PhysicalSaleItem;
import microservice.sale_service.Repository.SaleRepository;
import microservice.sale_service.Utils.ModelTransformer;
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

@Service
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final ExternalProductService externalProductService;
    private final ExternalEmployeeService externalEmployeeService;
    private final ExternalInventoryService externalInventoryService;

    @Autowired
    public SaleServiceImpl(SaleRepository saleRepository,
                           ExternalProductService externalProductService,
                           ExternalEmployeeService externalEmployeeService,
                           ExternalInventoryService externalInventoryService) {
        this.saleRepository = saleRepository;
        this.externalProductService = externalProductService;
        this.externalEmployeeService = externalEmployeeService;
        this.externalInventoryService = externalInventoryService;
    }

    @Async
    @Transactional
    public Result<CreateSaleDTO> createSale(SaleProductsDTO saleProductsDTO) {
        Result<List<ProductDTO>> productResult = externalProductService.findProducts(saleProductsDTO.getProductsId());
        if (!productResult.isSuccess()) {
            return Result.error(productResult.getErrorMessage());
        }

        Result<EmployeeDTO> employeeResult = externalEmployeeService.getEmployeeBySaleProductsDTO(saleProductsDTO);
        if (!employeeResult.isSuccess()) {
            return Result.error(employeeResult.getErrorMessage());
        }

        PhysicalSale sale = ModelTransformer.MakeSale(employeeResult.getData());
        saleRepository.saveAndFlush(sale);

        List<PhysicalSaleItem> saleItems = ModelTransformer.MakeSaleItems(saleProductsDTO.getItems(), productResult.getData(), sale);
        sale.setSaleItems(saleItems);

        PhysicalSale saleCalculated = calculateTotal(sale);
        saleRepository.saveAndFlush(saleCalculated);

        CreateSaleDTO createSaleDTO = ModelTransformer.SaleToCreateSaleDTO(sale);
        return Result.success(createSaleDTO);
    }

    @Async
    @Transactional
    public Result<ProcessSaleDTO> paySale(PaySaleDTO paySaleDTO) {
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

        ProcessSaleDTO processSaleDTO = ModelTransformer.PaidSaleToReturnDTO(sale, paySaleDTO);
        return Result.success(processSaleDTO);
    }

    @Async
    public Result<SaleDTO> getSaleById(Long saleId) {
        Optional<PhysicalSale> saleOptional = saleRepository.findById(saleId);
        if (saleOptional.isEmpty()) {
            return Result.error("Sale With Id:" + saleId + " Not Found");
        }

        SaleDTO saleDTO = ModelTransformer.saleToDTO(saleOptional.get());
        return Result.success(saleDTO);
    }

    @Async
    @Transactional
    public List<SaleDTO> getTodaySales() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        List<PhysicalSale> sales = saleRepository.findPhysicalSalesByDateAndStatus(startOfDay, endOfDay, SaleStatus.PAID);
        return sales.stream()
                .map(ModelTransformer::saleToDTO)
                .toList();
    }

    @Async
    @Transactional
    public SalesSummaryDTO getTodaySummarySales() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        List<PhysicalSale> sales = saleRepository.findPhysicalSalesByDateAndStatus(startOfDay, endOfDay, SaleStatus.PAID);
        return ModelTransformer.saleToSummaryDTO(sales, startOfDay, endOfDay);
    }

    private PhysicalSale calculateTotal(PhysicalSale sale) {
        BigDecimal total = BigDecimal.ZERO;

        for (var saleItem : sale.getSaleItems()) {
            BigDecimal itemPrice = saleItem.getProductUnitPrice();
            int quantity = saleItem.getProductQuantity();
            total = total.add(itemPrice.multiply(BigDecimal.valueOf(quantity)));
        }

        sale.setSubTotal(total);
        sale.setTotal(total);
        sale.setDiscount(BigDecimal.ZERO);
        sale.setSaleStatus(SaleStatus.PENDING_PAYMENT);

        return sale;
    }

    private Result<String> updateInventory(PhysicalSale sale) {
        List<SaleItemDTO> saleItemDTOS = new ArrayList<>();
        for (var saleItem : sale.getSaleItems()) {
            SaleItemDTO saleItemDTO = new SaleItemDTO();
            saleItemDTO.setProductId(saleItem.getProductId());
            saleItemDTO.setQuantity(saleItem.getProductQuantity());
            saleItemDTOS.add(saleItemDTO);
        }

        return externalInventoryService.updateStockBySaleItemDTO(saleItemDTOS);
    }
}
