package microservice.inventory_service.inventory.adapter.outbound.persistence.repository.jpa;

import microservice.inventory_service.inventory.adapter.outbound.persistence.model.StockReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaStockReservationRepository extends JpaRepository<StockReservationEntity, String> {
}
