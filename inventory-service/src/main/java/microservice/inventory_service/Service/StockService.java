package microservice.inventory_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Inventory.ProductStockDTO;
import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import at.backend.drugstore.microservice.common_models.DTO.Sale.SaleItemDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;

import java.util.List;

public interface StockService {

    Result<ProductDTO> validateExistingProduct(Long productId);
    Result<Void> updateStockFromSale(List<SaleItemDTO> saleItemDTOS);
    ProductStockDTO getCurrentStockByProduct(Long productId, ProductDTO productDTO);
}
