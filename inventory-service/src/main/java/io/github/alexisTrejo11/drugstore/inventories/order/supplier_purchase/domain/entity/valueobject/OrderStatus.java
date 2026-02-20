package io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.entity.valueobject;

public enum OrderStatus {
    DRAFT,
    PENDING_APPROVAL,
    APPROVED,
    SENT,
    PARTIALLY_RECEIVED,
    RECEIVED,
    CANCELLED,
    REJECTED
}

