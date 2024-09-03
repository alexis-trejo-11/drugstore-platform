package microservice.product_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.MainCategoryDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.MainCategoryInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.MainCategoryUpdateDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import microservice.product_service.Service.MainCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/v1/drugstore/products/main-categories")
public class MainCategoryController {

    private final MainCategoryService mainCategoryService;

    @Autowired
    public MainCategoryController(MainCategoryService mainCategoryService) {
        this.mainCategoryService = mainCategoryService;
    }

    @Operation(summary = "Get Main Category by ID", description = "Fetches a main category by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved main category"),
            @ApiResponse(responseCode = "404", description = "Main category not found")
    })
    @GetMapping("/{mainCategoryID}")
    public ResponseEntity<ResponseWrapper<MainCategoryDTO>> getMainCategoryById(@Parameter(description = "ID of the main category to be fetched") @PathVariable Long mainCategoryID,
                                                                                @RequestParam(defaultValue = "0") int page,
                                                                                @RequestParam(defaultValue = "10") int size) {
        boolean isMainCategoryExisting = mainCategoryService.validateExistingMainCategory(mainCategoryID);
        if (!isMainCategoryExisting) {
            log.warn("Main category not found for ID: {}", mainCategoryID);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseWrapper<>(false, null, "Main Category With ID " + mainCategoryID.toString() + " Not Found.", 404));
        }

        Pageable pageable = PageRequest.of(page, size);
        MainCategoryDTO mainCategoryDTO = mainCategoryService.getMainCategoryByIdWithCategoryAndSubCategory(mainCategoryID, pageable);
        log.info("Main category found for ID: {}", mainCategoryID);
        return ResponseEntity.ok(new ResponseWrapper<>(true, mainCategoryDTO, "Main Category Successfully Fetched.", 200));
    }

    @Operation(summary = "Get Main Category with Products by ID", description = "Fetches a main category along with its products by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved main category with products"),
            @ApiResponse(responseCode = "404", description = "Main category not found")
    })
    @GetMapping("/{mainCategoryID}/products")
    public ResponseEntity<ResponseWrapper<MainCategoryDTO>> getMainCategoryByIdWithProducts(@Parameter(description = "ID of the main category to be fetched along with its products") @PathVariable Long mainCategoryID,
                                                                                            @RequestParam(defaultValue = "0") int page,
                                                                                            @RequestParam(defaultValue = "10") int size) {
        boolean isMainCategoryExisting = mainCategoryService.validateExistingMainCategory(mainCategoryID);
        if (!isMainCategoryExisting) {
            log.warn("Main category not found for ID: {}", mainCategoryID);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseWrapper<>(false, null, "Main Category With ID " + mainCategoryID.toString() + " Not Found.", 404));
        }

        Pageable pageable = PageRequest.of(page, size);
        MainCategoryDTO mainCategoryDTO = mainCategoryService.getMainCategoryByIdWithProducts(mainCategoryID, pageable);
        log.info("Main category with products found for ID: {}", mainCategoryID);

        return ResponseEntity.ok(new ResponseWrapper<>(true, mainCategoryDTO, "Main Category Successfully Fetched.", 200));
    }

    @Operation(summary = "Insert a new Main Category", description = "Inserts a new main category into the system")
    @ApiResponse(responseCode = "200", description = "Successfully inserted main category")
    @PostMapping
    public ResponseEntity<String> insertCategory(@Parameter(description = "Details of the main category to be inserted") @Valid @RequestBody MainCategoryInsertDTO mainCategoryInsertDTO) {
        mainCategoryService.insertCategory(mainCategoryInsertDTO);
        log.info("Main category inserted successfully: {}", mainCategoryInsertDTO.getName());

        return ResponseEntity.ok("Category inserted successfully");
    }

    @Operation(summary = "Update an existing Main Category", description = "Updates an existing main category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated main category"),
            @ApiResponse(responseCode = "404", description = "Main category not found")
    })
    @PutMapping
    public ResponseEntity<ResponseWrapper<Void>> updateMainCategory(@Parameter(description = "Updated details of the main category") @Valid @RequestBody MainCategoryUpdateDTO mainCategoryUpdateDTO) {
        boolean isMainCategoryExisting = mainCategoryService.validateExistingMainCategory(mainCategoryUpdateDTO.getId());
        if (!isMainCategoryExisting) {
            log.warn("Main category not found for ID: {}", mainCategoryUpdateDTO.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseWrapper<>(false, null, "Main Category With ID " + mainCategoryUpdateDTO.getId().toString() + " Not Found.", 404));
        }

        mainCategoryService.updateMainCategory(mainCategoryUpdateDTO);
        log.info("Main category updated successfully: {}", mainCategoryUpdateDTO.getId());

        return ResponseEntity.ok(new ResponseWrapper<>(true, null, "Main Category Successfully Updated.", 200));
    }

    @Operation(summary = "Delete a Main Category", description = "Deletes a main category by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted main category"),
            @ApiResponse(responseCode = "404", description = "Main category not found")
    })
    @DeleteMapping("/{mainCategoryID}")
    public ResponseEntity<ResponseWrapper<Void>> deleteCategory(@Parameter(description = "ID of the main category to be deleted") @PathVariable Long mainCategoryID) {
        boolean isMainCategoryExisting = mainCategoryService.validateExistingMainCategory(mainCategoryID);
        if (!isMainCategoryExisting) {
            log.warn("Main category not found for ID: {}", mainCategoryID);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseWrapper<>(false, null, "Main Category With ID " + mainCategoryID.toString() + " Not Found.", 404));
        }

        mainCategoryService.deleteMainCategoryById(mainCategoryID);
        log.info("Main category deleted successfully, ID: {}", mainCategoryID);

        return ResponseEntity.ok(new ResponseWrapper<>(true, null, "Main Category Successfully Deleted.", 200));
    }
}
