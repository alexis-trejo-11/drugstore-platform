package microservice.inventory_service.Controller;

import at.backend.drugstore.microservice.common_models.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import at.backend.drugstore.microservice.common_models.DTO.Inventory.InventoryInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Inventory.InventoryDTO;
import microservice.inventory_service.Service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;

    }

    @PostMapping("inventory/create")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> createInventory(@Valid @RequestBody InventoryInsertDTO inventoryInsertDTO, BindingResult bindingResult, @RequestHeader("Authorization") String authorizationHeader) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            ResponseWrapper<Void> responseWrapper = new ResponseWrapper<>(null, errors.toString());
            return CompletableFuture.completedFuture(new ResponseEntity<>(responseWrapper, HttpStatus.BAD_REQUEST));
        }

        return inventoryService.createInventory(inventoryInsertDTO)
                .thenApply(result -> {
                    if (!result.isSuccess()) {
                        ResponseWrapper<Void> responseWrapper = new ResponseWrapper<>(null, result.getErrorMessage());
                        return new ResponseEntity<>(responseWrapper, HttpStatus.NOT_FOUND);

                    } else {
                        ResponseWrapper<Void> responseWrapper = new ResponseWrapper<>(result.getData(), null);
                        return new ResponseEntity<>(responseWrapper, HttpStatus.CREATED);
                    }
                })
                .exceptionally(ex -> {
            ResponseWrapper<Void> responseWrapper = new ResponseWrapper<>(null, ex.getMessage());
            return new ResponseEntity<>(responseWrapper, HttpStatus.INTERNAL_SERVER_ERROR);
        });

    }

    @GetMapping("inventory/product/{productId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<List<InventoryDTO>>>> getInventoryByProductId(@PathVariable Long productId) {
        return inventoryService.getInventoriesByProductId(productId)
                .thenApply(listResult -> {
                    if (!listResult.isSuccess()) {
                        ResponseWrapper<List<InventoryDTO>> responseWrapper = new ResponseWrapper<>(null, listResult.getErrorMessage());
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
                    } else {
                        ResponseWrapper<List<InventoryDTO>> responseWrapper = new ResponseWrapper<>(listResult.getData(), null);
                        return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
                    }
                })
                .exceptionally(ex -> {
                    ResponseWrapper<List<InventoryDTO>> responseWrapper = new ResponseWrapper<>(null, ex.getMessage());
                    return new ResponseEntity<>(responseWrapper, HttpStatus.INTERNAL_SERVER_ERROR);
                });

    }
    /*
    @GetMapping("inventory/createdBetween")
    public CompletableFuture<ResponseEntity<ResponseWrapper>> getInventoryCreatedBetween(@RequestParam LocalDateTime startDate, @RequestParam LocalDateTime endDate) {
        return CompletableFuture.supplyAsync(() -> {
           Result<List<InventoryReturnDTO>> result = inventoryService.getInventoryCreatedBetween(startDate, endDate);
            if (!result.isSuccess()) {
                ResponseWrapper responseWrapper = new ResponseWrapper<>(null, result.getError());
                return new ResponseEntity<>(responseWrapper, HttpStatus.NOT_FOUND);

            } else {
                ResponseWrapper responseWrapper = new ResponseWrapper<>(result.getData(), null);
                return new ResponseEntity<>(responseWrapper, HttpStatus.CREATED);
            }
        }).exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }


    @GetMapping("inventory/createdAfter")
    public CompletableFuture<ResponseEntity<ResponseWrapper>> getInventoryCreatedAfter(@RequestParam LocalDateTime date) {
        return CompletableFuture.supplyAsync(() -> {
           Result<List<InventoryReturnDTO>> result = inventoryService.getInventoryCreatedAfter(date);
            if (!result.isSuccess()) {
                ResponseWrapper responseWrapper = new ResponseWrapper<>(null, result.getError());
                return new ResponseEntity<>(responseWrapper, HttpStatus.NOT_FOUND);

            } else {
                ResponseWrapper responseWrapper = new ResponseWrapper<>(result.getData(), null);
                return new ResponseEntity<>(responseWrapper, HttpStatus.CREATED);
            }
        }).exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
     */

        @DeleteMapping("admin/inventory/{inventoryId}")
        public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> deleteInventory (@PathVariable Long inventoryId) {
             return inventoryService.deleteInventory(inventoryId)
                     .thenApply(result  -> {
                         if (!result.isSuccess()) {
                             ResponseWrapper<Void> responseWrapper = new ResponseWrapper<>(null, result.getErrorMessage());
                             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
                         } else {
                             ResponseWrapper<Void> responseWrapper = new ResponseWrapper<>( null, null);
                             return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
                         }
                     })
                     .exceptionally(ex -> {
                         ResponseWrapper<Void> responseWrapper = new ResponseWrapper<>(null, ex.getMessage());
                         return new ResponseEntity<>(responseWrapper, HttpStatus.INTERNAL_SERVER_ERROR);
                     });


    }
}
