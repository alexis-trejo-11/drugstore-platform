package io.github.alexisTrejo11.drugstore.order.orders.domain.exceptions;

import libs_kernel.exceptions.ConflictException;

public class OrderAlreadyCompletedException extends ConflictException {
    public OrderAlreadyCompletedException(String message) {
        super(message, "order_already_completed");
    }
}

