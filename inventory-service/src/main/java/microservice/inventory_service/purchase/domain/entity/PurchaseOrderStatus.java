package microservice.inventory_service.purchase.domain.entity;

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

