package microservice.orders.core.models.enums;

public enum DeliveryMethod {
    DELIVERY("delivery", "Order will be delivered to customer address"),
    PICKUP("pickup", "Customer will pick up the order from store");

    private final String code;
    private final String description;

    DeliveryMethod(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public boolean requiresAddress() {
        return this == DELIVERY;
    }
}
