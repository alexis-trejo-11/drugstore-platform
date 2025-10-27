package microservice.inventory_service.internal.inventory.core.batch.domain.entity.valueobject;

public enum BatchStatus {
    ACTIVE,
    QUARANTINED,
    EXPIRED,
    RECALLED,
    DAMAGED,
    DEPLETED
}
