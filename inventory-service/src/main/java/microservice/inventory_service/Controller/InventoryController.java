package microservice.inventory_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryItemDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryItemInsertDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import jakarta.validation.Valid;
import microservice.inventory_service.Service.InventoryService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/drugstore/inventories")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // Data Validation Handled on Global Exception Handler
    @PostMapping("/create")
    public ResponseEntity<ResponseWrapper<Void>> createInventoryItem(@Valid @RequestBody InventoryItemInsertDTO inventoryItemInsertDTO,
                                                                 @RequestParam Long employeeId) {
        Result<Void> valdiationResult = inventoryService.validateDataToCreateInventory(inventoryItemInsertDTO.getProductId());
        if (!valdiationResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseWrapper.badRequest(valdiationResult.getErrorMessage()));
        }

        Result<Void> createResult = inventoryService.createInventoryItem(inventoryItemInsertDTO);
        if (!createResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseWrapper.badRequest(valdiationResult.getErrorMessage()));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseWrapper.ok("Inventory Stock", "Increase"));
    }

    @GetMapping("/by-product/{productId}")
    public ResponseEntity<ResponseWrapper<Page<InventoryItemDTO>>> getInventoriesByProduct(@PathVariable Long productId) {
        boolean isProductValidated = inventoryService.validateExistingProduct(productId);
        if (!isProductValidated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound("Product", "Id"));
        }

        Page<InventoryItemDTO> inventoryItemDTOS = inventoryService.getInventoryByProduct(productId);
        return ResponseEntity.ok(ResponseWrapper.found(inventoryItemDTOS, "Inventories"));
    }
}
