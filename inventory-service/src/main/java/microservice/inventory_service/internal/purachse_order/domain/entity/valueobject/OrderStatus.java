package microservice.inventory_service.internal.purachse_order.domain.entity.valueobject;

public enum OrderStatus {
    DRAFT,
    PENDING_APPROVAL,
    APPROVED,
    SENT,
    PARTIALLY_RECEIVED,
    RECEIVED,
    CANCELLED,
    REJECTED
}

