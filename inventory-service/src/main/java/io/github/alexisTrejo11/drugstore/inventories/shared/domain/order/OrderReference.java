package io.github.alexisTrejo11.drugstore.inventories.shared.domain.order;

import io.github.alexisTrejo11.drugstore.inventories.order.sales.core.domain.entity.valueobject.SaleOrderId;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.entity.valueobject.PurchaseOrderId;

public record OrderReference(OrderType type, String orderId) {
    public static OrderReference forPurchaseOrder(PurchaseOrderId id) {
        return new OrderReference(OrderType.PURCHASE_ORDER, id.value());
    }

    public static OrderReference forSaleOrder(SaleOrderId id) {
        return new OrderReference(OrderType.SALE_ORDER, id.value());
    }

    public enum OrderType {
        PURCHASE_ORDER, SALE_ORDER
    }


    public static OrderReference from(OrderType type, String orderId) {
        return new OrderReference(type, orderId);
    }
}
