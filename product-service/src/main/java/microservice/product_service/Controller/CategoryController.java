package microservice.product_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.CategoryDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.CategoryInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.CategoryUpdateDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import microservice.product_service.Service.CategoryService;
import microservice.product_service.Service.ValidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/drugstore/products/categories")
@Tag(name = "Category", description = "APIs for managing product categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final ValidateService validateService;

    @Autowired
    public CategoryController(CategoryService categoryService, ValidateService validateService) {
        this.categoryService = categoryService;
        this.validateService = validateService;
    }

    @Operation(summary = "Get category with products by ID", description = "Retrieve a category with its associated products by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category successfully fetched"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @GetMapping("/{categoryId}")
    public ResponseEntity<ResponseWrapper<Page<CategoryDTO>>> getCategoryWithProductsById(@PathVariable Long categoryId,
                                                                                          @RequestParam(defaultValue = "0") int page,
                                                                                          @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<CategoryDTO> categoryDTOPage = categoryService.findCategoryByIdWithProducts(categoryId, pageable);

        if (categoryDTOPage.isEmpty()) {
            log.warn("Category with ID {} not found", categoryId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseWrapper<>(false, null, "Category With ID " + categoryId + " Not Found", 404));
        }

        log.info("Category with products retrieved successfully: {}", categoryDTOPage);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseWrapper<>(true, categoryDTOPage, "Category Successfully Fetched.", 200));
    }

    @Operation(summary = "Get category with subcategories", description = "Retrieve a category with its associated subcategories by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category with subcategories successfully fetched"),
            @ApiResponse(responseCode = "404", description = "Category with subcategories not found")
    })
    @GetMapping("/subcategories/{categoryId}")
    public ResponseEntity<ResponseWrapper<Page<CategoryDTO>>> getCategoryWithSubCategories(@PathVariable Long categoryId,
                                                                                           @RequestParam(defaultValue = "0") int page,
                                                                                           @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CategoryDTO> categoryDTOPage = categoryService.findCategoryByIdWithSubcategory(categoryId, pageable);

        if (categoryDTOPage.isEmpty()) {
            log.warn("Category with subcategories and ID {} not found", categoryId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseWrapper<>(false, null, "Category With Subcategories and ID " + categoryId + " Not Found", 404));
        }

        log.info("Category with subcategories retrieved successfully: {}", categoryDTOPage);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseWrapper<>(true, categoryDTOPage, "Category with Subcategories Successfully Fetched.", 200));
    }

    @Operation(summary = "Get category with products and subcategories", description = "Retrieve a category with its associated products and subcategories by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category with products and subcategories successfully fetched"),
            @ApiResponse(responseCode = "404", description = "Category with products and subcategories not found")
    })
    @GetMapping("/subcategories/products/{categoryId}")
    public ResponseEntity<ResponseWrapper<Page<CategoryDTO>>> getCategoryWithProductsAndSubCategories(@PathVariable Long categoryId,
                                                                                                      @RequestParam(defaultValue = "0") int page,
                                                                                                      @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<CategoryDTO> categoryDTOPage = categoryService.findCategoryByIdWithSubCategoriesAndProducts(categoryId, pageable);

        if (categoryDTOPage.isEmpty()) {
            log.warn("Category with subcategories and products and ID {} not found", categoryId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseWrapper<>(false, null, "Category With Subcategories and Products and ID " + categoryId + " Not Found", 404));
        }

        log.info("Category with subcategories and products retrieved successfully: {}", categoryDTOPage);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseWrapper<>(true, categoryDTOPage, "Category with Subcategories and Products Successfully Fetched.", 200));
    }

    @Operation(summary = "Insert a new category", description = "Add a new category to the system")
    @ApiResponse(responseCode = "201", description = "Category successfully created")
    @PostMapping("/admin/create")
    public ResponseEntity<ResponseWrapper<Void>> insertCategory(@Valid @RequestBody CategoryInsertDTO categoryInsertDTO) {
        Result<Void> insertResult = categoryService.insertCategory(categoryInsertDTO);
        if (!insertResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseWrapper<>(true, null, insertResult.getErrorMessage(), 400));

        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseWrapper<>(true, null, "Category Successfully Created.", 201));
    }

    @Operation(summary = "Update an existing category", description = "Update an existing category in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category successfully updated"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @PutMapping("/admin/update")
    public ResponseEntity<ResponseWrapper<Void>> updateCategory(@RequestBody CategoryUpdateDTO categoryUpdateDTO) {
        boolean isCategoryExisting = validateService.validateExistingCategory(categoryUpdateDTO.getId());
        if (!isCategoryExisting) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseWrapper<>(false, null, "Category With ID " + categoryUpdateDTO.getId() + " Not Found", 404));
        }

        Result<Void> updateResult = categoryService.updateCategory(categoryUpdateDTO);
        if (!updateResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseWrapper<>(true, null, updateResult.getErrorMessage(), 400));

        }

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseWrapper<>(true, null, "Category Successfully Updated.", 200));
    }

    @Operation(summary = "Delete a category", description = "Remove a category from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @DeleteMapping("/admin/categories/delete/{categoryId}")
    public ResponseEntity<ResponseWrapper<Void>> deleteCategory(@PathVariable Long categoryId) {
        boolean isCategoryExisting = validateService.validateExistingCategory(categoryId);
        if (!isCategoryExisting) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseWrapper<>(false, null, "Category With ID " + categoryId + " Not Found", 404));
        }

        categoryService.deleteCategory(categoryId);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseWrapper<>(true, null, "Category Successfully Deleted.", 200));
    }
}
