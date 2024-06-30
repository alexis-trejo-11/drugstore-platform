package microservice.product_service.Controller;

import at.backend.drugstore.microservice.common_models.DTO.Product.Category.MainCategoryDTO;
import microservice.product_service.Service.MainCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
public class MainCategoryController {

    private final MainCategoryService mainCategoryService;

    @Autowired
    public MainCategoryController(MainCategoryService mainCategoryService) {
        this.mainCategoryService = mainCategoryService;
    }

    // CREATE
    @PostMapping("/admin/maincategories/add")
    public CompletableFuture<ResponseEntity<?>> insertMainCategory(@RequestBody MainCategoryDTO mainCategoryDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            return CompletableFuture.completedFuture(new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST));
        }

        return mainCategoryService.insertCategory(mainCategoryDTO)
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
    @GetMapping("maincategories/{id}")
    public CompletableFuture<ResponseEntity<?>> getCategoryWithProductsById(@PathVariable Long id) {
        return mainCategoryService.findMainCategoryByIdWithProducts(id)
                .thenApply(result -> {
                    if (!result.isSuccess()) {
                        return new ResponseEntity<>(result.getErrorMessage(), HttpStatus.NOT_FOUND);
                    } else {
                        return new ResponseEntity<>(result.getData(), HttpStatus.OK);
                    }
                })
                .exceptionally(ex -> new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @GetMapping("maincategories/categories/subcategories/{id}")
    public CompletableFuture<ResponseEntity<?>> getCategoryWithSubCategories(@PathVariable Long id) {
        return mainCategoryService.findMainCategoryByIdWithCategoryAndSubCategory(id)
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
    @PutMapping("admin/maincategories/update{id}")
    public CompletableFuture<ResponseEntity<?>> updateCategory(@PathVariable Long id, @RequestBody MainCategoryDTO mainCategoryDTO) {
        return mainCategoryService.updateMainCategory(id, mainCategoryDTO)
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
    @DeleteMapping("admin/maincategories/delete{id}")
    public CompletableFuture<ResponseEntity<?>> deleteCategory(@PathVariable Long id) {
        return mainCategoryService.deleteCategory(id)
                .thenApply(result -> {
                    if (!result.isSuccess()) {
                        return new ResponseEntity<>(result.getErrorMessage(), HttpStatus.NOT_FOUND);
                    } else {
                        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                    }
                })
                .exceptionally(ex -> new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
