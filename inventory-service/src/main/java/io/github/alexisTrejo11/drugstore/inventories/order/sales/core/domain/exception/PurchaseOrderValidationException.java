package io.github.alexisTrejo11.drugstore.inventories.order.sales.core.domain.exception;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.exception.base.InventoryException;

public class PurchaseOrderValidationException extends InventoryException {
    public PurchaseOrderValidationException(String message) {
        super(message, "PURCHASE_ORDER_VALIDATION_ERROR");
    }
}
