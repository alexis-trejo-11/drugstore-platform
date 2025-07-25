package microservice.orders.core.ports.output;

import microservice.orders.core.models.Order;

public interface NotificationService {
    void notifyOrderCreated(Order order);
    void notifyOrderStatusChanged(Order order);
    void notifyOrderReadyForPickup(Order order);
    void notifyOrderOutForDelivery(Order order);
    void notifyOrderDelivered(Order order);
    void notifyOrderCancelled(Order order);
}
