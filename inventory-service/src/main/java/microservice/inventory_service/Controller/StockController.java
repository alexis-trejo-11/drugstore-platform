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

@RestController
@RequestMapping("/inventory/stock")
public class StockController {

    private static final Logger logger = LoggerFactory.getLogger(StockController.class);

    private final StockServiceImpl stockServiceImpl;

    @Autowired
    public StockController(StockServiceImpl stockServiceImpl) {
        this.stockServiceImpl = stockServiceImpl;
    }

    /**
     * Get stock information for a specific product.
     *
     * @param productId the ID of the product to retrieve stock information for
     * @return ResponseEntity containing the stock information and status code
     */
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductStockDTO>> getStockByProductId(@PathVariable Long productId) {
        logger.info("Fetching stock information for product ID: {}", productId);

        Result<ProductDTO> productDTOResult = stockServiceImpl.validateExistingProduct(productId);
        if(!productDTOResult.isSuccess()) {
            logger.info("Product with ID {} not found.", productId);
            return ResponseEntity.status(productDTOResult.getStatus()).body(new ApiResponse<>(false, null, productDTOResult.getErrorMessage(), productDTOResult.getStatus().value()));
        }

        ProductStockDTO productStockDTO = stockServiceImpl.getCurrentStockByProduct(productId, productDTOResult.getData());
        if (productStockDTO == null) {
            logger.info("Product with ID {} is out of stock", productId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiResponse<>(false, null, "Product Out of Stock", 204));
        }

        logger.info("Stock information found for product ID: {}", productId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, productStockDTO, "Stock Found", 200));
    }

    /**
     * Update stock based on a list of sale items.
     *
     * @param saleItemDTOS the list of sale items to update stock for
     * @return ResponseEntity indicating the result of the stock update operation
     */
    @PutMapping("/update")
    public ResponseEntity<ApiResponse<Void>> updateStock(@RequestBody List<SaleItemDTO> saleItemDTOS) {
        logger.info("Updating stock based on sale items: {}", saleItemDTOS);

        Result<Void> updateResult = stockServiceImpl.updateStockFromSale(saleItemDTOS);
        if (!updateResult.isSuccess()) {
            logger.warn("Stock update failed: {}", updateResult.getErrorMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>(false, null, updateResult.getErrorMessage(), 409));
        }

        logger.info("Stock successfully updated based on sale items");
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, null, "Stock Updated", 200));
    }
}
