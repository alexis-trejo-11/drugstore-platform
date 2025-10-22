package microservice.inventory.application.command;

import microservice.inventory.domain.entity.valueobject.id.BatchId;
import microservice.inventory.domain.entity.valueobject.id.UserId;

public record MarkBatchAsExpiredCommand(BatchId batchId, UserId performedBy) {
}
