package microservice.inventory_service.internal.core.inventory.application;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.internal.core.alert.domain.entity.InventoryAlert;
import microservice.inventory_service.internal.core.alert.domain.port.InventoryAlertOutputPort;
import microservice.inventory_service.internal.core.alert.domain.service.InventoryAlertDomainService;
import microservice.inventory_service.internal.core.batch.application.handler.MarkBatchAsExpiredCommandHandler;
import microservice.inventory_service.internal.core.batch.domain.entity.InventoryBatch;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryBatchService {
    private final InventoryAlertDomainService alertDomainService;
    private final InventoryAlertOutputPort alertRepository;

    @Transactional
    public void processExpiredBatch(MarkBatchAsExpiredCommandHandler command, InventoryBatch batch) {
        batch.markAsExpired();
        InventoryAlert expiredAlert = alertDomainService.createExpiredBatchAlert(batch);
        alertRepository.save(expiredAlert);
    }
}
