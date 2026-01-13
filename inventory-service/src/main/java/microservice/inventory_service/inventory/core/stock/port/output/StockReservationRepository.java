package microservice.inventory_service.inventory.core.stock.port.output;

import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.inventory.core.stock.domain.entity.StockReservation;
import microservice.inventory_service.inventory.core.stock.domain.valueobject.ReservationStatus;
import microservice.inventory_service.inventory.core.stock.domain.valueobject.ReservationId;
import microservice.inventory_service.shared.domain.order.OrderReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StockReservationRepository {
    StockReservation save(StockReservation reservation);
    Optional<StockReservation> findById(ReservationId id);
    //Page<StockReservation> findByInventoryId(InventoryId inventoryId, Pageable pageable);
    Optional<StockReservation> findByOrderReference(OrderReference orderReference);
    Page<StockReservation> findByStatus(ReservationStatus status, Pageable pageable);
    List<StockReservation> findAllExpiredReservations(LocalDateTime currentTime);
    List<StockReservation> findActiveReservationsByInventoryId(InventoryId inventoryId);
    void delete(ReservationId id);
}
