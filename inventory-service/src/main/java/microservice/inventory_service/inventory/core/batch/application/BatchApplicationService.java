package microservice.inventory_service.inventory.core.batch.application;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.core.batch.application.command.RegisterInventoryBatchCommand;
import microservice.inventory_service.inventory.core.batch.application.command.MarkBatchAsExpiredCommand;
import microservice.inventory_service.inventory.core.batch.application.handler.AddInventoryBatchCommandHandler;
import microservice.inventory_service.inventory.core.batch.application.handler.GetExpiringBatchesQueryHandler;
import microservice.inventory_service.inventory.core.batch.application.handler.MarkBatchAsExpiredCommandHandler;
import microservice.inventory_service.inventory.core.batch.application.query.GetExpiringBatchesQuery;
import microservice.inventory_service.inventory.core.batch.application.query.GetInventoryBatchesQueryHandler;
import microservice.inventory_service.inventory.core.batch.domain.entity.InventoryBatch;
import microservice.inventory_service.inventory.core.batch.domain.entity.valueobject.BatchId;
import microservice.inventory_service.inventory.core.batch.port.input.BatchUseCase;
import microservice.inventory_service.inventory.core.batch.port.output.InventoryBatchRepository;
import microservice.inventory_service.inventory.core.inventory.service.cqrs.query.GetInventoryBatchesQuery;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.UserId;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public BatchId addBatch(RegisterInventoryBatchCommand command) {
        return addBatchHandler.handle(command);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InventoryBatch> getInventoryBatches(GetInventoryBatchesQuery query) {
        return getBatchesHandler.handle(query);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InventoryBatch> getExpiringBatches(GetExpiringBatchesQuery query) {
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
