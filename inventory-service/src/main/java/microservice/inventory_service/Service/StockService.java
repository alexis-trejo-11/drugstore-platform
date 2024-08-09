package microservice.inventory_service.Service;

import at.backend.drugstore.microservice.common_models.DTOs.Inventory.ProductStockDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Product.ProductDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Sale.SaleItemDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface StockService {

    CompletableFuture<Result<ProductDTO>> validateExistingProduct(Long productId);
    CompletableFuture<Result<Void>> updateStockFromSale(List<SaleItemDTO> saleItemDTOS);
    CompletableFuture<ProductStockDTO> getCurrentStockByProduct(Long productId, ProductDTO productDTO);
}
