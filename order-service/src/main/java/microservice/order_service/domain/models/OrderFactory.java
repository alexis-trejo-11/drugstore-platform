package microservice.order_service.domain.models;

import microservice.order_service.domain.models.enums.DeliveryMethod;
import microservice.order_service.domain.models.valueobjects.*;

import java.util.List;

public class OrderFactory {
    public static Order createOrder(CustomerID customerId, List<OrderItem> items,
                                    DeliveryMethod deliveryMethod, AddressID addressID, String notes) {
        return Order.create(customerId, items, deliveryMethod, addressID, notes);
    }
    
    public static Order createDeliveryOrder(CustomerID customerId, List<OrderItem> items,
                                            AddressID addressID, String notes) {
        return Order.create(customerId, items, DeliveryMethod.STANDARD_DELIVERY, addressID, notes);
    }
    
    public static Order createPickupOrder(CustomerID customerId, List<OrderItem> items, String notes) {
        return Order.create(customerId, items, DeliveryMethod.STORE_PICKUP, null, notes);
    }
    
    public static OrderItem createOrderItem(ProductID productId, String productName, Money unitPrice, int quantity) {
        return OrderItem.create(productId, productName, unitPrice, quantity, false);
    }
}
