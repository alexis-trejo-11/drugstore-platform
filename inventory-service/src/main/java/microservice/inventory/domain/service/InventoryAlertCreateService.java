package microservice.inventory.domain.service;

import lombok.RequiredArgsConstructor;
import microservice.inventory.domain.entity.enums.AlertSeverity;
import microservice.inventory.domain.entity.enums.AlertStatus;
import microservice.inventory.domain.entity.enums.AlertType;
import microservice.inventory.domain.entity.valueobject.id.AlertId;
import microservice.inventory.domain.port.output.InventoryAlertRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InventoryAlertCreateService {
    private final InventoryAlertRepository alertRepository;

    public void checkAndCreateLowStockAlert(Inventory inventory) {
        if (inventory.getAvailableQuantity() <= inventory.getReorderLevel()) {
            InventoryAlert alert = InventoryAlert.builder()
                .id(AlertId.generate())
                .inventoryId(inventory.getId())
                .alertType(AlertType.LOW_STOCK)
                .severity(AlertSeverity.HIGH)
                .message(String.format("Low stock alert for inventory %s. Current: %d, Reorder Level: %d",
                    inventory.getId(), inventory.getAvailableQuantity(), inventory.getReorderLevel()))
                .status(AlertStatus.ACTIVE)
                .triggeredAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

            alertRepository.save(alert);
        }
    }

    public void checkAndCreateOutOfStockAlert(Inventory inventory) {
        if (inventory.getAvailableQuantity() <= 0) {
            InventoryAlert alert = InventoryAlert.builder()
                .id(AlertId.generate())
                .inventoryId(inventory.getId())
                .alertType(AlertType.OUT_OF_STOCK)
                .severity(AlertSeverity.CRITICAL)
                .message(String.format("Out of stock for inventory %s", inventory.getId()))
                .status(AlertStatus.ACTIVE)
                .triggeredAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

            alertRepository.save(alert);
        }
    }

    public void createExpiringBatchAlert(InventoryBatch batch) {
        InventoryAlert alert = InventoryAlert.builder()
            .id(AlertId.generate())
            .inventoryId(batch.getInventoryId())
            .alertType(AlertType.EXPIRING_SOON)
            .severity(AlertSeverity.MEDIUM)
            .message(String.format("Batch %s is expiring soon. Expiration date: %s",
                batch.getBatchNumber(), batch.getExpirationDate()))
            .status(AlertStatus.ACTIVE)
            .triggeredAt(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .build();

        alertRepository.save(alert);
    }

    public void createExpiredBatchAlert(InventoryBatch batch) {
        InventoryAlert alert = InventoryAlert.builder()
            .id(AlertId.generate())
            .inventoryId(batch.getInventoryId())
            .alertType(AlertType.EXPIRED)
            .severity(AlertSeverity.HIGH)
            .message(String.format("Batch %s has expired. Expiration date: %s",
                batch.getBatchNumber(), batch.getExpirationDate()))
            .status(AlertStatus.ACTIVE)
            .triggeredAt(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .build();

        alertRepository.save(alert);
    }
}