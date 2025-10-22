package microservice.inventory_service.inventory.infrastructure.adapter.outbound.persistence.repository;

import microservice.inventory_service.inventory.domain.entity.enums.BatchStatus;
import microservice.inventory_service.inventory.infrastructure.adapter.outbound.persistence.model.InventoryBatchEntity;
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

    @Query("SELECT b FROM InventoryBatchEntity b WHERE b.expirationDate < CURRENT_TIMESTAMP AND b.status = microservice.inventory_service.inventory.domain.entity.enums.BatchStatus.EXPIRED")
    List<InventoryBatchEntity> findExpiredBatches();

    @Query("SELECT b FROM InventoryBatchEntity b WHERE b.inventoryId = :inventoryId AND b.status = microservice.inventory_service.inventory.domain.entity.enums.BatchStatus.ACTIVE")
    List<InventoryBatchEntity> findActiveByInventoryId(@Param("inventoryId") String inventoryId);
}
