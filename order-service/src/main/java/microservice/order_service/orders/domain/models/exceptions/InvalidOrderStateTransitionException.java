package microservice.order_service.orders.domain.models.exceptions;

import microservice.order_service.orders.domain.models.enums.OrderStatus;

import java.util.HashMap;
import java.util.Map;

public class InvalidOrderStateTransitionException extends OrderDomainException {
    private final String fromStatus;
    private final String toStatus;

    public InvalidOrderStateTransitionException(OrderStatus from, OrderStatus to) {
        super(
                String.format("Cannot transition from %s to %s", from, to),
                "INVALID_STATE_TRANSITION"
        );
        this.fromStatus = from.name();
        this.toStatus = to.name();
    }

    public String getFromStatus() {
        return fromStatus;
    }

    public String getToStatus() {
        return toStatus;
    }

    @Override
    public Map<String, Object> getLoggingContext() {
        Map<String, Object> context = new HashMap<>(super.getLoggingContext());
        context.put("fromStatus", fromStatus);
        context.put("toStatus", toStatus);
        return context;
    }
}