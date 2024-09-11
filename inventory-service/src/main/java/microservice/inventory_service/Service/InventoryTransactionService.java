package microservice.inventory_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryTransactionInsertDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;

public interface InventoryTransactionService {
    void createInventoryTransaction(InventoryTransactionInsertDTO inventoryTransactionInsertDTO);
    Result<Void> validateSupplier(Long supplierId);
}
