package microservice.inventory_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Inventory.InventoryDTO;
import at.backend.drugstore.microservice.common_models.DTO.Inventory.InventoryInsertDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;

import java.util.List;

public interface InventoryService {
    Result<Void> createInventory(InventoryInsertDTO inventoryInsertDTO);
    List<InventoryDTO> getInventoriesByProductId(Long productId);
    boolean deleteInventory(Long inventoryId);
}
