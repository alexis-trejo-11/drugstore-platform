package microservice.inventory_service.inventory.core.batch.application.command;

import microservice.inventory_service.inventory.core.batch.domain.entity.valueobject.BatchId;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.UserId;

public record MarkBatchAsExpiredCommand(BatchId batchId, UserId performedBy) {

    public static MarkBatchAsExpiredCommand of(String batchId, String performedBy) {
        return new MarkBatchAsExpiredCommand(BatchId.of(batchId), UserId.of(performedBy));
    }
}
