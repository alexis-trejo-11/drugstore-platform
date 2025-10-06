package microservice.order_service.orders.domain.models.enums;

import lombok.Getter;

@Getter
public enum DeliveryMethod {
    HOME_DELIVERY("home_delivery", "Order will be delivered to customer's address"),
    STORE_PICKUP("pickup", "Customer will pick up the order from store"),
    EXPRESS_DELIVERY("express_delivery", "Order will be delivered quickly"),
    STANDARD_DELIVERY("standard_delivery", "Order will be delivered with standard shipping");

    private final String code;
    private final String description;

    DeliveryMethod(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public boolean requiresAddress() {
        return this == HOME_DELIVERY || this == EXPRESS_DELIVERY || this == STANDARD_DELIVERY;
    }

}
