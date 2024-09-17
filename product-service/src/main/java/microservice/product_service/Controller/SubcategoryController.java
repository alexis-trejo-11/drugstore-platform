package microservice.product_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.SubCategoryInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.SubCategoryUpdateDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.SubcategoryDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import microservice.product_service.Service.SubcategoryService;
import microservice.product_service.Service.ValidateService;
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
    private final ValidateService validateService;

    @Autowired
    public SubcategoryController(SubcategoryService subcategoryService, ValidateService validateService) {
        this.subcategoryService = subcategoryService;
        this.validateService = validateService;
    }

    @Operation(summary = "Get All Subcategories", description = "Retrieves all subcategories")
    @ApiResponse(responseCode = "200", description = "All subcategories retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubcategoryDTO.class)))
    @GetMapping
    public ResponseEntity<ResponseWrapper<Page<SubcategoryDTO>>> getAllSubCategories(@RequestParam(defaultValue = "0") int page,
                                                                                     @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SubcategoryDTO> subcategoryReturnDTOS = subcategoryService.getAllSubCategories(pageable);

        return ResponseEntity.ok(ResponseWrapper.found(subcategoryReturnDTOS, "Subcategory"));
    }

    @Operation(summary = "Get Subcategory by ID", description = "Fetches a subcategory by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subcategory successfully fetched",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class))),
            @ApiResponse(responseCode = "404", description = "Subcategory not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class)))
    })
    @GetMapping("/{subcategoryID}")
    public ResponseEntity<ResponseWrapper<SubcategoryDTO>> getSubCategoryById(@PathVariable Long subcategoryID,
                                                                              @RequestParam(defaultValue = "0") int page,
                                                                              @RequestParam(defaultValue = "10") int size) {
        boolean isSubcategoryExisting = validateService.validateExistingSubCategory(subcategoryID);
        if (!isSubcategoryExisting) {
            log.warn("Subcategory not found for ID: {}", subcategoryID);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound("Subcategory", "Id"));
        }

        Pageable pageable = PageRequest.of(page, size);
        SubcategoryDTO subcategoryReturnDTO = subcategoryService.getSubCategoryByIdWithProducts(subcategoryID, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseWrapper.found(subcategoryReturnDTO, "Subcategory"));
    }

    @Operation(summary = "Insert a new Subcategory", description = "Inserts a new subcategory into the system")
    @ApiResponse(responseCode = "200", description = "Subcategory inserted successfully")
    @PostMapping
    public ResponseEntity<ResponseWrapper<Void>> insertCategory(@Valid @RequestBody SubCategoryInsertDTO subCategoryInsertDTO) {
        subcategoryService.insertCategory(subCategoryInsertDTO);
        return ResponseEntity.ok(ResponseWrapper.created("Subcategory"));
    }

    @Operation(summary = "Update Subcategory", description = "Updates an existing subcategory by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subcategory successfully updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class))),
            @ApiResponse(responseCode = "404", description = "Subcategory not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class)))
    })
    @PutMapping("/{subcategoryID}")
    public ResponseEntity<ResponseWrapper<Void>> updateCategory(@PathVariable Long subcategoryID,
                                                                @Valid @RequestBody SubCategoryUpdateDTO subCategoryUpdateDTO) {
        boolean isSubcategoryExisting = validateService.validateExistingSubCategory(subCategoryUpdateDTO.getId());
        if (!isSubcategoryExisting) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound("Subcategory", "Id"));
        }

        subcategoryService.updateCategory(subCategoryUpdateDTO);

        return ResponseEntity.ok(ResponseWrapper.ok("Subcategory", "Update"));
    }

    @Operation(summary = "Delete Subcategory", description = "Deletes a subcategory by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subcategory successfully deleted",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class))),
            @ApiResponse(responseCode = "404", description = "Subcategory not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class)))
    })
    @DeleteMapping("/{subcategoryID}")
    public ResponseEntity<ResponseWrapper<Void>> deleteCategory(@PathVariable Long subcategoryID) {
        boolean isSubcategoryExisting = validateService.validateExistingSubCategory(subcategoryID);
        if (!isSubcategoryExisting) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound("Subcategory", "Id"));
        }

        subcategoryService.deleteCategory(subcategoryID);

        return ResponseEntity.ok(ResponseWrapper.ok("Subcategory", "Delete"));
    }
}
