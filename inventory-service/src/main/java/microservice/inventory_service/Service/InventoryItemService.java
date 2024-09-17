package microservice.inventory_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryItemDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryItemInsertDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import microservice.inventory_service.Utils.InventorySummary;
import microservice.inventory_service.Utils.ProductStockDTO;
import microservice.inventory_service.Utils.ProductStockStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InventoryItemService {
    InventoryItemDTO getInventoryItemById(Long inventoryItemId);
    Page<InventoryItemDTO> getInventoryItemByProductId(Long inventoryItemId, Pageable pageable);
    InventorySummary getInventorySummary();

    Result<Void> createInventoryItem(InventoryItemInsertDTO inventoryItemDTO);
    void updateInventoryItem(Long inventoryItemId, InventoryItemInsertDTO itemInsertDTO);
    void deleteInventoryItem(Long inventoryItemId);

    ProductStockStatus getProductTotalStock(Long productId);
    // List<StockAlert> getStockAlerts(AlertFilterParams filterParams);
}
