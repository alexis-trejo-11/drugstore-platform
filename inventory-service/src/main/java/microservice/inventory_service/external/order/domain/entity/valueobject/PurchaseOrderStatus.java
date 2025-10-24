package microservice.inventory_service.external.order.domain.entity.valueobject;

public enum PurchaseOrderStatus {
    DRAFT,
    PENDING_APPROVAL,
    APPROVED,
    SENT,
    PARTIALLY_RECEIVED,
    RECEIVED,
    CANCELLED,
    REJECTED
}

