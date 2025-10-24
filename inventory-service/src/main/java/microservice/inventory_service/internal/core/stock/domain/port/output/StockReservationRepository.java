package microservice.inventory_service.internal.core.stock.domain.port.output;

import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.internal.core.stock.domain.StockReservation;
import microservice.inventory_service.internal.core.inventory.domain.entity.enums.ReservationStatus;
import microservice.inventory_service.internal.core.stock.domain.ReservationId;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StockReservationRepository {
    StockReservation save(StockReservation reservation);
    Optional<StockReservation> findById(ReservationId id);
    Optional<StockReservation> findActiveById(ReservationId id);
    Page<StockReservation> findByInventoryID(InventoryId inventoryId);
    Page<StockReservation> findByOrderId(String orderId);
    Page<StockReservation> findByStatus(ReservationStatus status);
    List<StockReservation> findAllExpiredReservations(LocalDateTime currentTime);
    List<StockReservation> findActiveReservationsByInventoryId(InventoryId inventoryId);
    void delete(ReservationId id);
}
