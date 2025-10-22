package microservice.inventory.infrastructure.adapter.outbound.persistence.repository;

import microservice.inventory.infrastructure.adapter.outbound.persistence.model.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaInventoryRepository extends JpaRepository<InventoryEntity, String> {
}
