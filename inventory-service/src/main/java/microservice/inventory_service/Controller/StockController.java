package microservice.inventory_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.ProductStockDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Sale.SaleItemDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import microservice.inventory_service.Service.StockServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@Tag(name = "Drugstore Microservice API (Inventory Service)", description = "Service for managing inventory stock")
public class StockController {

    private final StockServiceImpl stockServiceImpl;

    @Autowired
    public StockController(StockServiceImpl stockServiceImpl) {
        this.stockServiceImpl = stockServiceImpl;
    }

    @Operation(summary = "Get stock information by product ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock information found"),
            @ApiResponse(responseCode = "204", description = "Product out of stock"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{productId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<ProductStockDTO>>> getStockByProductId(@PathVariable Long productId) {
        log.info("Fetching stock information for product ID: {}", productId);
        return stockServiceImpl.validateExistingProduct(productId)
                .thenCompose(productDTOResult -> {
                    if(!productDTOResult.isSuccess()) {
                        log.info("Product with ID {} not found.", productId);
                        return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseWrapper<>(false, null, "Not Found", 404)));
                    }

                    return stockServiceImpl.getCurrentStockByProduct(productId, productDTOResult.getData())
                            .thenApply(productStockDTO -> {
                                if (productStockDTO == null) {
                                    log.info("Product with ID {} is out of stock", productId);
                                    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ResponseWrapper<>(false, null, "Product Out of Stock", 204));
                                }

                                log.info("Stock information found for product ID: {}", productId);
                                return ResponseEntity.status(HttpStatus.OK).body(new ResponseWrapper<>(true, productStockDTO, "Stock Found", 200));
                            });
                });
    }

    @Operation(summary = "Update stock based on sale items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock successfully updated"),
            @ApiResponse(responseCode = "409", description = "Stock update conflict")
    })
    @PutMapping("/update")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> updateStock(@RequestBody List<SaleItemDTO> saleItemDTOS) {
        log.info("Updating stock based on sale items: {}", saleItemDTOS);

        return stockServiceImpl.updateStockFromSale(saleItemDTOS)
                .thenApply(updateResult -> {
                    if (!updateResult.isSuccess()) {
                        log.warn("Stock update failed: {}", updateResult.getErrorMessage());
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseWrapper<>(false, null, updateResult.getErrorMessage(), 409));
                    }

                    log.info("Stock successfully updated based on sale items");
                    return ResponseEntity.status(HttpStatus.OK).body(new ResponseWrapper<>(true, null, "Stock Updated", 200));
                });
    }
}
