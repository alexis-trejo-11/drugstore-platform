package microservice.inventory_service.inventory.core.batch.port.input;

import microservice.inventory_service.inventory.core.batch.application.command.RegisterInventoryBatchCommand;
import microservice.inventory_service.inventory.core.batch.application.command.MarkBatchAsExpiredCommand;
import microservice.inventory_service.inventory.core.batch.application.query.GetExpiringBatchesQuery;
import microservice.inventory_service.inventory.core.batch.domain.entity.InventoryBatch;
import microservice.inventory_service.inventory.core.batch.domain.entity.valueobject.BatchId;
import microservice.inventory_service.inventory.core.inventory.application.cqrs.query.GetInventoryBatchesQuery;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.UserId;

import java.util.List;

public interface BatchUseCase {
    BatchId addBatch(RegisterInventoryBatchCommand command);
    List<InventoryBatch> getInventoryBatches(GetInventoryBatchesQuery query);
    List<InventoryBatch> getExpiringBatches(GetExpiringBatchesQuery query);
    void markBatchAsExpired(MarkBatchAsExpiredCommand command);
    void markBatchAsDamaged(BatchId batchId, UserId performedBy);
    void quarantineBatch(BatchId batchId, UserId performedBy);
}