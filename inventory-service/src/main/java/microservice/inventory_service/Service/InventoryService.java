package microservice.inventory_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryInsertDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface InventoryService {
    CompletableFuture<Result<Void>> createInventory(InventoryInsertDTO inventoryInsertDTO);
    CompletableFuture<List<InventoryDTO>> getInventoriesByProductId(Long productId);
    boolean deleteInventory(Long inventoryId);
}
