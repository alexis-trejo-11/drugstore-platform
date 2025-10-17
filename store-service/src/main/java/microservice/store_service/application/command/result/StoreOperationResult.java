package microservice.store_service.application.command.result;

import microservice.store_service.domain.model.valueobjects.StoreID;

public record StoreOperationResult (
        OperationType operationType,
        String message
) {

    public enum OperationType {
        UPDATE,
        DELETE,
        ACTIVATE,
        DEACTIVATE
    }

    public static StoreOperationResult updateResult(StoreID storeID) {
        return new StoreOperationResult(OperationType.UPDATE, "Store with ID " + storeID + " has been updated.");
    }

    public static StoreOperationResult deleteResult(StoreID storeID) {
        return new StoreOperationResult(OperationType.DELETE, "Store with ID " + storeID + " has been deleted.");
    }

    public static StoreOperationResult activateResult(StoreID storeID) {
        return new StoreOperationResult(OperationType.ACTIVATE, "Store with ID " + storeID + " has been activated.");
    }

    public static StoreOperationResult deactivateResult(StoreID storeID) {
        return new StoreOperationResult(OperationType.DEACTIVATE, "Store with ID " + storeID + " has been deactivated.");
    }
}
