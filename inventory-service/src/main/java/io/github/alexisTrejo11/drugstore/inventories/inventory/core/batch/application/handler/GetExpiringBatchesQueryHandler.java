package io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.application.handler;

import lombok.RequiredArgsConstructor;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.application.query.GetExpiringBatchesQuery;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.entity.InventoryBatch;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.port.output.InventoryBatchRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class GetExpiringBatchesQueryHandler {
    private final InventoryBatchRepository batchRepository;
    
    @Transactional(readOnly = true)
    public Page<InventoryBatch> handle(GetExpiringBatchesQuery query) {
        LocalDateTime expirationThreshold;
        
        if (query.expirationDate().isPresent()) {
            expirationThreshold = query.expirationDate().get();
        } else if (query.daysThreshold().isPresent()) {
            expirationThreshold = LocalDateTime.now().plusDays(query.daysThreshold().get());
        } else {
            expirationThreshold = LocalDateTime.now().plusDays(30);
        }
        
        return batchRepository.findExpiringBefore(expirationThreshold, query.pageRequest().toPageable());
    }
}