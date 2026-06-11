package io.github.alexisTrejo11.drugstore.order.orders.domain.exceptions;

public class EmptyOrderException extends OrderDomainException {
    public EmptyOrderException() {
        super("Order must contain at least one item", "empty_order");
    }
}
