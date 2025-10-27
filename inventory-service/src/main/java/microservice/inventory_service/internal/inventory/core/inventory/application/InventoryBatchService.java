package microservice.inventory_service.internal.inventory.core.inventory.application;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.internal.inventory.core.alert.domain.entity.InventoryAlert;
import microservice.inventory_service.internal.inventory.core.alert.domain.port.InventoryAlertRepository;
import microservice.inventory_service.internal.inventory.core.alert.domain.service.InventoryAlertDomainService;
import microservice.inventory_service.internal.inventory.core.batch.application.handler.MarkBatchAsExpiredCommandHandler;
import microservice.inventory_service.internal.inventory.core.batch.domain.entity.InventoryBatch;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryBatchService {
    private final InventoryAlertDomainService alertDomainService;
    private final InventoryAlertRepository alertRepository;

    @Transactional
    public void processExpiredBatch(MarkBatchAsExpiredCommandHandler command, InventoryBatch batch) {
        batch.markAsExpired();
        InventoryAlert expiredAlert = alertDomainService.createExpiredBatchAlert(batch);
        alertRepository.save(expiredAlert);
    }
}
