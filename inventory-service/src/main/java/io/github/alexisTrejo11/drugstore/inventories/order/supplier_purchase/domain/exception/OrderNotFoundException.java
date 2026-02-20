package io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.exception;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.exception.base.InventoryException;

public class OrderNotFoundException extends InventoryException {
    public OrderNotFoundException(String message) {
        super(message, "ORDER_NOT_FOUND");
    }
}
