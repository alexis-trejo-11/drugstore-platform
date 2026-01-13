package microservice.inventory_service.inventory.adapter.inbound.api.rest.controller;

import jakarta.validation.Valid;
import libs_kernel.mapper.ResponseMapper;
import libs_kernel.page.PageRequest;
import libs_kernel.page.PageResponse;
import libs_kernel.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.core.batch.application.command.MarkBatchAsExpiredCommand;
import microservice.inventory_service.inventory.core.batch.application.query.GetExpiringBatchesQuery;
import microservice.inventory_service.inventory.core.batch.domain.entity.InventoryBatch;
import microservice.inventory_service.inventory.core.batch.domain.entity.valueobject.BatchId;
import microservice.inventory_service.inventory.core.batch.port.input.BatchUseCase;
import microservice.inventory_service.inventory.core.inventory.service.cqrs.query.GetInventoryBatchesQuery;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.UserId;
import microservice.inventory_service.inventory.adapter.inbound.api.rest.dto.request.AddInventoryBatchRequest;
import microservice.inventory_service.inventory.adapter.inbound.api.rest.dto.response.BatchResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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
    public ResponseWrapper<PageResponse<BatchResponse>> getInventoryWithBatches(
            @PathVariable String inventoryId,
            @RequestParam(required = false, defaultValue = "false") Boolean activeOnly,
            @Valid @ModelAttribute PageRequest pageRequest) {

        var query = new GetInventoryBatchesQuery(
                InventoryId.of(inventoryId),
                activeOnly, pageRequest
                .toPageable()
        );
        Page<InventoryBatch> batches = batchUseCase.getInventoryBatches(query);

        PageResponse<BatchResponse> batchResponses = responseMapper.toResponsePage(batches);
        return ResponseWrapper.found(batchResponses, "Batches");
    }

    // TODO: Parse DateTime
    @GetMapping("/batches/expiring")
    public ResponseWrapper<PageResponse<BatchResponse>> getExpiringBatches(
            @RequestParam(required = false, defaultValue = "30") Integer daysThreshold,
            @RequestParam(required = false) LocalDateTime expirationDate,
            @Valid @ModelAttribute PageRequest pageRequest) {

        var query = GetExpiringBatchesQuery.of(expirationDate, daysThreshold, pageRequest);
        Page<InventoryBatch> batchPage = batchUseCase.getExpiringBatches(query);

        PageResponse<BatchResponse> batchResponse = responseMapper.toResponsePage(batchPage);
        return ResponseWrapper.found(batchResponse, "Expiring Batches");
    }

    @PatchMapping("/batches/{batchId}/mark-expired")
    public ResponseWrapper<Void> markBatchAsExpired(
            @PathVariable String batchId,
            @RequestParam String performedBy) {

        var command = MarkBatchAsExpiredCommand.of(batchId, performedBy);
        batchUseCase.markBatchAsExpired(command);

        return ResponseWrapper.updated(null, "Batch marked as expired");
    }

    @PatchMapping("/batches/{batchId}/mark-damaged")
    public ResponseWrapper<Void> markBatchAsDamaged(
            @PathVariable String batchId,
            @RequestParam String performedBy) {

        batchUseCase.markBatchAsDamaged(BatchId.of(batchId), UserId.of(performedBy));
        return ResponseWrapper.updated(null, "Batch marked as damaged");
    }

    @PatchMapping("/batches/{batchId}/quarantine")
    public ResponseWrapper<Void> quarantineBatch(
            @PathVariable String batchId,
            @RequestParam String performedBy) {

        batchUseCase.quarantineBatch(BatchId.of(batchId), UserId.of(performedBy));
        return ResponseWrapper.updated(null, "Batch quarantined");
    }
}