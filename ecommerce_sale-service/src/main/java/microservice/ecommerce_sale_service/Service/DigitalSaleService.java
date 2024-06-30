package microservice.ecommerce_sale_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;

import at.backend.drugstore.microservice.common_models.DTO.Sale.*;
import at.backend.drugstore.microservice.common_models.ExternalService.Inventory.ExternalInventoryService;
import at.backend.drugstore.microservice.common_models.ExternalService.Products.ExternalProductService;
import at.backend.drugstore.microservice.common_models.Models.Sales.SaleStatus;
import at.backend.drugstore.microservice.common_models.Utils.Result;

import microservice.ecommerce_sale_service.Model.DigitalSale;
import microservice.ecommerce_sale_service.Model.DigitalSaleItem;
import microservice.ecommerce_sale_service.Repository.DigitalSaleRepository;
import microservice.ecommerce_sale_service.Utils.ModelTransformer;
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
public class DigitalSaleService {

    private final DigitalSaleRepository saleRepository;
    private final ExternalProductService externalProductService;
    private final ExternalInventoryService externalInventoryService;

    @Autowired
    public DigitalSaleService(DigitalSaleRepository saleRepository,
                              ExternalProductService externalProductService,
                              ExternalInventoryService externalInventoryService) {

        this.saleRepository = saleRepository;
        this.externalProductService = externalProductService;
        this.externalInventoryService = externalInventoryService;
    }

    @Async
    @Transactional
    public CompletableFuture<Result<Void>> createDigitalSale(SaleDTO saleDTO, CartDTO cartDTO) {
        // Fetch Product Data
        Result<List<ProductDTO>> productResult = externalProductService.findProducts(saleDTO.getProductsIds());
        if (!productResult.isSuccess()) {
            Result<Void> result = new Result<>();
            result.setStatus(productResult.getStatus());
            result.setErrorMessage(productResult.getErrorMessage());
            return CompletableFuture.completedFuture(result);
        }

        // Make Sale
        DigitalSale sale = ModelTransformer.MakeDigitalSale(cartDTO, saleDTO);
        saleRepository.saveAndFlush(sale);

        //Make Sale Items
        List<DigitalSaleItem> saleItem = ModelTransformer.MakeSaleItems(cartDTO.getCartItems(), productResult.getData(), sale);
        sale.setSaleItems(saleItem);

        saleRepository.saveAndFlush(sale);

        //Return DTO
        return CompletableFuture.completedFuture(Result.success());
    }

    @Async
    @Transactional
    public CompletableFuture<Result<Void>> addOrderToSale(Long saleId, Long orderId) {
       Optional<DigitalSale> optionalDigitalSale = saleRepository.findById(saleId);
        if (optionalDigitalSale.isEmpty()) {
            return CompletableFuture.completedFuture(Result.error("Digital Sale With Id " + saleId + "Not Found"));
        }

        optionalDigitalSale.get().setOrderId(orderId);
        saleRepository.saveAndFlush(optionalDigitalSale.get());

        //Return DTO
        return CompletableFuture.completedFuture(Result.success());
    }



    @Async
    public Result<SaleDTO> getSaleById(Long saleId) {
      Optional<DigitalSale> saleOptional = saleRepository.findById(saleId);
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
        List<DigitalSale> sales = saleRepository.findDigitalSalesByDate(startOfDay, endOfDay);
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
            List<DigitalSale> sales = saleRepository.findDigitalSalesByDate(startOfDay, endOfDay);

            // Transform the sales list into a summary DTO
            SalesSummaryDTO summaryDTO = ModelTransformer.saleToSummaryDTO(sales, startOfDay, endOfDay);

            // Return the summary DTO wrapped in a CompletableFuture
            return CompletableFuture.completedFuture(summaryDTO);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

}

