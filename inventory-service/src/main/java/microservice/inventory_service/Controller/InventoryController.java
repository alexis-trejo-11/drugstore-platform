package microservice.inventory_service.Controller;

import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryDTO;
import lombok.extern.slf4j.Slf4j;
import microservice.inventory_service.Service.InventoryServiceImpl;
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
@RequestMapping("/v1/api/inventory")
@Tag(name = "Drugstore Microservice API (Inventory Service)", description = "Service for managing inventory")
public class InventoryController {

    private final InventoryServiceImpl inventoryServiceImpl;

    @Autowired
    public InventoryController(InventoryServiceImpl inventoryServiceImpl) {
        this.inventoryServiceImpl = inventoryServiceImpl;
    }

    @Operation(summary = "Create a new inventory entry")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory successfully created"),
            @ApiResponse(responseCode = "404", description = "Failed to create inventory")
    })
    @PostMapping("/create")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> createInventory(@RequestBody InventoryInsertDTO inventoryInsertDTO, @RequestHeader("Authorization") String authorizationHeader) {
        log.info("Received request to create inventory: {}", inventoryInsertDTO);
        return inventoryServiceImpl.createInventory(inventoryInsertDTO).thenApply(inventoryResult -> {
            if (!inventoryResult.isSuccess()) {
                log.error("Failed to create inventory: {}", inventoryResult.getErrorMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseWrapper<>(false, null, inventoryResult.getErrorMessage(), 404));
            }
            log.info("Inventory successfully created");
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseWrapper<>(true, null, "Inventory Successfully Created!", 200));
        });
    }

    @Operation(summary = "Get inventory by product ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory found"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/product/{productId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<List<InventoryDTO>>>> getInventoryByProductId(@PathVariable Long productId) {
        log.info("Received request to get inventory by product ID: {}", productId);
        return inventoryServiceImpl.getInventoriesByProductId(productId).thenApply(inventoryDTOS -> {
            if (inventoryDTOS == null || inventoryDTOS.isEmpty()) {
                log.warn("Product not found for ID: {}", productId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseWrapper<>(false, null, "Product Not Found!", 404));
            }
            log.info("Inventory found for product ID: {}", productId);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseWrapper<>(true, inventoryDTOS, "Inventory Found!", 200));
        });
    }

    @Operation(summary = "Delete inventory by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Inventory not found")
    })
    @DeleteMapping("admin/{inventoryId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> deleteInventory(@PathVariable Long inventoryId) {
        log.info("Received request to delete inventory with ID: {}", inventoryId);
        return inventoryServiceImpl.deleteInventory(inventoryId).thenApply(isInventoryDeleted -> {
            if (!isInventoryDeleted) {
                log.warn("Inventory not found for ID: {}", inventoryId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseWrapper<>(false, null, "Inventory Not Found!", 404));
            }
            log.info("Inventory successfully deleted for ID: {}", inventoryId);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseWrapper<>(true, null, "Inventory Successfully Deleted!", 200));
        });
    }
}
