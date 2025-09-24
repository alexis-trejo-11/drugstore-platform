package microservice.order_service.infrastructure.persistence.models;



public enum OrderStatusModel {
    PENDING,
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    RETURNED,
    READY_FOR_PICKUP,
    PICKED_UP,
}