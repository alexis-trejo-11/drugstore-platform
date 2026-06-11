package io.github.alexisTrejo11.drugstore.order.orders.domain.exceptions;


import libs_kernel.exceptions.NotFoundException;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.OrderID;

public class OrderNotFoundIDException extends NotFoundException {
    public OrderNotFoundIDException(OrderID orderId) {
        super("Order", "id" ,orderId.value());
    }
}
