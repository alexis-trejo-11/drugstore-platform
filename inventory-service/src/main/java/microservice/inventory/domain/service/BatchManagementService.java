package microservice.inventory.domain.service;


import microservice.inventory.domain.entity.Inventory;
import microservice.inventory.domain.entity.InventoryBatch;
import microservice.inventory.domain.entity.enums.BatchStatus;
import microservice.inventory.domain.exception.BatchExpiredException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class BatchManagementService {
    private static final int EXPIRATION_WARNING_DAYS = 30;

    private void validateBatchForUse(InventoryBatch batch) {
        if (batch.getStatus() != BatchStatus.ACTIVE) {
            throw new IllegalStateException("Batch is not active");
        }

        if (batch.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new BatchExpiredException("Batch " + batch.getBatchNumber() + " is expired.");
        }

        if (batch.getAvailableQuantity() <= 0) {
            throw new IllegalStateException("Batch has no available quantity");
        }
    }

    public boolean isBatchExpiringSoon(InventoryBatch batch) {
        long daysUntilExpiration = ChronoUnit.DAYS.between(
                LocalDateTime.now(),
                batch.getExpirationDate()
        );
        return daysUntilExpiration <= EXPIRATION_WARNING_DAYS && daysUntilExpiration > 0;
    }

    public boolean isBatchExpired(InventoryBatch batch) {
        return batch.getExpirationDate().isBefore(LocalDateTime.now());
    }
}
