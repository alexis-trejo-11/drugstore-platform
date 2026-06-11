package io.github.alexisTrejo11.drugstore.order.orders.domain.exceptions;

import libs_kernel.exceptions.ValidationException;

public class OrderValidationException extends ValidationException {
    public OrderValidationException(String message) {
        super(message, "order_validation_error");
    }
}
