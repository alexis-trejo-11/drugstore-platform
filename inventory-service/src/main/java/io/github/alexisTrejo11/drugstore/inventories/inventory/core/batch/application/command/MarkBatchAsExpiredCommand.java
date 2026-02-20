package io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.application.command;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.entity.valueobject.BatchId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.UserId;

public record MarkBatchAsExpiredCommand(BatchId batchId, UserId performedBy) {

    public static MarkBatchAsExpiredCommand of(String batchId, String performedBy) {
        return new MarkBatchAsExpiredCommand(BatchId.of(batchId), UserId.of(performedBy));
    }
}
