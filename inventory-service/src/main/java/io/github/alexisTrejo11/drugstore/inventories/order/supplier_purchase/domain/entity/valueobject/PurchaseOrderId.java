package io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.entity.valueobject;

public record PurchaseOrderId(String value) {
    public static PurchaseOrderId of(String value) {
        return new PurchaseOrderId(value);
    }
    public static PurchaseOrderId generate() {
        return new PurchaseOrderId(java.util.UUID.randomUUID().toString());
    }
}
