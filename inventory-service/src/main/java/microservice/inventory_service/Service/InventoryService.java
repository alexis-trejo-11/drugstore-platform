package microservice.inventory_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Inventory.InventoryDTO;
import at.backend.drugstore.microservice.common_models.DTO.Inventory.InventoryInsertDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface InventoryService {
    CompletableFuture<Result<Void>> createInventory(InventoryInsertDTO inventoryInsertDTO);
    CompletableFuture<List<InventoryDTO>> getInventoriesByProductId(Long productId);
    CompletableFuture<Boolean> deleteInventory(Long inventoryId);
}
