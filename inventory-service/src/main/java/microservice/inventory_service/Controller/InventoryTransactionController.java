package microservice.inventory_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryTransactionInsertDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import jakarta.validation.Valid;
import microservice.inventory_service.Service.InventoryService;
import microservice.inventory_service.Service.InventoryTransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/drugstore/inventory-transactions")
public class InventoryTransactionController {

    private final InventoryService inventoryService;
    private final InventoryTransactionService inventoryTransactionService;

    public InventoryTransactionController(InventoryService inventoryService, InventoryTransactionService inventoryTransactionService) {
        this.inventoryService = inventoryService;
        this.inventoryTransactionService = inventoryTransactionService;
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseWrapper<Void>> createInventoryTransaction(@Valid @RequestBody InventoryTransactionInsertDTO transactionInsertDTO) {
        boolean isInventoryItemValidated = inventoryService.validateExistingInventoryItem(transactionInsertDTO.getInventoryItemId());
        if (!isInventoryItemValidated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound("Inventory", "Id"));
        }

        Result<Void> valdiationResult = inventoryTransactionService.validateSupplier(transactionInsertDTO.getSupplierId());
        if (!valdiationResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.badRequest(valdiationResult.getErrorMessage()));
        }

        inventoryTransactionService.createInventoryTransaction(transactionInsertDTO);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseWrapper.created("Inventory"));
    }
}
