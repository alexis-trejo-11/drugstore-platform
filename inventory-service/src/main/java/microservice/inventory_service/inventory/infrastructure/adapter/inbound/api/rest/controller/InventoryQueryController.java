package microservice.inventory_service.inventory.infrastructure.adapter.inbound.api.rest.controller;

import libs_kernel.mapper.ResponseMapper;
import libs_kernel.page.PageResponse;
import libs_kernel.page.PageableResponse;
import libs_kernel.page.Pagination;
import libs_kernel.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.application.query.GetInventoryByIdQuery;
import microservice.inventory_service.inventory.application.query.GetInventoryByMedicineQuery;
import microservice.inventory_service.inventory.application.service.InventoryQueryService;
import microservice.inventory_service.inventory.domain.entity.Inventory;
import microservice.inventory_service.inventory.infrastructure.adapter.inbound.api.rest.dto.response.InventoryResponse;
import microservice.inventory_service.stock.application.query.GetLowStockInventoriesQuery;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/inventories")
@RequiredArgsConstructor
public class InventoryQueryController {
    private final InventoryQueryService inventoryService;
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
    public ResponseWrapper<PageResponse<InventoryResponse>> getLowStockInventories(@RequestParam(value = "page", defaultValue = "1") int page,
                                                                                   @RequestParam(value = "size", defaultValue = "10") int size) {
        var query = new GetLowStockInventoriesQuery(Pagination.of(page, size));
        var inventoryPage = inventoryService.getLowStockInventories(query);

        var inventoryResponsePageResponse = responseMapper.toResponsePage(inventoryPage);
        return ResponseWrapper.found(inventoryResponsePageResponse, "Low Stock Inventories");
    }
}
