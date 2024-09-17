package microservice.inventory_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryItemDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryItemInsertDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import microservice.inventory_service.Utils.InventorySummary;
import microservice.inventory_service.Service.InventoryItemService;
import microservice.inventory_service.Service.InventoryValidationService;
import microservice.inventory_service.Utils.ProductStockStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/drugstore/inventories")
@Tag(name = "Inventory Items", description = "Endpoints for managing inventory items")
public class InventoryItemController {

    private final InventoryItemService inventoryItemService;
    private final InventoryValidationService inventoryValidationService;

    public InventoryItemController(InventoryItemService inventoryItemService,
                                   InventoryValidationService inventoryValidationService) {
        this.inventoryItemService = inventoryItemService;
        this.inventoryValidationService = inventoryValidationService;
    }

    @Operation(summary = "Get inventory item by ID", description = "Fetch an inventory item by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory item found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = InventoryItemDTO.class))),
            @ApiResponse(responseCode = "404", description = "Inventory item not found",
                    content = @Content)
    })
    @GetMapping("/{inventoryItemId}")
    public ResponseEntity<ResponseWrapper<InventoryItemDTO>> getInventoryItemById(@Parameter(description = "ID of the inventory item to be retrieved")
                                                                                      @PathVariable Long inventoryItemId) {
        boolean isInventoryItemValidated = inventoryValidationService.validateExistingInventoryItem(inventoryItemId);
        if (!isInventoryItemValidated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound("Inventory Item", "Id"));
        }

        InventoryItemDTO inventoryItemDTO = inventoryItemService.getInventoryItemById(inventoryItemId);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseWrapper.found(inventoryItemDTO, "Inventory Item"));
    }

    @Operation(summary = "Get inventories by product ID", description = "Fetch inventory items by product ID with pagination.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventories found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content)
    })
    @GetMapping("/by-product/{productId}")
    public ResponseEntity<ResponseWrapper<Page<InventoryItemDTO>>> getInventoriesByProductId(@RequestParam(defaultValue = "0") int page,
                                                                                             @RequestParam(defaultValue = "10") int size,
                                                                                             @Parameter(description = "ID of the product")
                                                                                                 @PathVariable Long productId) {
        boolean isProductValidated = inventoryValidationService.validateExistingProduct(productId);
        if (!isProductValidated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound("Product", "Id"));
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<InventoryItemDTO> inventoryItemDTOS = inventoryItemService.getInventoryItemByProductId(productId, pageable);

        return ResponseEntity.ok(ResponseWrapper.found(inventoryItemDTOS, "Inventories By Product"));
    }

    @Operation(summary = "Get inventory summary", description = "Fetch a summary of inventory items.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory summary found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = InventorySummary.class)))
    })
    @GetMapping("/summary")
    public ResponseWrapper<InventorySummary> getInventorSummary() {
        InventorySummary inventorySummary = inventoryItemService.getInventorySummary();
        return ResponseWrapper.found(inventorySummary, "Inventory Summary");
    }

    @Operation(summary = "Create a new inventory item", description = "Create a new inventory item.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Inventory item created",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Validation failed or creation failed",
                    content = @Content)
    })
    @PostMapping("/create")
    public ResponseEntity<ResponseWrapper<Void>> createInventoryItem(@Valid @RequestBody InventoryItemInsertDTO inventoryItemInsertDTO,
                                                                     @Parameter(description = "ID of the employee creating the inventory item")
                                                                     @RequestParam Long employeeId) {
        Result<Void> validationResult = inventoryValidationService.validateInventoryItemRelationships(inventoryItemInsertDTO.getProductId());
        if (!validationResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseWrapper.badRequest(validationResult.getErrorMessage()));
        }

        Result<Void> createResult = inventoryItemService.createInventoryItem(inventoryItemInsertDTO);
        if (!createResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseWrapper.badRequest(createResult.getErrorMessage()));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseWrapper.ok("Inventory Stock", "Increase"));
    }

    @Operation(summary = "Update an existing inventory item", description = "Update an existing inventory item by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory item updated",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content)
    })
    @PutMapping("/update/{inventoryItemId}")
    public ResponseEntity<ResponseWrapper<Void>> updateInventoryItem(@Valid @RequestBody InventoryItemInsertDTO inventoryItemInsertDTO,
                                                                     @Parameter(description = "ID of the inventory item to be updated")
                                                                     @PathVariable Long inventoryItemId) {
        Result<Void> validationResult = inventoryValidationService.validateInventoryItemRelationships(inventoryItemInsertDTO.getProductId());
        if (!validationResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseWrapper.badRequest(validationResult.getErrorMessage()));
        }

        inventoryItemService.updateInventoryItem(inventoryItemId, inventoryItemInsertDTO);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseWrapper.ok("Inventory Stock", "Updated"));
    }

    @Operation(summary = "Get product stock status", description = "Fetch the stock status of a product.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product stock status retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductStockStatus.class))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content)
    })
    @GetMapping("/product-stock/{productId}")
    public ResponseEntity<ResponseWrapper<ProductStockStatus>> getProductStockStatus(@Parameter(description = "ID of the product")
                                                                                         @PathVariable Long productId) {
        boolean isProductValidated  = inventoryValidationService.validateExistingProduct(productId);
        if (!isProductValidated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound("Product", "Id"));
        }

        ProductStockStatus productTotalStock = inventoryItemService.getProductTotalStock(productId);
        return ResponseEntity.ok(ResponseWrapper.ok(productTotalStock,"Product Stock Status", "Retrieve"));
    }
}
