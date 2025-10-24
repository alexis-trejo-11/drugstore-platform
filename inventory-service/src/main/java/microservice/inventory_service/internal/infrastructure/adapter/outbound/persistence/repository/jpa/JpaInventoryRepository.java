package microservice.inventory_service.internal.infrastructure.adapter.outbound.persistence.repository.jpa;

import microservice.inventory_service.internal.core.inventory.domain.entity.enums.InventoryStatus;
import microservice.inventory_service.internal.infrastructure.adapter.outbound.persistence.model.InventoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaInventoryRepository extends JpaRepository<InventoryEntity, String> {
    Optional<InventoryEntity> findByMedicineId(String medicineId);

    boolean existsByMedicineId(String medicineId);

    Page<InventoryEntity> findByStatus(InventoryStatus status, Pageable pageable);
    List<InventoryEntity> findByStatus(InventoryStatus status);

    Page<InventoryEntity> findByWarehouseLocation(String warehouseLocation, Pageable pageable);
}
