package microservice.inventory_service.inventory.application.service;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.application.service.report.BatchExpirationReport;
import microservice.inventory_service.inventory.application.service.report.InventoryMovementReport;
import microservice.inventory_service.inventory.application.service.report.InventoryStockReport;
import microservice.inventory_service.inventory.domain.entity.Inventory;
import microservice.inventory_service.inventory.domain.entity.InventoryBatch;
import microservice.inventory_service.inventory.domain.entity.InventoryMovement;
import microservice.inventory_service.inventory.domain.entity.enums.MovementType;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.InventoryId;
import microservice.inventory_service.inventory.domain.port.output.InventoryBatchRepository;
import microservice.inventory_service.inventory.domain.port.output.InventoryMovementRepository;
import microservice.inventory_service.inventory.domain.port.output.InventoryRepository;
import microservice.inventory_service.stock.domain.StockReservation;
import microservice.inventory_service.stock.domain.port.output.StockReservationRepository;
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
        
        List<InventoryBatch> batches = batchRepository.findByInventoryId(inventoryId);
        List<StockReservation> activeReservations = reservationRepository.findActiveReservationsByInventoryId(inventoryId);
        
        return InventoryStockReport.builder()
            .inventoryId(inventoryId)
            .medicineId(inventory.getMedicineId())
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
        List<InventoryMovement> movements = movementRepository.findByInventoryIdAndDateRange(inventoryId, startDate, endDate);
        
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
            .totalMovements(movements.size())
            .totalReceipts(totalReceipts)
            .totalSales(totalSales)
            .totalAdjustments(totalAdjustments)
            .totalDamage(totalDamage)
            .totalExpiration(totalExpiration)
            .movements(movements)
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