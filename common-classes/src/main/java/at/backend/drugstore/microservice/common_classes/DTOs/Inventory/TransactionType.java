package at.backend.drugstore.microservice.common_classes.DTOs.Inventory;

public enum TransactionType {
    RECEIVED, SOLD, RETURNED, DAMAGED, EXPIRED;

    public static TransactionType getByIndex(int index) {
        // Check if the index is within bounds
        if (index < 0 || index >= TransactionType.values().length) {
            throw new IllegalArgumentException("Invalid index: " + index);
        }
        return TransactionType.values()[index];
    }
}