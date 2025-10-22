package microservice.inventory_service.inventory.domain.service;

import microservice.inventory_service.inventory.domain.entity.InventoryBatch;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BatchExpirationService {
    private static final int DEFAULT_WARNING_DAYS = 30;

    public List<InventoryBatch> findExpiredBatches(List<InventoryBatch> batches) {
        return batches.stream()
                .filter(InventoryBatch::isExpired)
                .toList();
    }

    public List<InventoryBatch> findExpiringSoonBatches(List<InventoryBatch> batches, Integer warningDays) {
        int days = warningDays != null ? warningDays : DEFAULT_WARNING_DAYS;
        return batches.stream()
                .filter(batch -> batch.isExpiringSoon(days))
                .toList();
    }

    public void processExpiredBatches(List<InventoryBatch> expiredBatches) {
        expiredBatches.forEach(InventoryBatch::markAsExpired);
    }
}
