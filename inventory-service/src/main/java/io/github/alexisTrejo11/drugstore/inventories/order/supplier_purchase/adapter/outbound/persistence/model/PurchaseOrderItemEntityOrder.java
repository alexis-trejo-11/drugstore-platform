package io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.adapter.outbound.persistence.model;

import lombok.*;

import jakarta.persistence.*;
import lombok.experimental.SuperBuilder;
import io.github.alexisTrejo11.drugstore.inventories.shared.order.model.BaseOrderItemEntity;

@Entity
@Table(name = "purchase_order_items")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PurchaseOrderItemEntityOrder extends BaseOrderItemEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrderEntityOrder purchaseOrder;

    @Column(name = "ordered_quantity", nullable = false)
    private Integer orderedQuantity;

    @Column(name = "received_quantity", nullable = false)
    private Integer receivedQuantity;

    @Column(name = "batch_number", length = 100)
    private String batchNumber;
}