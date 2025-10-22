package microservice.stock.infrastructure.adapter.outbound.persistence.repository;

import microservice.inventory.domain.entity.enums.ReservationStatus;
import microservice.inventory.domain.entity.valueobject.id.InventoryId;
import microservice.stock.domain.ReservationId;
import microservice.stock.domain.StockReservation;
import microservice.stock.domain.port.output.StockReservationRepository;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class StockReservationRepositoryImpl implements StockReservationRepository {
    @Override
    public StockReservation save(StockReservation reservation) {
        return null;
    }

    @Override
    public Optional<StockReservation> findById(ReservationId id) {
        return Optional.empty();
    }

    @Override
    public Page<StockReservation> findByInventoryID(InventoryId inventoryId) {
        return null;
    }

    @Override
    public Page<StockReservation> findByOrderId(String orderId) {
        return null;
    }

    @Override
    public Page<StockReservation> findByStatus(ReservationStatus status) {
        return null;
    }

    @Override
    public List<StockReservation> findAllExpiredReservations(LocalDateTime currentTime) {
        return null;
    }

    @Override
    public Page<StockReservation> findActiveReservationsByInventoryId(InventoryId inventoryId) {
        return null;
    }

    @Override
    public void delete(ReservationId id) {

    }
}
