package microservice.inventory_service.internal.core.batch.application;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.internal.core.batch.application.command.AddInventoryBatchCommand;
import microservice.inventory_service.internal.core.batch.application.command.MarkBatchAsExpiredCommand;
import microservice.inventory_service.internal.core.batch.application.handler.AddInventoryBatchCommandHandler;
import microservice.inventory_service.internal.core.batch.application.handler.GetExpiringBatchesQueryHandler;
import microservice.inventory_service.internal.core.batch.application.handler.MarkBatchAsExpiredCommandHandler;
import microservice.inventory_service.internal.core.batch.application.query.GetExpiringBatchesQuery;
import microservice.inventory_service.internal.core.batch.application.query.GetInventoryBatchesQueryHandler;
import microservice.inventory_service.internal.core.batch.domain.entity.InventoryBatch;
import microservice.inventory_service.internal.core.batch.domain.entity.valueobject.BatchId;
import microservice.inventory_service.internal.core.batch.port.input.BatchUseCase;
import microservice.inventory_service.internal.core.batch.port.output.InventoryBatchRepository;
import microservice.inventory_service.internal.core.inventory.application.cqrs.query.GetInventoryBatchesQuery;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BatchApplicationService implements BatchUseCase {
    private final AddInventoryBatchCommandHandler addBatchHandler;
    private final MarkBatchAsExpiredCommandHandler markExpiredHandler;
    private final GetInventoryBatchesQueryHandler getBatchesHandler;
    private final GetExpiringBatchesQueryHandler getExpiringHandler;
    private final InventoryBatchRepository batchRepository;


    @Override
    @Transactional
    public BatchId addBatch(AddInventoryBatchCommand command) {
        return addBatchHandler.handle(command);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryBatch> getInventoryBatches(GetInventoryBatchesQuery query) {
        return getBatchesHandler.handle(query);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryBatch> getExpiringBatches(GetExpiringBatchesQuery query) {
        return getExpiringHandler.handle(query);
    }

    @Override
    @Transactional
    public void markBatchAsExpired(MarkBatchAsExpiredCommand command) {
        markExpiredHandler.handle(command);
    }

    @Override
    @Transactional
    public void markBatchAsDamaged(BatchId batchId, UserId performedBy) {
        InventoryBatch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new IllegalStateException("Batch not found"));

        batch.markAsDamaged();
        batchRepository.save(batch);
    }

    @Override
    @Transactional
    public void quarantineBatch(BatchId batchId, UserId performedBy) {
        InventoryBatch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new IllegalStateException("Batch not found"));

        batch.quarantine();
        batchRepository.save(batch);
    }
}
