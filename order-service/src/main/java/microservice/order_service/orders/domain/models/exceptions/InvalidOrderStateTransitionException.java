package microservice.order_service.orders.domain.models.exceptions;

import microservice.order_service.orders.domain.models.enums.OrderStatus;

public class InvalidOrderStateTransitionException extends OrderDomainException {
    public InvalidOrderStateTransitionException(OrderStatus from, OrderStatus to) {
        super(String.format("Cannot transition order status from %s to %s", from, to), "invalid_order_state_transition");
    }
}