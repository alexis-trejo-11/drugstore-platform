package microservice.inventory_service.internal.inventory.core.batch.port.input;

import microservice.inventory_service.internal.inventory.core.batch.application.command.AddInventoryBatchCommand;
import microservice.inventory_service.internal.inventory.core.batch.application.command.MarkBatchAsExpiredCommand;
import microservice.inventory_service.internal.inventory.core.batch.application.query.GetExpiringBatchesQuery;
import microservice.inventory_service.internal.inventory.core.batch.domain.entity.InventoryBatch;
import microservice.inventory_service.internal.inventory.core.batch.domain.entity.valueobject.BatchId;
import microservice.inventory_service.internal.inventory.core.inventory.application.cqrs.query.GetInventoryBatchesQuery;
import microservice.inventory_service.internal.inventory.core.inventory.domain.entity.valueobject.UserId;

import java.util.List;

public interface BatchUseCase {
    BatchId addBatch(AddInventoryBatchCommand command);
    List<InventoryBatch> getInventoryBatches(GetInventoryBatchesQuery query);
    List<InventoryBatch> getExpiringBatches(GetExpiringBatchesQuery query);
    void markBatchAsExpired(MarkBatchAsExpiredCommand command);
    void markBatchAsDamaged(BatchId batchId, UserId performedBy);
    void quarantineBatch(BatchId batchId, UserId performedBy);
}