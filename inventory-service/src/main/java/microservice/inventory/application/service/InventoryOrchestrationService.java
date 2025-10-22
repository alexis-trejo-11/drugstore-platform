package microservice.inventory.application.service;

import lombok.RequiredArgsConstructor;
import microservice.inventory.application.command.AddInventoryBatchCommand;
import microservice.inventory.application.command.AdjustInventoryCommand;
import microservice.inventory.application.command.CreateInventoryCommand;
import microservice.inventory.application.command.MarkBatchAsExpiredCommand;
import microservice.inventory.application.handler.command.AddInventoryBatchCommandHandler;
import microservice.inventory.application.handler.command.AdjustInventoryCommandHandler;
import microservice.inventory.application.handler.command.CreateInventoryCommandHandler;
import microservice.inventory.domain.entity.valueobject.id.InventoryId;
import microservice.inventory.domain.service.BatchManagementService;
import microservice.inventory.domain.service.InventoryAlertDomainService;
import microservice.inventory.domain.service.InventoryAlertService;
import microservice.inventory.domain.service.InventoryStatusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryOrchestrationService {
    private final CreateInventoryCommandHandler createInventoryHandler;
    private final AddInventoryBatchCommandHandler addBatchHandler;
    private final AdjustInventoryCommandHandler adjustInventoryHandler;
    private final InventoryStatusService statusService;
    private final InventoryAlertDomainService alertService;
    private final BatchManagementService batchManagementService;

    @Transactional
    public InventoryId createInventoryWithInitialBatch(CreateInventoryCommand inventoryCommand, AddInventoryBatchCommand batchCommand) {
        InventoryId inventoryId = createInventoryHandler.handle(inventoryCommand);

        AddInventoryBatchCommand batchWithInventorCommand = batchCommand.setInventoryId(inventoryId);
        addBatchHandler.handle(batchWithInventorCommand);

        return inventoryId;
    }

    @Transactional
    public void processInventoryAdjustmentWithAlerts(AdjustInventoryCommand command, Inventory inventory) {
        adjustInventoryHandler.handle(command);

        statusService.updateInventoryStatus(inventory);

        if (statusService.needsReorder(inventory)) {
            alertService.checkAndCreateLowStockAlert(inventory);
        }

        if (inventory.getAvailableQuantity() <= 0) {
            alertService.checkAndCreateOutOfStockAlert(inventory);
        }
    }

    @Transactional
    public void processExpiredBatch(MarkBatchAsExpiredCommand command, InventoryBatch batch) {
        batchManagementService.markBatchAsExpired(batch);
        alertService.createExpiredBatchAlert(batch);
    }
}

