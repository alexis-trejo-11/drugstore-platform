package microservice.inventory_service.inventory.adapter.outbound.persistence.repository.jpa;

import microservice.inventory_service.inventory.core.batch.domain.entity.valueobject.BatchStatus;
import microservice.inventory_service.inventory.adapter.outbound.persistence.model.InventoryBatchEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JpaInventoryBatchRepository extends JpaRepository<InventoryBatchEntity, String> {

    Page<InventoryBatchEntity> findByInventoryIdAndStatus(String inventoryId, Pageable pageable, BatchStatus status);


    Page<InventoryBatchEntity> findByStatus(BatchStatus status, Pageable pageable);


    @Query("SELECT b FROM InventoryBatchEntity b WHERE b.expirationDate < :date")
    Page<InventoryBatchEntity> findByExpirationDateBefore(@Param("date") LocalDateTime date, Pageable pageable);

    @Query("SELECT b FROM InventoryBatchEntity b WHERE b.expirationDate < :date")
    List<InventoryBatchEntity> findByExpirationDateBefore(@Param("date") LocalDateTime date);

    @Query("SELECT b FROM InventoryBatchEntity b WHERE b.expirationDate < CURRENT_TIMESTAMP AND b.status = :status")
    List<InventoryBatchEntity> findActive(@Param("status") BatchStatus status);

    @Query("SELECT b FROM InventoryBatchEntity b WHERE b.inventoryId = :inventoryId AND b.status = :status")
    List<InventoryBatchEntity> findByInventoryIdAndStatus(@Param("inventoryId") String inventoryId, @Param("status") BatchStatus status);
}
