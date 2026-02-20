package io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service;

import lombok.RequiredArgsConstructor;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.report.BatchExpirationReport;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.Inventory;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.entity.InventoryBatch;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.port.InventoryRepository;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.movement.domain.InventoryMovement;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.movement.domain.port.InventoryMovementRepository;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.movement.domain.valueobject.MovementType;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.port.output.InventoryBatchRepository;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.report.InventoryMovementReport;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.report.InventoryStockReport;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.entity.StockReservation;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.port.output.StockReservationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryReportingService {
    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository movementRepository;
    private final InventoryBatchRepository batchRepository;
    private final StockReservationRepository reservationRepository;
    
    @Transactional(readOnly = true)
    public List<Inventory> getInventoriesNeedingReorder() {
        List<Inventory> allInventories = inventoryRepository.findAll();
        
        return allInventories.stream()
            .filter(Inventory::needsReorder)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public InventoryStockReport generateStockReport(InventoryId inventoryId) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
            .orElseThrow(() -> new IllegalStateException("Inventory not found"));
        
        List<InventoryBatch> batches = batchRepository.findByInventoryId(inventoryId, false);
        List<StockReservation> activeReservations = reservationRepository.findActiveReservationsByInventoryId(inventoryId);
        
        return InventoryStockReport.builder()
            .inventoryId(inventoryId)
            .productId(inventory.getProductId())
            .totalQuantity(inventory.getTotalQuantity())
            .availableQuantity(inventory.getAvailableQuantity())
            .reservedQuantity(inventory.getReservedQuantity())
            .reorderLevel(inventory.getReorderLevel())
            .status(inventory.getStatus())
            .numberOfBatches(batches.size())
            .activeBatches((int) batches.stream().filter(InventoryBatch::isActive).count())
            .activeReservations(activeReservations.size())
            .needsReorder(inventory.needsReorder())
            .recommendedOrderQuantity(inventory.calculateReorderQuantity())
            .build();
    }
    
    @Transactional(readOnly = true)
    public InventoryMovementReport generateMovementReport(InventoryId inventoryId, LocalDateTime startDate, LocalDateTime endDate) {
        var maxPageSize = Integer.MAX_VALUE;
        var maxPlaceHolderPage  = Pageable.ofSize(maxPageSize);
        Page<InventoryMovement> movements = movementRepository.findByInventoryIdAndDateRange(inventoryId, startDate, endDate, maxPlaceHolderPage);
        
        int totalReceipts = movements.stream()
            .filter(m -> m.getMovementType() == MovementType.RECEIPT)
            .mapToInt(InventoryMovement::getQuantity)
            .sum();
        
        int totalSales = movements.stream()
            .filter(m -> m.getMovementType() == MovementType.SALE)
            .mapToInt(InventoryMovement::getQuantity)
            .sum();
        
        int totalAdjustments = movements.stream()
            .filter(m -> m.getMovementType() == MovementType.ADJUSTMENT)
            .mapToInt(InventoryMovement::getQuantity)
            .sum();
        
        int totalDamage = movements.stream()
            .filter(m -> m.getMovementType() == MovementType.DAMAGE)
            .mapToInt(InventoryMovement::getQuantity)
            .sum();
        
        int totalExpiration = movements.stream()
            .filter(m -> m.getMovementType() == MovementType.EXPIRATION)
            .mapToInt(InventoryMovement::getQuantity)
            .sum();
        
        return InventoryMovementReport.builder()
            .inventoryId(inventoryId)
            .startDate(startDate)
            .endDate(endDate)
            .totalMovements((int) movements.getTotalElements())
            .totalReceipts(totalReceipts)
            .totalSales(totalSales)
            .totalAdjustments(totalAdjustments)
            .totalDamage(totalDamage)
            .totalExpiration(totalExpiration)
            .movements(movements.stream().toList())
            .build();
    }
    
    @Transactional(readOnly = true)
    public BatchExpirationReport generateBatchExpirationReport(Integer daysThreshold) {
        LocalDateTime expirationThreshold = LocalDateTime.now().plusDays(daysThreshold);
        List<InventoryBatch> expiringBatches = batchRepository.findExpiringBefore(expirationThreshold);
        List<InventoryBatch> expiredBatches = batchRepository.findExpiredBatches();
        
        return BatchExpirationReport.builder()
            .reportDate(LocalDateTime.now())
            .daysThreshold(daysThreshold)
            .expiringBatchesCount(expiringBatches.size())
            .expiredBatchesCount(expiredBatches.size())
            .expiringBatches(expiringBatches)
            .expiredBatches(expiredBatches)
            .build();
    }
}