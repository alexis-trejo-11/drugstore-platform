package microservice.inventory_service.inventory.core.batch.domain.entity.valueobject;

public enum BatchStatus {
    ACTIVE,
    QUARANTINED,
    EXPIRED,
    RECALLED,
    DAMAGED,
    DEPLETED
}
