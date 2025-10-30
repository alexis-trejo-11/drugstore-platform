package microservice.inventory_service.order.supplier_purchase.adapter.outbound.persistence.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.*;
import microservice.inventory_service.order.supplier_purchase.domain.entity.valueobject.OrderStatus;
import microservice.inventory_service.shared.order.model.BaseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchase_order")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PurchaseOrderEntityOrder extends BaseEntity {
    @Column(name = "supplier_id", nullable = false, length = 36)
    private String supplierId;

    @Column(name = "supplier_name", nullable = false)
    private String supplierName;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<PurchaseOrderItemEntityOrder> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "expected_delivery_date")
    private LocalDateTime expectedDeliveryDate;

    @Column(name = "actual_delivery_date")
    private LocalDateTime actualDeliveryDate;

    @Column(name = "delivery_location")
    private String deliveryLocation;

    @Column(name = "created_by", nullable = false, length = 36)
    private String createdBy;

    @Column(name = "approved_by", length = 36)
    private String approvedBy;

    public PurchaseOrderEntityOrder(String id) {
        super(id);
    }
}