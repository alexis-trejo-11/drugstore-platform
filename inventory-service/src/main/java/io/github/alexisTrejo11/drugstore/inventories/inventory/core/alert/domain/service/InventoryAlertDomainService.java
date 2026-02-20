package io.github.alexisTrejo11.drugstore.inventories.inventory.core.alert.domain.service;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.Inventory;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.alert.domain.entity.InventoryAlert;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.entity.InventoryBatch;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.alert.domain.entity.valueobject.AlertSeverity;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.alert.domain.entity.valueobject.AlertType;
import org.springframework.stereotype.Service;

@Service
public class InventoryAlertDomainService {
    public InventoryAlert createLowStockAlert(Inventory inventory) {
        String message = String.format(
                "Low stock alert for inventory %s. Current: %d, Reorder Level: %d",
                inventory.getId(), inventory.getAvailableQuantity(), inventory.getReorderLevel()
        );
        return InventoryAlert.create(inventory.getId(), AlertType.LOW_STOCK, AlertSeverity.HIGH, message);
    }

    public InventoryAlert createOutOfStockAlert(Inventory inventory) {
        String message = String.format("Out of stock for inventory %s", inventory.getId());
        return InventoryAlert.create(inventory.getId(), AlertType.OUT_OF_STOCK, AlertSeverity.CRITICAL, message);
    }

    public InventoryAlert createExpiringBatchAlert(InventoryBatch batch) {
        String message = String.format(
                "Batch %s is expiring soon. Expiration date: %s ",
                batch.getBatchNumber(), batch.getExpirationDate()
        );
        return InventoryAlert.create(batch.getInventoryId(), AlertType.EXPIRING_SOON, AlertSeverity.MEDIUM, message);
    }

    public InventoryAlert createExpiredBatchAlert(InventoryBatch batch) {
        String message = String.format(
                "Batch %s has expired. Expiration date: %s",
                batch.getBatchNumber(), batch.getExpirationDate()
        );
        return InventoryAlert.create(batch.getInventoryId(), AlertType.EXPIRED, AlertSeverity.HIGH, message);
    }

    public InventoryAlert createOverstockAlert(Inventory inventory) {
        String message = String.format(
                "Overstock alert for inventory %s. Current: %d, Maximum: %d",
                inventory.getId(), inventory.getTotalQuantity(), inventory.getMaximumStockLevel()
        );
        return InventoryAlert.create(inventory.getId(), AlertType.OVERSTOCK, AlertSeverity.LOW, message);
    }

    public InventoryAlert createReorderNeededAlert(Inventory inventory) {
        String message = String.format(
                "Reorder needed for inventory %s. Recommended quantity: %d",
                inventory.getId(), inventory.calculateReorderQuantity()
        );
        return InventoryAlert.create(inventory.getId(), AlertType.REORDER_NEEDED, AlertSeverity.MEDIUM, message);
    }
}
