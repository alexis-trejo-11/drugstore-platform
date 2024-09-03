package microservice.product_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.SubCategoryInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.SubCategoryUpdateDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.SubcategoryDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import microservice.product_service.Service.SubcategoryService;
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

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/drugstore/products/subcategories")
public class SubcategoryController {

    private final SubcategoryService subcategoryService;

    @Autowired
    public SubcategoryController(SubcategoryService subcategoryService) {
        this.subcategoryService = subcategoryService;
    }

    @Operation(summary = "Get All Subcategories", description = "Retrieves all subcategories")
    @ApiResponse(responseCode = "200", description = "All subcategories retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubcategoryDTO.class)))
    @GetMapping
    public ResponseEntity<Page<SubcategoryDTO>> getAllSubCategories(@RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SubcategoryDTO> subcategoryReturnDTOS = subcategoryService.getAllSubCategories(pageable);
        return ResponseEntity.ok(subcategoryReturnDTOS);
    }

    @Operation(summary = "Get Subcategory by ID", description = "Fetches a subcategory by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subcategory successfully fetched",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class))),
            @ApiResponse(responseCode = "404", description = "Subcategory not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class)))
    })
    @GetMapping("/{subcategoryID}")
    public ResponseEntity<ResponseWrapper<SubcategoryDTO>> getSubCategoryById(@Parameter(description = "ID of the subcategory to be fetched") @PathVariable Long subcategoryID,
                                                                              @RequestParam(defaultValue = "0") int page,
                                                                              @RequestParam(defaultValue = "10") int size) {
        boolean isSubcategoryExisting = subcategoryService.validateExistingSubCategory(subcategoryID);
        if (!isSubcategoryExisting) {
            log.warn("Subcategory not found for ID: {}", subcategoryID);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseWrapper<>(false, null, "Subcategory with ID " + subcategoryID.toString() + " Not Found.", 404));
        }

        Pageable pageable = PageRequest.of(page, size);
        SubcategoryDTO subcategoryReturnDTO = subcategoryService.getSubCategoryByIdWithProducts(subcategoryID, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseWrapper<>(true, subcategoryReturnDTO, "Subcategory successfully fetched", 200));
    }

    @Operation(summary = "Insert a new Subcategory", description = "Inserts a new subcategory into the system")
    @ApiResponse(responseCode = "200", description = "Subcategory inserted successfully")
    @PostMapping
    public ResponseEntity<String> insertCategory(@Parameter(description = "Details of the subcategory to be inserted") @Valid @RequestBody SubCategoryInsertDTO subCategoryInsertDTO) {
        subcategoryService.insertCategory(subCategoryInsertDTO);
        return ResponseEntity.ok("Subcategory inserted successfully");
    }

    @Operation(summary = "Update Subcategory", description = "Updates an existing subcategory by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subcategory successfully updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class))),
            @ApiResponse(responseCode = "404", description = "Subcategory not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class)))
    })
    @PutMapping("/{subcategoryID}")
    public ResponseEntity<ResponseWrapper<Void>> updateCategory(@Parameter(description = "ID of the subcategory to be updated") @PathVariable Long subcategoryID,
                                                                @Parameter(description = "Updated details of the subcategory") @Valid @RequestBody SubCategoryUpdateDTO subCategoryUpdateDTO) {
        boolean isSubcategoryExisting = subcategoryService.validateExistingSubCategory(subCategoryUpdateDTO.getId());
        if (!isSubcategoryExisting) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseWrapper<>(false, null, "Subcategory with ID " + subcategoryID.toString() + " Not Found.", 404));
        }

        subcategoryService.updateCategory(subCategoryUpdateDTO);

        return ResponseEntity.ok(new ResponseWrapper<>(true, null, "Subcategory Successfully Updated", 200));
    }

    @Operation(summary = "Delete Subcategory", description = "Deletes a subcategory by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subcategory successfully deleted",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class))),
            @ApiResponse(responseCode = "404", description = "Subcategory not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class)))
    })
    @DeleteMapping("/{subcategoryID}")
    public ResponseEntity<ResponseWrapper<Void>> deleteCategory(@Parameter(description = "ID of the subcategory to be deleted") @PathVariable Long subcategoryID) {
        boolean isSubcategoryExisting = subcategoryService.validateExistingSubCategory(subcategoryID);
        if (!isSubcategoryExisting) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseWrapper<>(false, null, "Subcategory with ID " + subcategoryID.toString() + " Not Found.", 404));
        }

        subcategoryService.deleteCategory(subcategoryID);

        return ResponseEntity.ok(new ResponseWrapper<>(true, null, "Subcategory Successfully Deleted", 200));
    }
}
