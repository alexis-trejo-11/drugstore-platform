package microservice.inventory_service.inventory.application.command;

import microservice.inventory_service.inventory.domain.entity.valueobject.id.BatchId;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.UserId;

public record MarkBatchAsExpiredCommand(BatchId batchId, UserId performedBy) {
}
