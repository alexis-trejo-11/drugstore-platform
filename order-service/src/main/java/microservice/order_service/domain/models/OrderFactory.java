package microservice.order_service.domain.models;

import microservice.order_service.domain.models.enums.DeliveryMethod;
import microservice.order_service.domain.models.valueobjects.CustomerId;
import microservice.order_service.domain.models.valueobjects.DeliveryAddress;
import microservice.order_service.domain.models.valueobjects.Money;
import microservice.order_service.domain.models.valueobjects.ProductId;

import java.util.List;

public class OrderFactory {
    
    public static Order createOrder(CustomerId customerId, List<OrderItem> items, 
                                   DeliveryMethod deliveryMethod, DeliveryAddress deliveryAddress, String notes) {
        return Order.create(customerId, items, deliveryMethod, deliveryAddress, notes);
    }
    
    public static Order createDeliveryOrder(CustomerId customerId, List<OrderItem> items, 
                                          DeliveryAddress deliveryAddress, String notes) {
        return Order.create(customerId, items, DeliveryMethod.DELIVERY, deliveryAddress, notes);
    }
    
    public static Order createPickupOrder(CustomerId customerId, List<OrderItem> items, String notes) {
        return Order.create(customerId, items, DeliveryMethod.PICKUP, null, notes);
    }
    
    public static OrderItem createOrderItem(ProductId productId, String productName, Money unitPrice, int quantity) {
        return OrderItem.create(productId, productName, unitPrice, quantity);
    }
}
