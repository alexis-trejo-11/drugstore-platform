package microservice.inventory_service.Controller;

import at.backend.drugstore.microservice.common_models.Utils.ApiResponse;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import at.backend.drugstore.microservice.common_models.DTO.Inventory.InventoryInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Inventory.InventoryDTO;
import at.backend.drugstore.microservice.common_models.Validations.ControllerValidation;
import at.backend.drugstore.microservice.common_models.Validations.CustomControllerResponse;
import microservice.inventory_service.Service.InventoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/api/inventory")
public class InventoryController {

    private final InventoryServiceImpl inventoryServiceImpl;

    @Autowired
    public InventoryController(InventoryServiceImpl inventoryServiceImpl) {
        this.inventoryServiceImpl = inventoryServiceImpl;

    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<?>> createInventory(@Valid @RequestBody InventoryInsertDTO inventoryInsertDTO, BindingResult bindingResult, @RequestHeader("Authorization") String authorizationHeader) {
        if (bindingResult.hasErrors()) {
        CustomControllerResponse validationError = ControllerValidation.handleValidationError(bindingResult);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, validationError, "Validation Error", 400));
        }

        Result<Void> invetoryResult = inventoryServiceImpl.createInventory(inventoryInsertDTO);
        if (!invetoryResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, invetoryResult.getErrorMessage(), 404));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, null, "Inventory Successfully Created!.", 200));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<InventoryDTO>>> getInventoryByProductId(@PathVariable Long productId) {
        List<InventoryDTO> inventoryDTOS  = inventoryServiceImpl.getInventoriesByProductId(productId);
        if (inventoryDTOS == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Product Not Found!.", 404));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, inventoryDTOS, "Inventory Found!.", 200));
    }


    /*
    @GetMapping("inventory/createdBetween")
    public CompletableFuture<ResponseEntity<ResponseWrapper>> getInventoryCreatedBetween(@RequestParam LocalDateTime startDate, @RequestParam LocalDateTime endDate) {
        return CompletableFuture.supplyAsync(() -> {
           Result<List<InventoryReturnDTO>> result = inventoryServiceImpl.getInventoryCreatedBetween(startDate, endDate);
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
           Result<List<InventoryReturnDTO>> result = inventoryServiceImpl.getInventoryCreatedAfter(date);
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

        @DeleteMapping("admin/{inventoryId}")
        public ResponseEntity<ApiResponse<Void>> deleteInventory (@PathVariable Long inventoryId) {
             boolean isInventoryDeleted = inventoryServiceImpl.deleteInventory(inventoryId);
             if (!isInventoryDeleted) {
                 return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Inventory Not Found!.", 404));
             }

            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, null, "Inventory Successfully Deleted!", 200));
    }
}
