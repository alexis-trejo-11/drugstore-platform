package io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.port.input;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.application.command.RegisterInventoryBatchCommand;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.application.command.MarkBatchAsExpiredCommand;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.application.query.GetExpiringBatchesQuery;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.entity.InventoryBatch;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.entity.valueobject.BatchId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.query.GetInventoryBatchesQuery;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.UserId;
import org.springframework.data.domain.Page;

public interface BatchUseCase {
    BatchId addBatch(RegisterInventoryBatchCommand command);
    Page<InventoryBatch> getInventoryBatches(GetInventoryBatchesQuery query);
    Page<InventoryBatch> getExpiringBatches(GetExpiringBatchesQuery query);
    void markBatchAsExpired(MarkBatchAsExpiredCommand command);
    void markBatchAsDamaged(BatchId batchId, UserId performedBy);
    void quarantineBatch(BatchId batchId, UserId performedBy);
}