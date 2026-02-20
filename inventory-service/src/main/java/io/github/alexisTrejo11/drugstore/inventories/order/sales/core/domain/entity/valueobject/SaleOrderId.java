package io.github.alexisTrejo11.drugstore.inventories.order.sales.core.domain.entity.valueobject;

public record SaleOrderId(String value) {
    public static SaleOrderId of(String value) {
        return new SaleOrderId(value);
    }

    public static SaleOrderId generate() {
        return new SaleOrderId(java.util.UUID.randomUUID().toString());
    }
}
