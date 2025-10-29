package microservice.inventory_service.shared.domain.order;

public enum OrderStatus {
    PENDING_APPROVAL,
    APPROVED,
    CANCELLED,
    PARTIALLY_FULFILLED,
    FULFILLED,
    READY_FOR_LEAVE,
    DELIVERED;
}

