package microservice.product_service.Controller;

import at.backend.drugstore.microservice.common_models.DTO.Product.Category.CategoryReturnDTO;
import microservice.product_service.Service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // CREATE
    @PostMapping("/admin/categories/add")
    public CompletableFuture<ResponseEntity<?>> insertCategory(@RequestBody CategoryReturnDTO categoryDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return CompletableFuture.completedFuture(new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST));
        }

        return categoryService.insertCategory(categoryDTO)
                .thenApply(result -> {
                    if (result.isSuccess()) {
                        return new ResponseEntity<>(result.getData(), HttpStatus.CREATED);
                    } else {
                        return new ResponseEntity<>(result.getErrorMessage(), HttpStatus.BAD_REQUEST);
                    }
                })
                .exceptionally(ex -> new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR));
    }



    // READ
    @GetMapping("categories/{id}")
    public CompletableFuture<ResponseEntity<?>> getCategoryWithProductsById(@PathVariable Long id) {
        return categoryService.findCategoryByIdWithProducts(id)
                .thenApply(result -> {
                    if (!result.isSuccess()) {
                        return new ResponseEntity<>(result.getErrorMessage(), HttpStatus.NOT_FOUND);
                    } else {
                        return new ResponseEntity<>(result.getData(), HttpStatus.OK);
                    }
                })
                .exceptionally(ex -> new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @GetMapping("categories/subcategories/{id}")
    public CompletableFuture<ResponseEntity<?>> getCategoryWithSubCategories(@PathVariable Long id) {
        return categoryService.findCategoryByIdWithSubcategory(id)
                .thenApply(result -> {
                    if (!result.isSuccess()) {
                        return new ResponseEntity<>(result.getErrorMessage(), HttpStatus.NOT_FOUND);
                    } else {
                        return new ResponseEntity<>(result.getData(), HttpStatus.OK);
                    }
                })
                .exceptionally(ex -> new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @GetMapping("categories/subcategories/products/{id}")
    public CompletableFuture<ResponseEntity<?>> getCategoryWithProducts(@PathVariable Long id) {
        return categoryService.findCategoryByIdWithSubCategoriesAndProducts(id)
                .thenApply(result -> {
                    if (!result.isSuccess()) {
                        return new ResponseEntity<>(result.getErrorMessage(), HttpStatus.NOT_FOUND);
                    } else {
                        return new ResponseEntity<>(result.getData(), HttpStatus.OK);
                    }
                })
                .exceptionally(ex -> new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR));
    }

    // UPDATE
    @PutMapping("admin/categories/update{id}")
    public CompletableFuture<ResponseEntity<?>> updateCategory(@PathVariable Long id, @RequestBody CategoryReturnDTO categoryDTO) {
        return categoryService.updateCategory(id, categoryDTO)
                .thenApply(result -> {
                    if (!result.isSuccess()) {
                        return new ResponseEntity<>(result.getErrorMessage(), HttpStatus.NOT_FOUND);
                    } else {
                        return new ResponseEntity<>(result.getData(), HttpStatus.OK);
                    }
                })
                .exceptionally(ex -> new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR));
    }

    // DELETE
    @DeleteMapping("admin/categories/delete{id}")
    public CompletableFuture<ResponseEntity<?>> deleteCategory(@PathVariable Long id) {
        return categoryService.deleteCategory(id)
                .thenApply(result -> {
                    if (!result.isSuccess()) {
                        return new ResponseEntity<>(result.getErrorMessage(), HttpStatus.NOT_FOUND);
                    } else {
                        return new ResponseEntity<>(result.getData(), HttpStatus.NO_CONTENT);
                    }
                })
                .exceptionally(ex -> new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
