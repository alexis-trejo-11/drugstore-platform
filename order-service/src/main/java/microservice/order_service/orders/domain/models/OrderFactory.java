package microservice.order_service.orders.domain.models;

import microservice.order_service.external.users.domain.entity.User;
import microservice.order_service.orders.domain.models.enums.DeliveryMethod;
import microservice.order_service.orders.domain.models.valueobjects.*;


import java.util.List;

public class OrderFactory {
    public static Order createOrder(User user, List<OrderItem> items,
                                    DeliveryMethod deliveryMethod, AddressID addressID, String notes) {
        return Order.create(user, items, deliveryMethod, addressID, notes);
    }
    
    public static Order createDeliveryOrder(User user, List<OrderItem> items,
                                            AddressID addressID, String notes) {
        return Order.create(user, items, DeliveryMethod.STANDARD_DELIVERY, addressID, notes);
    }
    
    public static Order createPickupOrder(User user, List<OrderItem> items, String notes) {
        return Order.create(user, items, DeliveryMethod.STORE_PICKUP, null, notes);
    }
    
    public static OrderItem createOrderItem(ProductID productId, String productName, Money unitPrice, int quantity) {
        return OrderItem.create(productId, productName, unitPrice, quantity, false);
    }
}
