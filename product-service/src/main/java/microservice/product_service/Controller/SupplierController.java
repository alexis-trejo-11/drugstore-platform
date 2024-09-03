package microservice.product_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Supplier.SupplierDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Supplier.SupplierInsertDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import jakarta.validation.Valid;
import microservice.product_service.Service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@RestController
@RequestMapping("/v1/drugstore/products/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    @Autowired
    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @Operation(summary = "Create a new Supplier", description = "Inserts a new supplier into the system")
    @ApiResponse(responseCode = "200", description = "Supplier successfully created")
    @PostMapping
    public ResponseEntity<ResponseWrapper<Void>> createSupplier(@Parameter(description = "Details of the supplier to be inserted") @Valid @RequestBody SupplierInsertDTO supplierInsertDTO) {
        supplierService.insertSupplier(supplierInsertDTO);
        return ResponseEntity.ok().body(new ResponseWrapper<>(true, null, "Supplier Successfully Created", 200));
    }

    @Operation(summary = "Get Supplier by ID", description = "Fetches a supplier by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supplier data successfully fetched",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class))),
            @ApiResponse(responseCode = "404", description = "Supplier not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class)))
    })
    @GetMapping("/{supplierID}")
    public ResponseEntity<ResponseWrapper<SupplierDTO>> getSupplierById(@Parameter(description = "ID of the supplier to be fetched") @PathVariable Long supplierID) {
        boolean isSupplierExisting = supplierService.validateExistingSupplier(supplierID);
        if (!isSupplierExisting) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseWrapper<>(false, null, "Supplier With ID " + supplierID.toString() + " Not Found.", 404));
        }

        SupplierDTO supplierDTO = supplierService.getSupplierById(supplierID);
        return ResponseEntity.ok().body(new ResponseWrapper<>(true, supplierDTO, "Supplier With ID " + supplierID.toString() + " Data Successfully Fetched", 200));
    }

    @Operation(summary = "Get Supplier by Name", description = "Fetches a supplier by its name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supplier data successfully fetched",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class))),
            @ApiResponse(responseCode = "404", description = "Supplier not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class)))
    })
    @GetMapping("/supplierName/{supplierName}")
    public ResponseEntity<ResponseWrapper<SupplierDTO>> getSupplierByName(@Parameter(description = "Name of the supplier to be fetched") @PathVariable String supplierName) {
        SupplierDTO supplierDTO = supplierService.getSupplierByName(supplierName);
        if (supplierDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseWrapper<>(false, null, "Supplier With Name: " + supplierName + " Not Found.", 404));
        }

        return ResponseEntity.ok().body(new ResponseWrapper<>(true, supplierDTO, "Supplier With Name: " + supplierName + " Data Successfully Fetched", 200));
    }

    @Operation(summary = "Get All Suppliers", description = "Retrieves all suppliers")
    @ApiResponse(responseCode = "200", description = "All suppliers retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SupplierDTO.class)))
    @GetMapping
    public ResponseEntity<Page<SupplierDTO>> getSuppliers(@RequestParam(defaultValue = "false") Boolean sortedAsc,
                                                          Pageable pageable) {
        Page<SupplierDTO> suppliers = supplierService.getAllSuppliersSortedByName(sortedAsc, pageable);
        return ResponseEntity.ok(suppliers);
    }

    @Operation(summary = "Update Supplier", description = "Updates an existing supplier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supplier successfully updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class))),
            @ApiResponse(responseCode = "404", description = "Supplier not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class)))
    })
    @PutMapping
    public ResponseEntity<ResponseWrapper<Void>> updateSupplier(@Parameter(description = "Updated details of the supplier") @RequestBody SupplierDTO supplierDTO) {
        boolean isSupplierExisting = supplierService.validateExistingSupplier(supplierDTO.getId());
        if (!isSupplierExisting) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseWrapper<>(false, null, "Supplier With ID " + supplierDTO.getId().toString() + " Not Found.", 404));
        }

        supplierService.updateSupplier(supplierDTO);
        return ResponseEntity.ok().body(new ResponseWrapper<>(true, null, "Supplier Successfully Updated", 200));
    }

    @Operation(summary = "Delete Supplier", description = "Deletes a supplier by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supplier successfully deleted",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class))),
            @ApiResponse(responseCode = "404", description = "Supplier not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Void>> deleteSupplier(@Parameter(description = "ID of the supplier to be deleted") @PathVariable Long supplierID) {
        boolean isSupplierExisting = supplierService.validateExistingSupplier(supplierID);
        if (!isSupplierExisting) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseWrapper<>(false, null, "Supplier With ID " + supplierID.toString() + " Not Found.", 404));
        }

        supplierService.deleteSupplier(supplierID);
        return ResponseEntity.ok().body(new ResponseWrapper<>(true, null, "Supplier Successfully Deleted.", 200));
    }
}
