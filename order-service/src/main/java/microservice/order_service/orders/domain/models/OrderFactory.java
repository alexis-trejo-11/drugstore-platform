package microservice.order_service.orders.domain.models;

import microservice.order_service.external.address.domain.model.DeliveryAddress;
import microservice.order_service.external.users.domain.entity.User;
import microservice.order_service.orders.domain.models.enums.DeliveryMethod;
import microservice.order_service.orders.domain.models.valueobjects.*;

import java.util.List;

public class OrderFactory {
    public static Order createOrder(DeliveryMethod deliveryMethod, String notes,
                                    Money shippingCost, Money taxAmount,
                                    List<OrderItem> items, User user, DeliveryAddress address) {
        if (deliveryMethod.requiresAddress() && address == null) {
            throw new IllegalArgumentException("Address is required for delivery method: " + deliveryMethod);
        }

        switch (deliveryMethod) {
            case STANDARD_DELIVERY, EXPRESS_DELIVERY, HOME_DELIVERY -> {
                return createDeliveryOrder(deliveryMethod, notes, shippingCost, taxAmount, items, user, address);
            }
            case STORE_PICKUP -> {
                return createPickupOrder(deliveryMethod, notes, shippingCost, taxAmount, items, user);
            }
            default -> throw new IllegalArgumentException("Invalid delivery method");
        }
    }
    
    public static Order createDeliveryOrder(DeliveryMethod deliveryMethod, String notes,
                                            Money shippingCost, Money taxAmount,
                                            List<OrderItem> items, User user, DeliveryAddress address) {
        return Order.create(deliveryMethod, notes, shippingCost, taxAmount, items, user, address);
    }
    
    public static Order createPickupOrder(DeliveryMethod deliveryMethod, String notes,
                                          Money shippingCost, Money taxAmount,
                                          List<OrderItem> items, User user) {
        return Order.create(DeliveryMethod.STORE_PICKUP, notes, shippingCost, taxAmount, items, user, null);
    }
}
