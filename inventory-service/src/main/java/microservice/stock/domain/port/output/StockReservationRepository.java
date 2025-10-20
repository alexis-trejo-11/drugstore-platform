package microservice.stock.domain.port.output;

import microservice.stock.domain.StockReservation;
import microservice.inventory.domain.entity.enums.ReservationStatus;
import microservice.inventory.domain.entity.valueobject.id.InventoryID;
import microservice.stock.domain.ReservationID;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Optional;

public interface StockReservationRepository {
    StockReservation save(StockReservation reservation);
    Optional<StockReservation> findById(ReservationID id);
    Page<StockReservation> findByInventoryID(InventoryID inventoryId);
    Page<StockReservation> findByOrderId(String orderId);
    Page<StockReservation> findByStatus(ReservationStatus status);
    Page<StockReservation> findExpiredReservations(LocalDateTime currentTime);
    Page<StockReservation> findActiveReservationsByInventoryId(InventoryID inventoryId);
    void delete(ReservationID id);
}
