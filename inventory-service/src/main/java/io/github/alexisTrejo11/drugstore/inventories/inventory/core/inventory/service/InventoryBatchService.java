package io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service;

import lombok.RequiredArgsConstructor;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.alert.domain.entity.InventoryAlert;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.alert.domain.port.InventoryAlertRepository;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.alert.domain.service.InventoryAlertDomainService;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.application.handler.MarkBatchAsExpiredCommandHandler;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.entity.InventoryBatch;
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
