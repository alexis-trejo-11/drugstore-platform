package microservice.inventory_service.Controller;

import at.backend.drugstore.microservice.common_models.DTO.Inventory.ProductStockDTO;
import at.backend.drugstore.microservice.common_models.DTO.Sale.SaleItemDTO;
import at.backend.drugstore.microservice.common_models.Utils.ResponseWrapper;
import microservice.inventory_service.Service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/inventory/stock")
public class StockController {

    private final StockService stockService;

    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/{productId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<ProductStockDTO>>> getStockByProductId(@PathVariable Long productId) {
        return stockService.getCurrentStockByProduct(productId)
                .thenApply(result -> {
                    if(!result.isSuccess()) {
                        ResponseWrapper<ProductStockDTO> errorResponse = new ResponseWrapper<>(null, result.getErrorMessage());
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                    } else  {
                        ResponseWrapper<ProductStockDTO> errorResponse = new ResponseWrapper<>(result.getData(), null);
                        return ResponseEntity.status(HttpStatus.OK).body(errorResponse);
                    }

        }).exceptionally(ex -> {
                    ResponseWrapper<ProductStockDTO> errorResponse = new ResponseWrapper<>( null, ex.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                });
    }


    @PutMapping("/update")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> updateStock(@RequestBody List<SaleItemDTO> saleItemDTOS) {
        return stockService.updateStockFromSale(saleItemDTOS)
                .thenApply(result -> {
                    if (!result.isSuccess()) {
                        ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, result.getErrorMessage());
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                    } else {
                        ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(result.getData(), null);
                        return ResponseEntity.status(HttpStatus.OK).body(errorResponse);
                    }
                })
                .exceptionally(ex -> {
                    ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, ex.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                });
    }
}
