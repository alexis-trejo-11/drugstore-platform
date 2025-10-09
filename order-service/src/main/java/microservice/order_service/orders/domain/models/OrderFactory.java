package microservice.order_service.orders.domain.models;

import microservice.order_service.external.address.domain.model.DeliveryAddress;
import microservice.order_service.external.users.domain.entity.User;
import microservice.order_service.orders.domain.models.enums.DeliveryMethod;
import microservice.order_service.orders.domain.models.valueobjects.*;

import java.util.Currency;


public class OrderFactory {
    public static Order createOrder(DeliveryMethod deliveryMethod, String notes,
                                    Money shippingCost, Money taxAmount, User user,
                                    DeliveryAddress address,  Currency currency) {
        if (deliveryMethod.requiresAddress() && address == null) {
            throw new IllegalArgumentException("Address is required for delivery method: " + deliveryMethod);
        }

        switch (deliveryMethod) {
            case STANDARD_DELIVERY, EXPRESS_DELIVERY -> {
                return createDeliveryOrder(deliveryMethod, notes, shippingCost, taxAmount, user, address, currency);
            }
            case STORE_PICKUP -> {
                return createPickupOrder(notes, shippingCost, taxAmount, user, currency);
            }
            default -> throw new IllegalArgumentException("Invalid delivery method");
        }
    }
    
    public static Order createDeliveryOrder(DeliveryMethod deliveryMethod, String notes,
                                            Money shippingCost, Money taxAmount,
                                            User user, DeliveryAddress address, Currency currency) {
        return Order.create(deliveryMethod, notes, shippingCost, taxAmount, user, address, currency);
    }
    
    public static Order createPickupOrder(String notes,
                                          Money shippingCost, Money taxAmount,
                                          User user, Currency currency) {
        return Order.create(DeliveryMethod.STORE_PICKUP, notes, shippingCost, taxAmount, user, null, currency);
    }
}
