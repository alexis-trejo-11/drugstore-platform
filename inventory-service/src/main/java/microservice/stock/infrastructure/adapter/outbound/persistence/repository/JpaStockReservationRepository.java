package microservice.stock.infrastructure.adapter.outbound.persistence.repository;

import microservice.stock.infrastructure.adapter.outbound.persistence.model.StockReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaStockReservationRepository extends JpaRepository<StockReservationEntity, String> {
}
