package microservice.inventory_service.internal.inventory.core.alert.domain.entity.valueobject;

public enum AlertType {
    LOW_STOCK,
    OUT_OF_STOCK,
    EXPIRING_SOON,
    EXPIRED,
    OVERSTOCK,
    REORDER_NEEDED,
    BATCH_RECALL,
    DAMAGED_STOCK
}
