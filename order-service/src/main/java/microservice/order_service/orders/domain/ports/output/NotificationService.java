package microservice.order_service.orders.domain.ports.output;

import microservice.order_service.orders.domain.models.Order;

public interface NotificationService {
    void notifyOrderCreated(Order order);
    void notifyOrderStatusChanged(Order order);
    void notifyOrderReadyForPickup(Order order);
    void notifyOrderOutForDelivery(Order order);
    void notifyOrderDelivered(Order order);
    void notifyOrderCancelled(Order order);
}
