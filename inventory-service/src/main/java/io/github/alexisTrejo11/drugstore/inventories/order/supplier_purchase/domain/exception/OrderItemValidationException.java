package io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.exception;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.exception.base.InventoryException;

public class OrderItemValidationException extends InventoryException {
    public OrderItemValidationException(String message) {
        super(message, "INVALID_ORDER_ITEM");
    }
}
