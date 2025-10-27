package microservice.inventory_service.internal.inventory.core.movement.domain.valueobject;

public enum MovementType {
    RECEIPT,
    SALE,
    RETURN,
    ADJUSTMENT,
    TRANSFER,
    DAMAGE,
    EXPIRATION,
    RESERVATION,
    RELEASE
}
