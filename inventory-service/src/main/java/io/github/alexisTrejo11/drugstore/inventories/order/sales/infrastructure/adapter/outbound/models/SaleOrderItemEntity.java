package io.github.alexisTrejo11.drugstore.inventories.order.sales.infrastructure.adapter.outbound.models;

import jakarta.persistence.*;
import lombok.*;
import io.github.alexisTrejo11.drugstore.inventories.shared.order.model.BaseOrderItemEntity;

@Entity
@Table(name = "sale_order_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SaleOrderItemEntity extends BaseOrderItemEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private SaleOrderEntity saleOrder;

    @Column(name = "ordered_quantity", nullable = false)
    private Integer orderedQuantity;

    @Column(name = "received_quantity", nullable = false)
    private Integer receivedQuantity;
}
