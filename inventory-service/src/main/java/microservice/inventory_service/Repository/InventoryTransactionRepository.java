package microservice.inventory_service.Repository;

import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.TransactionType;
import microservice.inventory_service.Model.InventoryTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {
    Page<InventoryTransaction> findBySupplierId(Long supplierId, Pageable pageable);
    Page<InventoryTransaction> findByEmployeeId(Long productId, Pageable pageable);

    @Query("SELECT i FROM InventoryTransaction i WHERE i.transactionDate BETWEEN :startDate AND :endDate")
    Page<InventoryTransaction> findByCreatedAtBetween(
            Pageable pageable,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT it FROM InventoryTransaction it JOIN FETCH it.inventoryItem WHERE it.transactionType = :transactionType AND it.transactionDate BETWEEN :start AND :end")
    List<InventoryTransaction> findByTransactionTypeAndDateRange(@Param("start") LocalDateTime start,
                                                                 @Param("end") LocalDateTime end,
                                                                 @Param("transactionType") TransactionType transactionType);

    @Query("SELECT it FROM InventoryTransaction it JOIN FETCH it.inventoryItem WHERE it.transactionType = :transactionType ORDER BY it.transactionDate DESC")
    Page<InventoryTransaction> findByTransactionTypeSortedByDateDesc(@Param("transactionType") TransactionType transactionType,
                                                                     Pageable pageable);

    @Query("SELECT i FROM InventoryTransaction i ORDER BY i.transactionDate DESC")
    Page<InventoryTransaction> findTransactionOrderByDateDesc(Pageable pageable);

    @Query("SELECT i FROM InventoryTransaction i WHERE i.expirationDate BETWEEN CURRENT_TIMESTAMP AND :requestedDate")
    Page<InventoryTransaction> findTransactionsNearToExpire(
            @Param("requestedDate") LocalDateTime requestedDate,
            Pageable pageable
    );
}
