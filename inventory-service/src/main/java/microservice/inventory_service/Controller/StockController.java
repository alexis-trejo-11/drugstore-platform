package microservice.inventory_service.Controller;

import at.backend.drugstore.microservice.common_models.DTO.Inventory.ProductStockDTO;
import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import at.backend.drugstore.microservice.common_models.DTO.Sale.SaleItemDTO;
import at.backend.drugstore.microservice.common_models.Utils.ApiResponse;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.inventory_service.Service.StockServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("v1/api/inventory/stock")
public class StockController {

    private static final Logger logger = LoggerFactory.getLogger(StockController.class);
    private final StockServiceImpl stockServiceImpl;

    @Autowired
    public StockController(StockServiceImpl stockServiceImpl) {
        this.stockServiceImpl = stockServiceImpl;
    }

    @GetMapping("/{productId}")
    public CompletableFuture<ResponseEntity<ApiResponse<ProductStockDTO>>> getStockByProductId(@PathVariable Long productId) {
        logger.info("Fetching stock information for product ID: {}", productId);
        return stockServiceImpl.validateExistingProduct(productId)
                .thenCompose(productDTOResult -> {
                    if(!productDTOResult.isSuccess()) {
                        logger.info("Product with ID {} not found.", productId);
                        return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Not Found", 404)));
                    }

                    return stockServiceImpl.getCurrentStockByProduct(productId, productDTOResult.getData())
                            .thenApply(productStockDTO -> {
                                if (productStockDTO == null) {
                                    logger.info("Product with ID {} is out of stock", productId);
                                    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiResponse<>(false, null, "Product Out of Stock", 204));
                                }

                                logger.info("Stock information found for product ID: {}", productId);
                                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, productStockDTO, "Stock Found", 200));
                            });
                });
    }

    @PutMapping("/update")
    public CompletableFuture<ResponseEntity<ApiResponse<Void>>> updateStock(@RequestBody List<SaleItemDTO> saleItemDTOS) {
        logger.info("Updating stock based on sale items: {}", saleItemDTOS);

        return stockServiceImpl.updateStockFromSale(saleItemDTOS)
                .thenApply(updateResult -> {
                    if (!updateResult.isSuccess()) {
                        logger.warn("Stock update failed: {}", updateResult.getErrorMessage());
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>(false, null, updateResult.getErrorMessage(), 409));
                    }

                    logger.info("Stock successfully updated based on sale items");
                    return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, null, "Stock Updated", 200));
                });
    }
}