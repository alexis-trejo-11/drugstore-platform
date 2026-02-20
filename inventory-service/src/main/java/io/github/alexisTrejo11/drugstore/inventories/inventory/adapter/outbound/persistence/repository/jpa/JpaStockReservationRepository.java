package io.github.alexisTrejo11.drugstore.inventories.inventory.adapter.outbound.persistence.repository.jpa;

import io.github.alexisTrejo11.drugstore.inventories.inventory.adapter.outbound.persistence.model.StockReservationsEntity;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.valueobject.ReservationStatus;
import io.github.alexisTrejo11.drugstore.inventories.shared.domain.order.OrderReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JpaStockReservationRepository extends JpaRepository<StockReservationsEntity, String> {
    Page<StockReservationsEntity> findByStatus(ReservationStatus status, Pageable pageable);
    Optional<StockReservationsEntity> findByOrderIdAndOrderType(String orderId, OrderReference.OrderType type);
    List<StockReservationsEntity> findByExpirationTimeBeforeAndStatus(LocalDateTime time, ReservationStatus status);
}
