package microservice.inventory_service.inventory.core.movement.domain.valueobject;

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
