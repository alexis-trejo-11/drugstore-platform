package microservice.inventory_service.order.sales.infrastructure.adapter.outbound.models;

import jakarta.persistence.*;
import lombok.*;
import microservice.inventory_service.order.sales.core.domain.entity.valueobject.DeliveryMethod;
import microservice.inventory_service.shared.order.model.BaseOrderEntity;

import java.util.List;

@Entity
@Table(name = "sale_orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SaleOrderEntity extends BaseOrderEntity {
    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_method", nullable = false)
    private DeliveryMethod deliveryMethod;

    @OneToMany(mappedBy = "saleOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SaleOrderItemEntity> items;

    @Column(name = "user_id", insertable = false, updatable = false, length = 36)
    private String customerUserId;

    @Column(name = "delivery_info_id", length = 36)
    private String deliveryInfoId;

    @Column(name = "pickup_info_id", length = 36)
    private String pickupInfoId;

    public SaleOrderEntity(String id) {
        this.setId(id);
    }
}
