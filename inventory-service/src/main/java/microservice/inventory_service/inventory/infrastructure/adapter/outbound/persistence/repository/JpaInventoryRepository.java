package microservice.inventory_service.inventory.infrastructure.adapter.outbound.persistence.repository;

import microservice.inventory_service.inventory.domain.entity.enums.InventoryStatus;
import microservice.inventory_service.inventory.infrastructure.adapter.outbound.persistence.model.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaInventoryRepository extends JpaRepository<InventoryEntity, String> {
    Optional<InventoryEntity> findByMedicineId(String medicineId);

    boolean existsByMedicineId(String medicineId);

    List<InventoryEntity> findByStatus(InventoryStatus status);

    List<InventoryEntity> findByWarehouseLocation(String warehouseLocation);
}
