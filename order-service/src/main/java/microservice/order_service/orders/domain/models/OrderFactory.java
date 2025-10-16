package microservice.order_service.orders.domain.models;

import microservice.order_service.external.address.domain.model.AddressID;
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
            AddressID addressID
    ) {
        switch (deliveryMethod) {
            case STANDARD_DELIVERY, EXPRESS_DELIVERY -> {
                return createDeliveryOrder(deliveryMethod, notes, shippingCost, taxAmount, userID, addressID);
            }
            case STORE_PICKUP -> {
                return createPickupOrder(notes, shippingCost, taxAmount, userID);
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
            AddressID addressID
    ) {
        return Order.create(userID, deliveryMethod, addressID, notes, shippingCost, taxAmount);
    }
    
    public static Order createPickupOrder(
            String notes,
            Money shippingCost,
            Money taxAmount,
            UserID userID
    ) {
        return Order.create(userID, DeliveryMethod.STORE_PICKUP, null, notes, shippingCost, taxAmount);
    }
}
