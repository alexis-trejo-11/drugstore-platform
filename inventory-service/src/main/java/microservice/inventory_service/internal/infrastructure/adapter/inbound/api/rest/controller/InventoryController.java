package microservice.inventory_service.internal.infrastructure.adapter.inbound.api.rest.controller;

import jakarta.validation.Valid;
import libs_kernel.mapper.ResponseMapper;
import libs_kernel.page.PageRequest;
import libs_kernel.page.PageResponse;
import libs_kernel.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import microservice.inventory_service.internal.core.inventory.application.InventoryService;
import microservice.inventory_service.internal.core.inventory.application.cqrs.query.GetInventoryByIdQuery;
import microservice.inventory_service.internal.core.inventory.application.cqrs.query.GetInventoryByMedicineQuery;
import microservice.inventory_service.internal.core.inventory.domain.entity.Inventory;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.internal.infrastructure.adapter.inbound.api.rest.dto.request.CreateInventoryRequest;
import microservice.inventory_service.internal.infrastructure.adapter.inbound.api.rest.dto.response.InventoryResponse;
import microservice.inventory_service.internal.core.stock.application.query.GetLowStockInventoriesQuery;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/inventories")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;
    private final ResponseMapper<InventoryResponse, Inventory> responseMapper;

    @GetMapping("/{id}")
    public ResponseWrapper<InventoryResponse> getInventoryById(@PathVariable String id) {
        var query = GetInventoryByIdQuery.of(id);
        var inventory = inventoryService.getInventoryById(query);

        var inventoryResponse = responseMapper.toResponse(inventory);
        return ResponseWrapper.found(inventoryResponse, "Inventory");
    }

    @GetMapping("/medicine/{medicineId}")
    public ResponseWrapper<InventoryResponse> getInventoryByMedicineId(@PathVariable String medicineId) {
        var query = GetInventoryByMedicineQuery.of(medicineId);
        var inventory = inventoryService.getInventoryByMedicine(query);

        var inventoryResponse = responseMapper.toResponse(inventory);
        return ResponseWrapper.found(inventoryResponse, "Inventory");
    }

    @GetMapping("low-stock")
    public ResponseWrapper<PageResponse<InventoryResponse>> getLowStockInventories(@Valid @ModelAttribute PageRequest pageRequest) {
        var query = new GetLowStockInventoriesQuery(pageRequest.toPageable());
        Page<Inventory> queryPage = inventoryService.getLowStockInventories(query);

        PageResponse<InventoryResponse> inventoryPage  = responseMapper.toResponsePage(queryPage);
        return ResponseWrapper.found(inventoryPage, "Low Stock Inventories");
    }


    @PostMapping
    public ResponseEntity<ResponseWrapper<InventoryId>> createInventory(@Valid @RequestBody CreateInventoryRequest request) {
        InventoryId id = inventoryService.createInventory(request.toCommand());

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseWrapper.created(id, "Inventory"));
    }
}
