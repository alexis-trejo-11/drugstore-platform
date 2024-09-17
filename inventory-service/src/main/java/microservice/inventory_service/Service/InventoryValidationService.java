package microservice.inventory_service.Service;

import at.backend.drugstore.microservice.common_classes.Utils.Result;

public interface InventoryValidationService {
    boolean validateExistingInventoryItem(Long inventoryItemId);
    boolean validateExistingTransaction(Long transactionId);
    boolean validateExistingSupplier(Long supplierId);
    boolean validateExistingProduct(Long productId);
    boolean validateExistingEmployee(Long employeeId);
    Result<Void> validateInventoryItemRelationships(Long productId);
}
