package io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.exception;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.exception.base.InventoryException;

public class PurchaseOrderStatusValidationException extends InventoryException {
    public PurchaseOrderStatusValidationException(String message) {
        super(message, "PURCHASE_ORDER_VALIDATION_ERROR");
    }
}
