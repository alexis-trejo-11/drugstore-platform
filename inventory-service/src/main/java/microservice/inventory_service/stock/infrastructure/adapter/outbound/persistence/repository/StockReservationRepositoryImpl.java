package microservice.inventory_service.stock.infrastructure.adapter.outbound.persistence.repository;

import microservice.inventory_service.inventory.domain.entity.enums.ReservationStatus;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.InventoryId;
import microservice.inventory_service.stock.domain.ReservationId;
import microservice.inventory_service.stock.domain.StockReservation;
import microservice.inventory_service.stock.domain.port.output.StockReservationRepository;
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
