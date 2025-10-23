package microservice.inventory_service.inventory.infrastructure.adapter.inbound.api.rest.controller;

import jakarta.validation.Valid;
import libs_kernel.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.application.service.InventoryOrchestrationService;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.InventoryId;
import microservice.inventory_service.inventory.infrastructure.adapter.inbound.api.rest.dto.request.CreateInventoryRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/inventories")
@RequiredArgsConstructor
public class InventoryCommandController {
    private final InventoryOrchestrationService inventoryService;

    @PostMapping
    public ResponseEntity<ResponseWrapper<InventoryId>> createInventory(@Valid @RequestBody CreateInventoryRequest request) {
        InventoryId id = inventoryService.createInventory(request.toCommand());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseWrapper.created(id, "Inventory"));
    }
}
