package microservice.inventory_service.internal.infrastructure.adapter.inbound.api.rest.controller;

import jakarta.validation.Valid;
import libs_kernel.mapper.ResponseMapper;
import libs_kernel.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import microservice.inventory_service.internal.core.batch.application.command.MarkBatchAsExpiredCommand;
import microservice.inventory_service.internal.core.batch.application.query.GetExpiringBatchesQuery;
import microservice.inventory_service.internal.core.batch.domain.entity.InventoryBatch;
import microservice.inventory_service.internal.core.batch.domain.entity.valueobject.BatchId;
import microservice.inventory_service.internal.core.batch.port.input.BatchUseCase;
import microservice.inventory_service.internal.core.inventory.application.cqrs.query.GetInventoryBatchesQuery;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.UserId;
import microservice.inventory_service.internal.infrastructure.adapter.inbound.api.rest.dto.request.AddInventoryBatchRequest;
import microservice.inventory_service.internal.infrastructure.adapter.inbound.api.rest.dto.response.BatchResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v2/inventories")
@RequiredArgsConstructor
public class InventoryBatchController {
    private final BatchUseCase batchUseCase;
    private final ResponseMapper<BatchResponse, InventoryBatch> responseMapper;

    @PostMapping("/{inventoryId}/batches")
    public ResponseEntity<ResponseWrapper<BatchId>> addBatch(@PathVariable String inventoryId,
                                                             @Valid @RequestBody AddInventoryBatchRequest request) {
        BatchId batchId = batchUseCase.addBatch(request.toCommand(inventoryId));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseWrapper.created(batchId, "Batch"));
    }

    @GetMapping("/{inventoryId}/batches")
    public ResponseWrapper<List<BatchResponse>> getInventoryBatches(@PathVariable String inventoryId,
                                                                    @RequestParam(required = false, defaultValue = "false") Boolean activeOnly) {
        var query = new GetInventoryBatchesQuery(InventoryId.of(inventoryId), activeOnly);

        List<InventoryBatch> batches = batchUseCase.getInventoryBatches(query);
        List<BatchResponse> batchResponses = responseMapper.toResponses(batches);

        return ResponseWrapper.found(batchResponses, "Batches");
    }

    // TODO: Add pagination to this endpoint --> Parse DateTime
    @GetMapping("/expiring")
    public ResponseWrapper<List<BatchResponse>> getExpiringBatches(@RequestParam(required = false, defaultValue = "30") Integer daysThreshold,
                                                                   @RequestParam(required = false) LocalDateTime expirationDate
    ) {
        var query = GetExpiringBatchesQuery.of(expirationDate, daysThreshold);
        List<InventoryBatch> batches = batchUseCase.getExpiringBatches(query);

        List<BatchResponse> batchResponses = responseMapper.toResponses(batches);
        return ResponseWrapper.found(batchResponses, "Expiring Batches");
    }

    @PatchMapping("/{batchId}/mark-expired")
    public ResponseWrapper<Void> markBatchAsExpired(
            @PathVariable String batchId,
            @RequestParam String performedBy
    ) {
        var command = MarkBatchAsExpiredCommand.of(batchId, performedBy);
        batchUseCase.markBatchAsExpired(command);

        return ResponseWrapper.updated(null, "Batch marked as expired");
    }

    @PatchMapping("/{batchId}/mark-damaged")
    public ResponseWrapper<Void> markBatchAsDamaged(
            @PathVariable String batchId,
            @RequestParam String performedBy
    ) {
        batchUseCase.markBatchAsDamaged(BatchId.of(batchId), UserId.of(performedBy));

        return ResponseWrapper.updated(null, "Batch marked as damaged");
    }

    @PatchMapping("/{batchId}/quarantine")
    public ResponseWrapper<Void> quarantineBatch(
            @PathVariable String batchId,
            @RequestParam String performedBy
    ) {
        batchUseCase.quarantineBatch(BatchId.of(batchId), UserId.of(performedBy));
        return ResponseWrapper.updated(null, "Batch quarantined");
    }
}