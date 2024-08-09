package microservice.product_service.Controller;

import at.backend.drugstore.microservice.common_models.DTOs.Product.Category.CategoryDTO;
import at.backend.drugstore.microservice.common_models.Validations.ControllerValidation;
import microservice.product_service.Service.CategoryServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/api/products/categories")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);
    private final CategoryServiceImpl categoryServiceImpl;

    @Autowired
    public CategoryController(CategoryServiceImpl categoryServiceImpl) {
        this.categoryServiceImpl = categoryServiceImpl;
    }

    /**
     * Inserts a new category.
     * @param categoryDTO the category data transfer object
     * @param bindingResult the result of validation binding
     * @return HTTP response entity with status
     */
    @PostMapping("/admin/add")
    public ResponseEntity<?> insertCategory(@Valid @RequestBody CategoryDTO categoryDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            var validationError = ControllerValidation.handleValidationError(bindingResult);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationError);
        }

        categoryServiceImpl.insertCategory(categoryDTO);
        logger.info("Category inserted successfully: {}", categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Fetches a category with its products by ID.
     * @param categoryId the category ID
     * @return HTTP response entity with category data or 404 status if not found
     */
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDTO> getCategoryWithProductsById(@PathVariable Long categoryId) {
        CategoryDTO categoryDTO = categoryServiceImpl.findCategoryByIdWithProducts(categoryId);
        if (categoryDTO == null) {
            logger.warn("Category with ID {} not found", categoryId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        logger.info("Category with products retrieved successfully: {}", categoryDTO);
        return ResponseEntity.status(HttpStatus.OK).body(categoryDTO);
    }

    /**
     * Fetches a category with its subcategories by ID.
     * @param categoryId the category ID
     * @return HTTP response entity with category data or 404 status if not found
     */
    @GetMapping("/subcategories/{categoryId}")
    public ResponseEntity<CategoryDTO> getCategoryWithSubCategories(@PathVariable Long categoryId) {
        CategoryDTO categoryDTO = categoryServiceImpl.findCategoryByIdWithSubcategory(categoryId);
        if (categoryDTO == null) {
            logger.warn("Category with subcategories and ID {} not found", categoryId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        logger.info("Category with subcategories retrieved successfully: {}", categoryDTO);
        return ResponseEntity.status(HttpStatus.OK).body(categoryDTO);
    }

    /**
     * Fetches a category with its subcategories and products by ID.
     * @param categoryId the category ID
     * @return HTTP response entity with category data or 404 status if not found
     */
    @GetMapping("/subcategories/products/{categoryId}")
    public ResponseEntity<CategoryDTO> getCategoryWithProducts(@PathVariable Long categoryId) {
        CategoryDTO categoryDTO = categoryServiceImpl.findCategoryByIdWithSubCategoriesAndProducts(categoryId);
        if (categoryDTO == null) {
            logger.warn("Category with subcategories and products and ID {} not found", categoryId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        logger.info("Category with subcategories and products retrieved successfully: {}", categoryDTO);
        return ResponseEntity.status(HttpStatus.OK).body(categoryDTO);
    }

    /**
     * Updates an existing category.
     * @param categoryDTO the category data transfer object
     * @return HTTP response entity with status
     */
    @PutMapping("/admin/categories/update")
    public ResponseEntity<Void> updateCategory(@RequestBody CategoryDTO categoryDTO) {
        boolean isCategoryUpdated = categoryServiceImpl.updateCategory(categoryDTO);
        if (!isCategoryUpdated) {
            logger.warn("Category with ID {} not found for update", categoryDTO.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        logger.info("Category updated successfully: {}", categoryDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Deletes a category by ID.
     * @param categoryId the category ID
     * @return HTTP response entity with status
     */
    @DeleteMapping("/admin/categories/delete/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        boolean isCategoryDeleted = categoryServiceImpl.deleteCategory(categoryId);
        if (!isCategoryDeleted) {
            logger.warn("Category with ID {} not found for deletion", categoryId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        logger.info("Category deleted successfully: {}", categoryId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
