package microservice.inventory_service.inventory.adapter.outbound.persistence.repository;

import libs_kernel.mapper.JpaEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.inventory_service.inventory.adapter.outbound.persistence.model.StockReservationsEntity;
import microservice.inventory_service.inventory.adapter.outbound.persistence.repository.jpa.JpaStockReservationRepository;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.inventory.core.stock.domain.entity.StockReservation;
import microservice.inventory_service.inventory.core.stock.domain.valueobject.ReservationId;
import microservice.inventory_service.inventory.core.stock.domain.valueobject.ReservationStatus;
import microservice.inventory_service.inventory.core.stock.port.output.StockReservationRepository;
import microservice.inventory_service.shared.domain.order.OrderReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class StockReservationRepositoryImpl implements StockReservationRepository {
    private final JpaStockReservationRepository reservationRepository;
    private final JpaEntityMapper<StockReservationsEntity, StockReservation> reservationJpaEntityMapper;

    @Override
    public StockReservation save(StockReservation reservation) {
        StockReservationsEntity entity = reservationJpaEntityMapper.fromDomain(reservation);

        log.info("Saving StockReservation inventory item0: {}", entity.getItems().getFirst());
        StockReservationsEntity createdEntity = reservationRepository.save(entity);

       return reservationJpaEntityMapper.toDomain(createdEntity);
    }

    @Override
    public Optional<StockReservation> findById(ReservationId id) {
       return reservationRepository.findById(id.value()).
               map(reservationJpaEntityMapper::toDomain);
    }

    @Override
    public Optional<StockReservation> findByOrderReference(OrderReference orderReference) {
        return reservationRepository.findByOrderIdAndOrderType(orderReference.orderId(), orderReference.type())
                .map(reservationJpaEntityMapper::toDomain);
    }

    @Override
    public Page<StockReservation> findByStatus(ReservationStatus status, Pageable pageable) {
        Page<StockReservationsEntity> entitiesPage = reservationRepository.findByStatus(status, pageable);
        return entitiesPage.map(reservationJpaEntityMapper::toDomain);
    }

    @Override
    public List<StockReservation> findAllExpiredReservations(LocalDateTime currentTime) {
        List<StockReservationsEntity> entities = reservationRepository
                .findByExpirationTimeBeforeAndStatus(currentTime, ReservationStatus.ACTIVE);

        return entities.stream()
                .map(reservationJpaEntityMapper::toDomain)
                .toList();

    }


    @Override
    public List<StockReservation> findActiveReservationsByInventoryId(InventoryId inventoryId) {
      return List.of();
    }



    @Override
    public void delete(ReservationId id) {
        reservationRepository.deleteById(id.value());
    }
}
