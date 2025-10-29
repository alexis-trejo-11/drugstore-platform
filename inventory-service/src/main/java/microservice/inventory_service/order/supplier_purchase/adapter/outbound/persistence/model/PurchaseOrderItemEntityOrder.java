package microservice.inventory_service.order.supplier_purchase.adapter.outbound.persistence.model;

import lombok.*;

import jakarta.persistence.*;
import lombok.experimental.SuperBuilder;
import microservice.inventory_service.shared.order.model.BaseOrderItemEntity;

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
    @Column(name = "purchase_order_id", length = 36)
    private String orderId;

    @Column(name = "ordered_quantity", nullable = false)
    private Integer orderedQuantity;

    @Column(name = "received_quantity", nullable = false)
    private Integer receivedQuantity;

    @Column(name = "batch_number", length = 100)
    private String batchNumber;
}