package microservice.inventory_service.Controller;

import at.backend.drugstore.microservice.common_models.Utils.ApiResponse;
import at.backend.drugstore.microservice.common_models.DTO.Inventory.InventoryInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Inventory.InventoryDTO;
import microservice.inventory_service.Service.InventoryServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/v1/api/inventory")
public class InventoryController {

    private final InventoryServiceImpl inventoryServiceImpl;
    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);

    @Autowired
    public InventoryController(InventoryServiceImpl inventoryServiceImpl) {
        this.inventoryServiceImpl = inventoryServiceImpl;
    }

    @PostMapping("/create")
    public CompletableFuture<ResponseEntity<ApiResponse<Void>>> createInventory(@RequestBody InventoryInsertDTO inventoryInsertDTO, @RequestHeader("Authorization") String authorizationHeader) {
        logger.info("Received request to create inventory: {}", inventoryInsertDTO);
        return inventoryServiceImpl.createInventory(inventoryInsertDTO).thenApply(inventoryResult -> {
            if (!inventoryResult.isSuccess()) {
                logger.error("Failed to create inventory: {}", inventoryResult.getErrorMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, inventoryResult.getErrorMessage(), 404));
            }
            logger.info("Inventory successfully created");
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, null, "Inventory Successfully Created!.", 200));
        });
    }

    @GetMapping("/product/{productId}")
    public CompletableFuture<ResponseEntity<ApiResponse<List<InventoryDTO>>>> getInventoryByProductId(@PathVariable Long productId) {
        logger.info("Received request to get inventory by product ID: {}", productId);
        return inventoryServiceImpl.getInventoriesByProductId(productId).thenApply(inventoryDTOS -> {
            if (inventoryDTOS == null || inventoryDTOS.isEmpty()) {
                logger.warn("Product not found for ID: {}", productId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Product Not Found!.", 404));
            }
            logger.info("Inventory found for product ID: {}", productId);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, inventoryDTOS, "Inventory Found!.", 200));
        });
    }

    @DeleteMapping("admin/{inventoryId}")
    public CompletableFuture<ResponseEntity<ApiResponse<Void>>> deleteInventory(@PathVariable Long inventoryId) {
        logger.info("Received request to delete inventory with ID: {}", inventoryId);
        return inventoryServiceImpl.deleteInventory(inventoryId).thenApply(isInventoryDeleted -> {
            if (!isInventoryDeleted) {
                logger.warn("Inventory not found for ID: {}", inventoryId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Inventory Not Found!.", 404));
            }
            logger.info("Inventory successfully deleted for ID: {}", inventoryId);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, null, "Inventory Successfully Deleted!", 200));
        });
    }
}
