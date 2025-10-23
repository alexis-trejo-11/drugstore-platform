package microservice.inventory_service.inventory.application.service;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.application.command.AddInventoryBatchCommand;
import microservice.inventory_service.inventory.application.command.AdjustInventoryCommand;
import microservice.inventory_service.inventory.application.command.CreateInventoryCommand;
import microservice.inventory_service.inventory.application.command.MarkBatchAsExpiredCommand;
import microservice.inventory_service.inventory.application.handler.command.AddInventoryBatchCommandHandler;
import microservice.inventory_service.inventory.application.handler.command.AdjustInventoryCommandHandler;
import microservice.inventory_service.inventory.application.handler.command.CreateInventoryCommandHandler;
import microservice.inventory_service.inventory.domain.entity.Inventory;
import microservice.inventory_service.inventory.domain.entity.InventoryAlert;
import microservice.inventory_service.inventory.domain.entity.InventoryBatch;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.AdjustmentId;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.InventoryId;
import microservice.inventory_service.inventory.domain.port.output.InventoryAlertRepository;
import microservice.inventory_service.inventory.domain.service.InventoryAlertDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryOrchestrationService {
    private final CreateInventoryCommandHandler createInventoryHandler;
    private final AddInventoryBatchCommandHandler addBatchHandler;
    private final AdjustInventoryCommandHandler adjustInventoryHandler;
    private final InventoryAlertDomainService alertDomainService;
    private final InventoryAlertRepository alertRepository;


    @Transactional
    public InventoryId createInventory(CreateInventoryCommand inventoryCommand) {
         return createInventoryHandler.handle(inventoryCommand);
    }

    @Transactional
    public AdjustmentId processInventoryAdjustmentWithAlerts(AdjustInventoryCommand command, Inventory inventory) {
        AdjustmentId adjustmentId = adjustInventoryHandler.handle(command);

        if (inventory.isLowStock()) {
            InventoryAlert lowStockAlert = alertDomainService.createLowStockAlert(inventory);
            alertRepository.save(lowStockAlert);
        }

        if (inventory.isOutOfStock()) {
            InventoryAlert outOfStockAlert = alertDomainService.createOutOfStockAlert(inventory);
            alertRepository.save(outOfStockAlert);
        }

        if (inventory.needsReorder()) {
            InventoryAlert reorderAlert = alertDomainService.createReorderNeededAlert(inventory);
            alertRepository.save(reorderAlert);
        }

        return adjustmentId;
    }

    @Transactional
    public void processExpiredBatch(MarkBatchAsExpiredCommand command, InventoryBatch batch) {
        batch.markAsExpired();
        InventoryAlert expiredAlert = alertDomainService.createExpiredBatchAlert(batch);
        alertRepository.save(expiredAlert);
    }
}

