package microservice.inventory_service.internal.infrastructure.adapter.inbound.api.rest.controller;

import jakarta.validation.Valid;
import libs_kernel.mapper.ResponseMapper;
import libs_kernel.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import microservice.inventory_service.internal.core.inventory.application.cqrs.query.GetInventoryMovementsQuery;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.AdjustmentId;
import microservice.inventory_service.internal.core.movement.domain.InventoryMovement;
import microservice.inventory_service.internal.core.stock.port.input.StockMovementUseCase;
import microservice.inventory_service.internal.infrastructure.adapter.inbound.api.rest.dto.request.AdjustInventoryRequest;
import microservice.inventory_service.internal.infrastructure.adapter.inbound.api.rest.dto.request.TransferInventoryRequest;
import microservice.inventory_service.internal.infrastructure.adapter.inbound.api.rest.dto.response.MovementResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2/inventories")
@RequiredArgsConstructor
public class InventoryStockMovementController {
    private final StockMovementUseCase stockMovementUseCase;
    private final ResponseMapper<MovementResponse, InventoryMovement> responseMapper;
    
    @PostMapping("/{inventoryId}/stocks/movements/adjust")
    public ResponseEntity<ResponseWrapper<AdjustmentId>> adjustInventory(
            @PathVariable String inventoryId,
            @Valid @RequestBody AdjustInventoryRequest request) {

        AdjustmentId adjustmentId = stockMovementUseCase.adjustInventory(request.toCommand(inventoryId));
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ResponseWrapper.created(adjustmentId, "Stock Adjustment"));
    }
    
    @PostMapping("/{sourceInventoryId}/stocks/movements/transfer")
    public ResponseWrapper<Void> transferInventory(
            @PathVariable String sourceInventoryId,
            @Valid @RequestBody TransferInventoryRequest request) {
        stockMovementUseCase.transferInventory(request.toCommand(sourceInventoryId));
        
        return ResponseWrapper.success("Inventory transferred successfully");
    }
    
    @GetMapping("/{inventoryId}/stocks/movements")
    public ResponseWrapper<List<MovementResponse>> getInventoryMovements(
            @PathVariable String inventoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        var query = GetInventoryMovementsQuery.of(inventoryId,startDate, endDate);
        List<InventoryMovement> movements = stockMovementUseCase.getInventoryMovements(query);

        List<MovementResponse> movementResponses = responseMapper.toResponses(movements);
        return ResponseWrapper.found(movementResponses, "Inventory Movements");
    }
}