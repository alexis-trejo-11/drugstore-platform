package microservice.inventory_service.inventory.adapter.outbound.persistence.repository.jpa;

import microservice.inventory_service.inventory.adapter.outbound.persistence.model.InventoryMovementEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface JpaInventoryMovementRepository extends JpaRepository<InventoryMovementEntity, String> {
    Page<InventoryMovementEntity> findByInventoryId(String inventoryId, Pageable pageable);
    Page<InventoryMovementEntity> findByBatchId(String batchId, Pageable pageable);
    Page<InventoryMovementEntity> findByMovementType(String type, Pageable pageable);
    Page<InventoryMovementEntity> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    Page<InventoryMovementEntity> findAllByInventoryIdAndDateBetween(String inventoryId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
