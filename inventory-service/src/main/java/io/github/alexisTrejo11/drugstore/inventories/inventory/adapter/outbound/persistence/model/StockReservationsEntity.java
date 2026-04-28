package io.github.alexisTrejo11.drugstore.inventories.inventory.adapter.outbound.persistence.model;

import lombok.*;

import jakarta.persistence.*;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.valueobject.ReservationStatus;
import io.github.alexisTrejo11.drugstore.inventories.shared.domain.order.OrderReference;
import io.github.alexisTrejo11.drugstore.inventories.shared.order.model.BaseEntity;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "stock_reservations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockReservationsEntity extends BaseEntity {
    @Column(name = "order_id", nullable = false, length = 36)
    private String orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type", nullable = false, length = 20)
    private OrderReference.OrderType orderType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status;

    @Column(name = "expiration_time", nullable = false)
    private LocalDateTime expirationTime;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationItemEntity> items;


}

