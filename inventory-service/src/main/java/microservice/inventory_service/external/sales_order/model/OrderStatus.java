package microservice.inventory_service.external.sales_order.model;

public enum OrderStatus {
    PENDING_APPROVAL,
    APPROVED,
    CANCELLED,
    PARTIALLY_FULFILLED,
    FULFILLED,
    READY_FOR_LEAVE,
    DELIVERED;
}

