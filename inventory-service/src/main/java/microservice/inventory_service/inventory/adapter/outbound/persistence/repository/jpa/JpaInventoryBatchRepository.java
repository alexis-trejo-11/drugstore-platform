package microservice.inventory_service.inventory.adapter.outbound.persistence.repository.jpa;

import microservice.inventory_service.inventory.core.batch.domain.entity.valueobject.BatchStatus;
import microservice.inventory_service.inventory.adapter.outbound.persistence.model.InventoryBatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JpaInventoryBatchRepository extends JpaRepository<InventoryBatchEntity, String> {
    List<InventoryBatchEntity> findByInventoryId(String inventoryId);

    List<InventoryBatchEntity> findByStatus(BatchStatus status);

    Optional<InventoryBatchEntity> findByBatchNumber(String batchNumber);

    @Query("SELECT b FROM InventoryBatchEntity b WHERE b.expirationDate < :date")
    List<InventoryBatchEntity> findByExpirationDateBefore(@Param("date") LocalDateTime date);

    @Query("SELECT b FROM InventoryBatchEntity b WHERE b.expirationDate < CURRENT_TIMESTAMP AND b.status = microservice.inventory_service.internal.inventory.core.batch.domain.entity.valueobject.BatchStatus.EXPIRED")
    List<InventoryBatchEntity> findExpiredBatches();

    @Query("SELECT b FROM InventoryBatchEntity b WHERE b.inventoryId = :inventoryId AND b.status = microservice.inventory_service.internal.inventory.core.batch.domain.entity.valueobject.BatchStatus.ACTIVE")
    List<InventoryBatchEntity> findActiveByInventoryId(@Param("inventoryId") String inventoryId);
}
