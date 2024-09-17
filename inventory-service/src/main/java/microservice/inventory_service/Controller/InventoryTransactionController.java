package microservice.inventory_service.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import microservice.inventory_service.Service.InventoryTransactionService;
import microservice.inventory_service.Service.InventoryValidationService;
import microservice.inventory_service.Utils.TransactionSummary;
import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryTransactionDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryTransactionInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.TransactionType;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/v1/drugstore/inventory-transactions")
@Tag(name = "Inventory Transactions", description = "Endpoints for managing inventory transactions")

public class InventoryTransactionController {

    private final InventoryTransactionService transactionService;
    private final InventoryValidationService inventoryValidationService;

    public InventoryTransactionController(InventoryTransactionService transactionService,
                                          InventoryValidationService inventoryValidationService) {
        this.transactionService = transactionService;
        this.inventoryValidationService = inventoryValidationService;
    }

    @Operation(summary = "Get transaction by ID", description = "Fetch an inventory transaction by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = InventoryTransactionDTO.class))),
            @ApiResponse(responseCode = "404", description = "Transaction not found",
                    content = @Content)
    })
    @GetMapping("/{transactionId}")
    public ResponseEntity<ResponseWrapper<InventoryTransactionDTO>> getTransactionById(@Parameter(description = "ID of the transaction to be retrieved") @PathVariable Long transactionId) {
        boolean isTransactionValidated = inventoryValidationService.validateExistingTransaction(transactionId);
        if (!isTransactionValidated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound("Inventory Transaction", "Id"));
        }

        InventoryTransactionDTO inventoryTransactionDTO = transactionService.getTransactionById(transactionId);
        return ResponseEntity.ok(ResponseWrapper.found(inventoryTransactionDTO, "Inventory Transaction"));
    }

    @Operation(summary = "Get transactions by supplier ID", description = "Fetch transactions by supplier ID with pagination.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "404", description = "Supplier not found",
                    content = @Content)
    })
    @GetMapping("/by-supplier/{supplierId}")
    public ResponseEntity<ResponseWrapper<Page<InventoryTransactionDTO>>> getTransactionLastTransactions(@RequestParam(defaultValue = "0") int page,
                                                                                                         @RequestParam(defaultValue = "10") int size,
                                                                                                         @Parameter(description = "ID of the supplier") @PathVariable Long supplierId) {
        boolean isSupplierExisting = inventoryValidationService.validateExistingSupplier(supplierId);
        if (!isSupplierExisting) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound("Supplier", "Id"));
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<InventoryTransactionDTO> transactionSummary = transactionService.getTransactionsBySupplierId(supplierId, pageable);

        return ResponseEntity.ok(ResponseWrapper.found(transactionSummary, "Inventory Transactions"));
    }

    @Operation(summary = "Get transactions ordered by date", description = "Fetch transactions ordered by date with pagination.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))),
    })
    @GetMapping("/by-date")
    public ResponseWrapper<Page<InventoryTransactionDTO>> getTransactionLastTransactions(@RequestParam(defaultValue = "0") int page,
                                                                                         @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<InventoryTransactionDTO> transactionSummary = transactionService.getTransactionsOrderByDate(pageable);

        return ResponseWrapper.found(transactionSummary, "Inventory Transactions");
    }

    @Operation(summary = "Get transactions by type", description = "Fetch transactions by type with pagination.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))),
    })
    @GetMapping("/by-transactionType")
    public ResponseWrapper<Page<InventoryTransactionDTO>> getTransactionByTransactionType(@RequestParam(defaultValue = "0") int page,
                                                                                          @RequestParam(defaultValue = "10") int size,
                                                                                          @RequestParam(defaultValue = "0") int transactionTypeIndex) {
        Pageable pageable = PageRequest.of(page, size);
        TransactionType transactionType = TransactionType.getByIndex(transactionTypeIndex);

        Page<InventoryTransactionDTO> transactionSummary = transactionService.getTransactionsByStatus(transactionType, pageable);

        return ResponseWrapper.found(transactionSummary, "Inventory Transactions");
    }

    @Operation(summary = "Get transactions near expiry", description = "Fetch transactions for items near expiry with pagination.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))),
    })
    @GetMapping("/near-to-expire")
    public ResponseWrapper<Page<InventoryTransactionDTO>> getTransactionNearToExpire(@RequestParam(defaultValue = "0") int page,
                                                                                     @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<InventoryTransactionDTO> transactionSummary = transactionService.getNearToExpire(pageable);

        return ResponseWrapper.found(transactionSummary, "Inventory Transactions");
    }

    @Operation(summary = "Get transaction summary", description = "Fetch a summary of transactions with pagination.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction summary found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransactionSummary.class))),
    })
    @GetMapping("/summary")
    public ResponseWrapper<TransactionSummary> getTransactionSummary(@RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        TransactionSummary transactionSummary = transactionService.getTransactionSummary(pageable);

        return ResponseWrapper.found(transactionSummary, "Transaction Summary");
    }

    @Operation(summary = "Get inventories by employee ID", description = "Fetch inventories managed by an employee with pagination.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventories found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "404", description = "Employee not found",
                    content = @Content)
    })
    @GetMapping("/by-employee/{employeeId}")
    public ResponseEntity<ResponseWrapper<Page<InventoryTransactionDTO>>> getInventoriesByEmployeeId(@Parameter(description = "ID of the employee") @PathVariable Long employeeId,
                                                                                                     @RequestParam(defaultValue = "0") int page,
                                                                                                     @RequestParam(defaultValue = "10") int size) {
        boolean isProductValidated = inventoryValidationService.validateExistingEmployee(employeeId);
        if (!isProductValidated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound("Employee", "Id"));
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<InventoryTransactionDTO> inventoryItemDTOS = transactionService.getInventoryItemByEmployeeId(employeeId, pageable);

        return ResponseEntity.ok(ResponseWrapper.found(inventoryItemDTOS, "Inventories By Employee"));
    }

    @Operation(summary = "Create a new inventory transaction", description = "Record a new inventory transaction.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transaction created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = InventoryTransactionDTO.class))),
            @ApiResponse(responseCode = "404", description = "Validation failed",
                    content = @Content)
    })
    @PostMapping("/create")
    public ResponseEntity<ResponseWrapper<InventoryTransactionDTO>> recordInventoryTransaction(@Parameter(description = "Data of the transaction to be created")
                                                                                                   @Valid @RequestBody InventoryTransactionInsertDTO transactionInsertDTO) {
        CompletableFuture<Result<Void>> validationFuture = transactionService.validateTransactionRelationships(transactionInsertDTO.getEmployeeId(), transactionInsertDTO.getSupplierId());

        Result<Void> validationResult = validationFuture.join();
        if (!validationResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.error(validationResult.getErrorMessage(), 404));
        }

        InventoryTransactionDTO inventoryTransactionDTO = transactionService.createTransaction(transactionInsertDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseWrapper.created(inventoryTransactionDTO, "Inventory"));
    }

    @Operation(summary = "Update an existing inventory transaction", description = "Update an existing inventory transaction by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = InventoryTransactionDTO.class))),
            @ApiResponse(responseCode = "404", description = "Validation failed or Transaction not found",
                    content = @Content)
    })
    @PutMapping("/update/{transactionId}")
    public ResponseEntity<ResponseWrapper<InventoryTransactionDTO>> updateInventoryTransaction(@Parameter(description = "Data of the transaction to be updated") @Valid @RequestBody InventoryTransactionInsertDTO transactionInsertDTO,
                                                                                               @Parameter(description = "ID of the transaction to be updated") @PathVariable Long transactionId) {
        CompletableFuture<Result<Void>> validationFuture = transactionService.validateTransactionRelationships(transactionInsertDTO.getEmployeeId(), transactionInsertDTO.getSupplierId());

        Result<Void> validationResult = validationFuture.join();
        if (!validationResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.error(validationResult.getErrorMessage(), 404));
        }

        InventoryTransactionDTO inventoryTransactionDTO = transactionService.updateTransaction(transactionId, transactionInsertDTO);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseWrapper.ok(inventoryTransactionDTO, "Inventory", "Updated"));
    }

    @Operation(summary = "Delete an inventory transaction", description = "Delete an inventory transaction by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction deleted",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Transaction not found",
                    content = @Content)
    })
    @DeleteMapping("/{transactionId}")
    public ResponseEntity<ResponseWrapper<InventoryTransactionDTO>> deleteTransaction(@Parameter(description = "ID of the transaction to be deleted") @PathVariable Long transactionId) {
        boolean isTransactionValidated = inventoryValidationService.validateExistingTransaction(transactionId);
        if (!isTransactionValidated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound("Inventory Transaction", "Id"));
        }

        transactionService.deleteTransaction(transactionId);
        return ResponseEntity.ok(ResponseWrapper.ok("Inventory Transaction", "Delete"));
    }
}
