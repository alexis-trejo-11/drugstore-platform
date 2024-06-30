package microservice.product_service.Controller;

import at.backend.drugstore.microservice.common_models.Utils.ResponseWrapper;
import microservice.product_service.Service.SubcategoryService;
import at.backend.drugstore.microservice.common_models.DTO.Product.Category.SubcategoryReturnDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/subcategories")
public class SubcategoryController {

    private final SubcategoryService subcategoryService;

    @Autowired
    public SubcategoryController(SubcategoryService subcategoryService) {
        this.subcategoryService = subcategoryService;
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<ResponseWrapper<SubcategoryReturnDTO>>> createSubcategory(@RequestBody SubcategoryReturnDTO subcategoryDTO) {
        return subcategoryService.insertCategory(subcategoryDTO)
                .thenApply(subcategory -> new ResponseEntity<>(new ResponseWrapper<>(subcategory.getData(), null, HttpStatus.CREATED), HttpStatus.CREATED))
                .exceptionally(ex -> new ResponseEntity<>(new ResponseWrapper<>(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<ResponseWrapper<List<SubcategoryReturnDTO>>>> getAllSubcategories() {
        return subcategoryService.findAllSubCategories()
                .thenApply(result -> new ResponseEntity<>(new ResponseWrapper<>(result.getData(), null, HttpStatus.OK), HttpStatus.OK))
                .exceptionally(ex -> new ResponseEntity<>(new ResponseWrapper<>(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<SubcategoryReturnDTO>>> getSubcategoryById(@PathVariable Long id) {
        return subcategoryService.findSubCategoryByIdWithProducts(id)
                .thenApply(result -> {
                    if(!result.isSuccess()) {
                       return new ResponseEntity<>(new ResponseWrapper<SubcategoryReturnDTO>(null, result.getErrorMessage(), HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);

                    } else {
                     return new ResponseEntity<>(new ResponseWrapper<SubcategoryReturnDTO>(result.getData(), null, HttpStatus.OK), HttpStatus.OK);
                    }
                })
                .exceptionally(ex -> new ResponseEntity<>(new ResponseWrapper<SubcategoryReturnDTO>(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @PutMapping("/{id}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<SubcategoryReturnDTO>>> updateSubcategory(@PathVariable Long id, @RequestBody SubcategoryReturnDTO subcategoryDTO) {
        return subcategoryService.updateCategory(id, subcategoryDTO)
                .thenApply(subcategory -> new ResponseEntity<>(new ResponseWrapper<>(subcategory.getData(), null, HttpStatus.OK), HttpStatus.OK))
                .exceptionally(ex -> new ResponseEntity<>(new ResponseWrapper<>(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<SubcategoryReturnDTO>>> deleteSubcategory(@PathVariable Long id) {
        return subcategoryService.deleteCategory(id)
                .thenApply(subcategory -> new ResponseEntity<>(new ResponseWrapper<>(subcategory.getData(), null, HttpStatus.OK), HttpStatus.OK))
                .exceptionally(ex -> new ResponseEntity<>(new ResponseWrapper<>(null, ex.getMessage(), HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND));
    }
}
