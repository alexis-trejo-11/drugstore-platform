package microservice.inventory_service.internal.core.inventory.domain.entity.enums;

public enum AdjustmentReason {
    PHYSICAL_COUNT,
    DAMAGED_GOODS,
    EXPIRED,
    THEFT,
    DATA_CORRECTION,
    RETURNED,
    SYSTEM_ERROR,
    QUALITY_ISSUE
}
