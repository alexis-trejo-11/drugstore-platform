package microservice.product_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.CategoryDTO;
import lombok.extern.slf4j.Slf4j;
import microservice.product_service.Service.CategoryServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/v1/api/products/categories")
public class CategoryController {

    private final CategoryServiceImpl categoryServiceImpl;

    @Autowired
    public CategoryController(CategoryServiceImpl categoryServiceImpl) {
        this.categoryServiceImpl = categoryServiceImpl;
    }

    @PostMapping("/admin/add")
    public ResponseEntity<?> insertCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        categoryServiceImpl.insertCategory(categoryDTO);
        log.info("Category inserted successfully: {}", categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDTO> getCategoryWithProductsById(@PathVariable Long categoryId) {
        CategoryDTO categoryDTO = categoryServiceImpl.findCategoryByIdWithProducts(categoryId);
        if (categoryDTO == null) {
            log.warn("Category with ID {} not found", categoryId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        log.info("Category with products retrieved successfully: {}", categoryDTO);
        return ResponseEntity.status(HttpStatus.OK).body(categoryDTO);
    }

    @GetMapping("/subcategories/{categoryId}")
    public ResponseEntity<CategoryDTO> getCategoryWithSubCategories(@PathVariable Long categoryId) {
        CategoryDTO categoryDTO = categoryServiceImpl.findCategoryByIdWithSubcategory(categoryId);
        if (categoryDTO == null) {
            log.warn("Category with subcategories and ID {} not found", categoryId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        log.info("Category with subcategories retrieved successfully: {}", categoryDTO);
        return ResponseEntity.status(HttpStatus.OK).body(categoryDTO);
    }

    @GetMapping("/subcategories/products/{categoryId}")
    public ResponseEntity<CategoryDTO> getCategoryWithProducts(@PathVariable Long categoryId) {
        CategoryDTO categoryDTO = categoryServiceImpl.findCategoryByIdWithSubCategoriesAndProducts(categoryId);
        if (categoryDTO == null) {
            log.warn("Category with subcategories and products and ID {} not found", categoryId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        log.info("Category with subcategories and products retrieved successfully: {}", categoryDTO);
        return ResponseEntity.status(HttpStatus.OK).body(categoryDTO);
    }

    @PutMapping("/admin/categories/update")
    public ResponseEntity<Void> updateCategory(@RequestBody CategoryDTO categoryDTO) {
        boolean isCategoryUpdated = categoryServiceImpl.updateCategory(categoryDTO);
        if (!isCategoryUpdated) {
            log.warn("Category with ID {} not found for update", categoryDTO.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        log.info("Category updated successfully: {}", categoryDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/admin/categories/delete/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        boolean isCategoryDeleted = categoryServiceImpl.deleteCategory(categoryId);
        if (!isCategoryDeleted) {
            log.warn("Category with ID {} not found for deletion", categoryId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        log.info("Category deleted successfully: {}", categoryId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
