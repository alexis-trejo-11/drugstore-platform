package microservice.inventory_service.internal.core.batch.application.command;

import microservice.inventory_service.internal.core.batch.domain.entity.valueobject.BatchId;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.UserId;

public record MarkBatchAsExpiredCommand(BatchId batchId, UserId performedBy) {
}
