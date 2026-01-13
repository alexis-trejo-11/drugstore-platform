package microservice.inventory_service.inventory.adapter.outbound.persistence.model;

import jakarta.persistence.*;
import lombok.*;
import microservice.inventory_service.shared.order.model.BaseEntity;

@Entity
@Table(name = "reservation_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationItemEntity extends BaseEntity {
    @Column(name = "reason")
    private String reason;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private StockReservationsEntity reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    private InventoryEntity inventory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    private InventoryBatchEntity batch;
}
