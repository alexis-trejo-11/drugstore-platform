package microservice.inventory.infrastructure.adapter.outbound.persistence.repository;

import microservice.inventory.infrastructure.adapter.outbound.persistence.model.InventoryEntity;
import microservice.inventory.infrastructure.adapter.outbound.persistence.model.InventoryMovementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaInventoryMovementRepository extends JpaRepository<InventoryMovementEntity, String> {
}
