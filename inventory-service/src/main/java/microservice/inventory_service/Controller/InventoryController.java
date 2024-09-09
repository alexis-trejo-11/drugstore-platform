package microservice.inventory_service.Controller;

import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import lombok.extern.slf4j.Slf4j;
import microservice.inventory_service.Service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/v1/drugstore/inventories")
@Tag(name = "Drugstore Microservice API (Inventory Service)", description = "Service for managing inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Operation(summary = "Create a new inventory entry")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory successfully created"),
            @ApiResponse(responseCode = "404", description = "Failed to create inventory")
    })
    @PostMapping("/create")
    public ResponseEntity<ResponseWrapper<Void>> createInventory(@RequestBody InventoryInsertDTO inventoryInsertDTO, @RequestHeader("Authorization") String authorizationHeader) {
        log.info("Received request to create inventory: {}", inventoryInsertDTO);
         CompletableFuture<Result<Void>> inventoryResultAsync = inventoryService.createInventory(inventoryInsertDTO);

         Result<Void> inventoryResult = inventoryResultAsync.join();
         if (!inventoryResult.isSuccess()) {
            log.error("Failed to create inventory: {}", inventoryResult.getErrorMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseWrapper<>(false, null, inventoryResult.getErrorMessage(), 404));
        }

        log.info("Inventory successfully created");
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseWrapper<>(true, null, "Inventory Successfully Created!", 200));

    }

    @Operation(summary = "Get inventory by product ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory found"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/product/{productId}")
    public ResponseWrapper<List<InventoryDTO>> getInventoryByProductId(@PathVariable Long productId) {
        log.info("Received request to get inventory by product ID: {}", productId);

        var inventoriesAsync = inventoryService.getInventoriesByProductId(productId);
        List<InventoryDTO> inventoryDTOS = inventoriesAsync.join();

        log.info("Inventory found for product ID: {}", productId);
        return ResponseWrapper.ok("Inventories", "Product Id", inventoryDTOS);
        }

    @Operation(summary = "Delete inventory by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Inventory not found")
    })
    @DeleteMapping("admin/{inventoryId}")
    public ResponseEntity<ResponseWrapper<Void>> deleteInventory(@PathVariable Long inventoryId) {
        log.info("Received request to delete inventory with ID: {}", inventoryId);

        boolean isInventoryDeleted = inventoryService.deleteInventory(inventoryId);
        if (!isInventoryDeleted) {
            log.warn("Inventory not found for ID: {}", inventoryId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseWrapper<>(false, null, "Inventory Not Found!", 404));
        }

        log.info("Inventory successfully deleted for ID: {}", inventoryId);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseWrapper<>(true, null, "Inventory Successfully Deleted!", 200));

    }
}
