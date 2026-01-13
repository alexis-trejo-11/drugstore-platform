package microservice.inventory_service.inventory.core.inventory.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.inventory_service.inventory.core.alert.domain.port.InventoryAlertRepository;
import microservice.inventory_service.inventory.core.inventory.domain.entity.Inventory;
import microservice.inventory_service.inventory.core.alert.domain.entity.InventoryAlert;
import microservice.inventory_service.inventory.core.batch.domain.entity.InventoryBatch;
import microservice.inventory_service.inventory.core.batch.domain.entity.valueobject.BatchStatus;
import microservice.inventory_service.inventory.core.batch.port.output.InventoryBatchRepository;
import microservice.inventory_service.inventory.core.batch.domain.service.BatchExpirationService;
import microservice.inventory_service.inventory.core.alert.domain.service.InventoryAlertDomainService;
import microservice.inventory_service.inventory.core.inventory.port.InventoryRepository;
import microservice.inventory_service.inventory.core.stock.port.input.StockMovementUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryMonitoringService {
    
    private final InventoryRepository inventoryRepository;
    private final InventoryBatchRepository batchRepository;
    private final InventoryAlertRepository alertRepository;
    private final InventoryAlertDomainService alertDomainService;
    private final BatchExpirationService batchExpirationService;
    private final StockMovementUseCase reservationOrchestrationService;
    
    @Scheduled(cron = "0 0 */6 * * *")
    @Transactional
    public void monitorLowStockLevels() {
        log.info("Starting low stock monitoring");
        
        List<Inventory> lowStockInventories = inventoryRepository.findAllLowStock();
        
        for (Inventory inventory : lowStockInventories) {
            if (inventory.isLowStock()) {
                InventoryAlert alert = alertDomainService.createLowStockAlert(inventory);
                alertRepository.save(alert);
            }
        }
        
        log.info("Completed low stock monitoring. Found {} low stock items", lowStockInventories.size());
    }
    
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void monitorExpiringBatches() {
        log.info("Starting expiring initialBatch monitoring");
        
        List<InventoryBatch> allBatches = batchRepository.findExpiringBefore(LocalDateTime.now().plusDays(30));
        List<InventoryBatch> expiringBatches = batchExpirationService.findExpiringSoonBatches(allBatches, 30);
        
        for (InventoryBatch batch : expiringBatches) {
            InventoryAlert alert = alertDomainService.createExpiringBatchAlert(batch);
            alertRepository.save(alert);
        }
        
        log.info("Completed expiring initialBatch monitoring. Found {} expiring initialBatch", expiringBatches.size());
    }
    
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void processExpiredBatches() {
        log.info("Starting expired initialBatch processing");
        
        List<InventoryBatch> expiredBatches = batchRepository.findExpiredBatches();
        
        for (InventoryBatch batch : expiredBatches) {
            if (batch.isExpired() && batch.getStatus() == BatchStatus.ACTIVE) {
                batch.markAsExpired();
                batchRepository.save(batch);
                
                InventoryAlert alert = alertDomainService.createExpiredBatchAlert(batch);
                alertRepository.save(alert);
            }
        }
        
        log.info("Completed expired initialBatch processing. Processed {} expired initialBatch", expiredBatches.size());
    }
    
    @Scheduled(cron = "0 */15 * * * *")
    @Transactional
    public void releaseExpiredReservations() {
        log.info("Starting expired reservations release");
        reservationOrchestrationService.releaseExpiredReservations();
        log.info("Completed expired reservations release");
    }
    
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void monitorOutOfStockItems() {
        log.info("Starting out of stock monitoring");

        List<Inventory> outOfStockInventories = inventoryRepository.findAllLowStock();
        
        for (Inventory inventory : outOfStockInventories) {
            if (inventory.isOutOfStock()) {
                InventoryAlert alert = alertDomainService.createOutOfStockAlert(inventory);
                alertRepository.save(alert);
            }
        }
        
        log.info("Completed out of stock monitoring. Found {} out of stock items", outOfStockInventories.size());
    }
}