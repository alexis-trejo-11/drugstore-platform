package microservice.inventory_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryItemDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryItemInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryTransactionDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import microservice.inventory_service.Model.InventoryItem;
import microservice.inventory_service.Model.InventorySummary;
import microservice.inventory_service.Model.InventoryTransaction;
import org.springframework.data.domain.Page;

public interface InventoryService {
    Result<Void> createInventoryItem(InventoryItemInsertDTO inventoryItemDTO);
    void updateInventoryItem(Long inventoryItemId ,int quantity);
    InventoryItem updateInventoryItem(Long itemId, InventoryItemDTO itemDTO);

    Page<InventoryItemDTO> getInventoryByProduct(Long inventoryId);
    InventorySummary getInventorySummary();
    InventoryTransaction recordTransaction(InventoryTransactionDTO transactionDTO);

    Result<Void> validateDataToCreateInventory(Long supplier);
    boolean validateExistingInventoryItem(Long inventoryItemId);
    boolean validateExistingProduct(Long inventoryItemId);



    // List<StockAlert> getStockAlerts(AlertFilterParams filterParams);
}
