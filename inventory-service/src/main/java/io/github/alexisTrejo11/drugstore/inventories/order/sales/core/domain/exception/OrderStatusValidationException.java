package io.github.alexisTrejo11.drugstore.inventories.order.sales.core.domain.exception;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.exception.base.InventoryException;

public class OrderStatusValidationException extends InventoryException {
    public OrderStatusValidationException(String message) {
        super(message, "PURCHASE_ORDER_STATUS_VALIDATION_ERROR");
    }
}
