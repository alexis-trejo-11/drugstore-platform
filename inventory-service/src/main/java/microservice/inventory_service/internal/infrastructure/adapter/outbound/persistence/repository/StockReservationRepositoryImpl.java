package microservice.inventory_service.internal.infrastructure.adapter.outbound.persistence.repository;

import microservice.inventory_service.internal.core.stock.domain.valueobject.ReservationStatus;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.internal.core.stock.domain.valueobject.ReservationId;
import microservice.inventory_service.internal.core.stock.domain.valueobject.StockReservation;
import microservice.inventory_service.internal.core.stock.port.output.StockReservationRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
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
    public Optional<StockReservation> findActiveById(ReservationId id) {
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
        return List.of();
    }

    @Override
    public List<StockReservation> findActiveReservationsByInventoryId(InventoryId inventoryId) {
        return List.of();
    }

    @Override
    public void delete(ReservationId id) {

    }
}
