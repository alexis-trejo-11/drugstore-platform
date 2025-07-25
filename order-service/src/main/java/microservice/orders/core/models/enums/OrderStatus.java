package microservice.orders.core.models.enums;

import java.util.Arrays;
import java.util.List;

public enum OrderStatus {
    PENDING("pending", "Order has been placed but not yet processed"),
    CONFIRMED("confirmed", "Order has been confirmed and is being prepared"),
    PREPARING("preparing", "Order is being prepared for delivery/pickup"),
    READY_FOR_PICKUP("ready_for_pickup", "Order is ready to be picked up"),
    OUT_FOR_DELIVERY("out_for_delivery", "Order is out for delivery"),
    DELIVERED("delivered", "Order has been delivered"),
    PICKED_UP("picked_up", "Order has been picked up by customer"),
    CANCELLED("cancelled", "Order has been cancelled"),
    RETURNED("returned", "Order has been returned");

    private final String code;
    private final String description;

    OrderStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public boolean canTransitionTo(OrderStatus newStatus) {
        return switch (this) {
            case PENDING -> Arrays.asList(CONFIRMED, CANCELLED).contains(newStatus);
            case CONFIRMED -> Arrays.asList(PREPARING, CANCELLED).contains(newStatus);
            case PREPARING -> Arrays.asList(READY_FOR_PICKUP, OUT_FOR_DELIVERY, CANCELLED).contains(newStatus);
            case READY_FOR_PICKUP -> Arrays.asList(PICKED_UP, CANCELLED).contains(newStatus);
            case OUT_FOR_DELIVERY -> Arrays.asList(DELIVERED, CANCELLED).contains(newStatus);
            case DELIVERED -> List.of(RETURNED).contains(newStatus);
            case PICKED_UP -> List.of(RETURNED).contains(newStatus);
            case CANCELLED, RETURNED -> false; // Terminal states
        };
    }

    public boolean isTerminal() {
        return this == DELIVERED || this == PICKED_UP || this == CANCELLED || this == RETURNED;
    }

    public boolean isActive() {
        return !isTerminal();
    }
}
