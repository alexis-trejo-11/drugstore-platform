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
import microservice.product_service.Service.ValidateService;
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

    private final ValidateService validateService;
    private final MainCategoryService mainCategoryService;

    @Autowired
    public MainCategoryController(ValidateService validateService,
                                  MainCategoryService mainCategoryService) {
        this.validateService = validateService;
        this.mainCategoryService = mainCategoryService;
    }

    @Operation(summary = "Get Main Category by ID", description = "Fetches a main category by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved main category"),
            @ApiResponse(responseCode = "404", description = "Main category not found")
    })
    @GetMapping("/{mainCategoryId}")
    public ResponseEntity<ResponseWrapper<MainCategoryDTO>> getMainCategoryById(@PathVariable Long mainCategoryId,
                                                                                @RequestParam(defaultValue = "0") int page,
                                                                                @RequestParam(defaultValue = "10") int size) {
        boolean isMainCategoryExisting = validateService.validateExistingMainCategory(mainCategoryId);
        if (!isMainCategoryExisting) {
            log.warn("getMainCategoryById -> main category not found for ID: {}", mainCategoryId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound("Main Category", "Id"));
        }

        Pageable pageable = PageRequest.of(page, size);
        MainCategoryDTO mainCategoryDTO = mainCategoryService.getMainCategoryByIdWithCategoryAndSubCategory(mainCategoryId, pageable);

        log.info("Main category found for ID: {}", mainCategoryId);
        return ResponseEntity.ok(ResponseWrapper.found(mainCategoryDTO, "Main Category"));
    }

    @Operation(summary = "Get Main Category with Products by ID", description = "Fetches a main category along with its products by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved main category with products"),
            @ApiResponse(responseCode = "404", description = "Main category not found")
    })
    @GetMapping("/{mainCategoryId}/products")
    public ResponseEntity<ResponseWrapper<MainCategoryDTO>> getMainCategoryByIdWithProducts(@PathVariable Long mainCategoryId,
                                                                                            @RequestParam(defaultValue = "0") int page,
                                                                                            @RequestParam(defaultValue = "10") int size) {
        boolean isMainCategoryExisting = validateService.validateExistingMainCategory(mainCategoryId);
        if (!isMainCategoryExisting) {
            log.warn("getMainCategoryByIdWithProducts -> main category not found for ID: {}", mainCategoryId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound("Main Category", "Id"));
        }

        Pageable pageable = PageRequest.of(page, size);
        MainCategoryDTO mainCategoryDTO = mainCategoryService.getMainCategoryByIdWithProducts(mainCategoryId, pageable);

        log.info("getMainCategoryByIdWithProducts -> main category with products found for ID: {}", mainCategoryId);
        return ResponseEntity.ok(ResponseWrapper.found(mainCategoryDTO, "Main Category"));
    }

    @Operation(summary = "Insert a new Main Category", description = "Inserts a new main category into the system")
    @ApiResponse(responseCode = "200", description = "Successfully inserted main category")
    @PostMapping
    public ResponseEntity<ResponseWrapper<Void>> insertCategory(@Valid @RequestBody MainCategoryInsertDTO mainCategoryInsertDTO) {
        mainCategoryService.insertCategory(mainCategoryInsertDTO);

        log.info("insertCategory -> Main category inserted successfully: {}", mainCategoryInsertDTO.getName());
        return ResponseEntity.ok(ResponseWrapper.created("Category"));
    }

    @Operation(summary = "Update an existing Main Category", description = "Updates an existing main category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated main category"),
            @ApiResponse(responseCode = "404", description = "Main category not found")
    })
    @PutMapping
    public ResponseEntity<ResponseWrapper<Void>> updateMainCategory(@Valid @RequestBody MainCategoryUpdateDTO mainCategoryUpdateDTO) {
        boolean isMainCategoryExisting = validateService.validateExistingMainCategory(mainCategoryUpdateDTO.getId());
        if (!isMainCategoryExisting) {
            log.warn("updateMainCategory -> main category not found for ID: {}", mainCategoryUpdateDTO.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.ok("Main Category", "Update"));
        }

        mainCategoryService.updateMainCategory(mainCategoryUpdateDTO);
        log.info("Main category updated successfully: {}", mainCategoryUpdateDTO.getId());

        return ResponseEntity.ok(ResponseWrapper.ok("Main Category", "Update"));
    }

    @Operation(summary = "Delete a Main Category", description = "Deletes a main category by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted main category"),
            @ApiResponse(responseCode = "404", description = "Main category not found")
    })
    @DeleteMapping("/{mainCategoryId}")
    public ResponseEntity<ResponseWrapper<Void>> deleteCategory(@PathVariable Long mainCategoryId) {
        boolean isMainCategoryExisting = validateService.validateExistingMainCategory(mainCategoryId);
        if (!isMainCategoryExisting) {
            log.warn("deleteCategory -> main category not found for ID: {}", mainCategoryId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound("Main Category", "Id"));
        }

        mainCategoryService.deleteMainCategoryById(mainCategoryId);
        log.info("deleteCategory -> main category deleted successfully, ID: {}", mainCategoryId);

        return ResponseEntity.ok(ResponseWrapper.ok("Main Category", "Delete"));

    }
}
