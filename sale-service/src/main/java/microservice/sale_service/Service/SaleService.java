package microservice.sale_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Employee.EmployeeDTO;
import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;

import at.backend.drugstore.microservice.common_models.DTO.Sale.*;
import at.backend.drugstore.microservice.common_models.ExternalService.Employee.ExternalEmployeeService;
import at.backend.drugstore.microservice.common_models.ExternalService.Inventory.ExternalInventoryService;
import at.backend.drugstore.microservice.common_models.ExternalService.Inventory.ExternalInventoryServiceImpl;
import at.backend.drugstore.microservice.common_models.ExternalService.Products.ExternalProductService;
import at.backend.drugstore.microservice.common_models.Models.Sales.SaleStatus;
import at.backend.drugstore.microservice.common_models.Utils.Result;

import microservice.sale_service.Model.PhysicalSale;
import microservice.sale_service.Model.PhysicalSaleItem;
import microservice.sale_service.Repository.*;
import microservice.sale_service.Utils.ModelTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final ExternalProductService externalProductService;
    private final ExternalEmployeeService externalEmployeeService;
    private final ExternalInventoryService externalInventoryService;

    @Autowired
    public SaleService(SaleRepository saleRepository,
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
    public Result<?> createSale(SaleProductsDTO saleProductsDTO) {
        // Fetch Product Data
        Result<List<ProductDTO>> productResult = externalProductService.findProducts(saleProductsDTO.getProductsId());
        if (!productResult.isSuccess()) {
            return productResult;
        }

        // Fetch Employee Data
        Result<EmployeeDTO> employeeResult = externalEmployeeService.getEmployeeBySaleProductsDTO(saleProductsDTO);
        if (!employeeResult.isSuccess()) {
            return employeeResult;
        }

        // Make Sale
        PhysicalSale sale = ModelTransformer.MakeSale(employeeResult.getData());
        saleRepository.saveAndFlush(sale);

        //Make Sale Items
        List<PhysicalSaleItem> saleItem = ModelTransformer.MakeSaleItems(saleProductsDTO.getItems(), productResult.getData(), sale);
        sale.setSaleItems(saleItem);

        // Calculate Total
        PhysicalSale saleCalculated = calculateTotal(sale);

        // Insert Into Database
        saleRepository.saveAndFlush(saleCalculated);

        //Return DTO
        CreateSaleDTO createSaleDTO = ModelTransformer.SaleToCreateSaleDTO(sale);
        return Result.success(createSaleDTO);
    }


    @Async
    @Transactional
    public Result<?> paySale(PaySaleDTO paySaleDTO) {
        // Find Sale
       Optional<PhysicalSale> sale = saleRepository.findById(paySaleDTO.getSaleId());
        if (sale.isEmpty()) {
            return Result.error("Sale With Id:" + paySaleDTO.getSaleId() + " Not Found");
        }

        // Validate Enough Money
        BigDecimal moneyProvided = paySaleDTO.getMoneyProvided();
        BigDecimal totalToPay = sale.get().getTotal();

        int comparisonResult = totalToPay.compareTo(moneyProvided);

        // Compare Money
        if (comparisonResult > 0) {
            // Not Enough Money
            BigDecimal moneyLeft = totalToPay.subtract(moneyProvided);
            return Result.error("Not Enough Money: " + moneyLeft + "$ Left");
        } else {
            //Process Sale
            sale.get().setSaleStatus(SaleStatus.PAID);
            sale.get().setPayType(PhysicalSale.PayType.valueOf(paySaleDTO.getPayType()));

            saleRepository.saveAndFlush(sale.get());

            // Update Stock
            List<SaleItemDTO> saleItemDTOS = new ArrayList<>();
            for (var saleItem : sale.get().getSaleItems()) {
                SaleItemDTO saleItemDTO = new SaleItemDTO();
                saleItemDTO.setProductId(saleItem.getProductId());
                saleItemDTO.setQuantity(saleItem.getProductQuantity());

                saleItemDTOS.add(saleItemDTO);
            }

            Result<String> stockResult = externalInventoryService.updateStockBySaleItemDTO(saleItemDTOS);
            if (!stockResult.isSuccess()) {
                return Result.error(stockResult.getErrorMessage());
            }

            //Return DTO
            ProcessSaleDTO createSaleDTO = ModelTransformer.PaidSaleToReturnDTO(sale.get(), paySaleDTO);
            return Result.success(createSaleDTO);
        }

    }

    @Async
    public Result<SaleDTO> getSaleById(Long saleId) {
      Optional<PhysicalSale> saleOptional = saleRepository.findById(saleId);
        if (saleOptional.isEmpty()) {
            return Result.error("Sale With Id:" + saleId + "Not Found");
        }

        SaleDTO saleDTO = ModelTransformer.saleToDTO(saleOptional.get());
        return Result.success(saleDTO);
    }

    @Async
    @Transactional
    public List<SaleDTO> getTodaySales() {
        // Status
        SaleStatus status = SaleStatus.PAID;

        // Date Timer
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        // Make DTOs and Return It
        List<PhysicalSale> sales = saleRepository.findPhysicalSalesByDateAndStatus(startOfDay, endOfDay, status);
        return sales.stream()
                .map(ModelTransformer::saleToDTO)
                .toList();

    }

    @Async
    @Transactional
    public CompletableFuture<SalesSummaryDTO> getTodaySummarySales() {
        try {
            // Define the sale status we are interested in
            final SaleStatus paid = SaleStatus.PAID;

            // Define the start and end of today
            LocalDate today = LocalDate.now();
            LocalDateTime startOfDay = today.atStartOfDay();
            LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

            // Fetch sales for today with the status PAID
            List<PhysicalSale> sales = saleRepository.findPhysicalSalesByDateAndStatus(startOfDay, endOfDay, paid);

            // Transform the sales list into a summary DTO
            SalesSummaryDTO summaryDTO = ModelTransformer.saleToSummaryDTO(sales, startOfDay, endOfDay);

            // Return the summary DTO wrapped in a CompletableFuture
            return CompletableFuture.completedFuture(summaryDTO);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }


    private PhysicalSale calculateTotal(PhysicalSale sale) {
        // Init Total And SubTotal
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal subTotal = BigDecimal.ZERO;

        for (var saleItem : sale.getSaleItems()) {
            BigDecimal itemPrice = saleItem.getProductUnitPrice();
            int quantity = saleItem.getProductQuantity();

            BigDecimal productTotal = itemPrice.multiply(new BigDecimal(quantity));
            subTotal = subTotal.add(productTotal);
        }

        total = subTotal;

        sale.setSubTotal(subTotal);
        sale.setTotal(total);
        sale.setDiscount(BigDecimal.ZERO);
        sale.setSaleStatus(SaleStatus.PENDING_PAYMENT);

        return sale;
    }
}

