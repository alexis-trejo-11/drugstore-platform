package microservice.order_service.orders.domain.models;

import microservice.order_service.external.address.domain.model.DeliveryAddress;
import microservice.order_service.external.users.domain.entity.User;
import microservice.order_service.orders.domain.models.enums.DeliveryMethod;
import microservice.order_service.orders.domain.models.valueobjects.*;

import java.util.Currency;

public class OrderFactory {
    public static Order createOrder(
            UserID userID,
            DeliveryMethod deliveryMethod,
            String notes,
            Money shippingCost,
            Money taxAmount,
            Currency currency,
            AddressID addressID
    ) {
        switch (deliveryMethod) {
            case STANDARD_DELIVERY, EXPRESS_DELIVERY -> {
                return createDeliveryOrder(deliveryMethod, notes, shippingCost, taxAmount, userID, addressID, currency);
            }
            case STORE_PICKUP -> {
                return createPickupOrder(notes, shippingCost, taxAmount, userID, currency);
            }
            default -> throw new IllegalArgumentException("Invalid delivery method");
        }
    }
    
    public static Order createDeliveryOrder(
            DeliveryMethod deliveryMethod,
            String notes,
            Money shippingCost,
            Money taxAmount,
            UserID userID,
            AddressID addressID,
            Currency currency
    ) {
        return Order.create(userID, deliveryMethod, addressID, notes, shippingCost, taxAmount, currency);
    }
    
    public static Order createPickupOrder(
            String notes,
            Money shippingCost,
            Money taxAmount,
            UserID userID,
            Currency currency
    ) {
        return Order.create(userID, DeliveryMethod.STORE_PICKUP, null, notes, shippingCost, taxAmount, currency);
    }
}
