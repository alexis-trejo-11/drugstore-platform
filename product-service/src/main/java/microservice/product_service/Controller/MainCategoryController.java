package microservice.product_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.MainCategoryDTO;
import lombok.extern.slf4j.Slf4j;
import microservice.product_service.Service.MainCategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/v1/api/products/main-categories")
public class MainCategoryController {

    private final MainCategoryServiceImpl mainCategoryServiceImpl;

    @Autowired
    public MainCategoryController(MainCategoryServiceImpl mainCategoryServiceImpl) {
        this.mainCategoryServiceImpl = mainCategoryServiceImpl;
    }

    @PostMapping
    public ResponseEntity<String> insertCategory(@Valid @RequestBody MainCategoryDTO mainCategoryDTO) {
        mainCategoryServiceImpl.insertCategory(mainCategoryDTO);
        log.info("Main category inserted successfully: {}", mainCategoryDTO.getName());
        return ResponseEntity.ok("Category inserted successfully");
    }


    @GetMapping("/{id}")
    public ResponseEntity<MainCategoryDTO> getMainCategoryById(@PathVariable Long id) {
        MainCategoryDTO mainCategoryDTO = mainCategoryServiceImpl.findMainCategoryByIdWithCategoryAndSubCategory(id);
        if (mainCategoryDTO == null) {
            log.warn("Main category not found for ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        log.info("Main category found for ID: {}", id);
        return ResponseEntity.ok(mainCategoryDTO);
    }


    @GetMapping("/{id}/products")
    public ResponseEntity<MainCategoryDTO> getMainCategoryByIdWithProducts(@PathVariable Long id) {
        MainCategoryDTO mainCategoryDTO = mainCategoryServiceImpl.findMainCategoryByIdWithProducts(id);
        if (mainCategoryDTO == null) {
            log.warn("Main category not found for ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        log.info("Main category with products found for ID: {}", id);
        return ResponseEntity.ok(mainCategoryDTO);
    }


    @PutMapping
    public ResponseEntity<String> updateMainCategory(@Valid @RequestBody MainCategoryDTO mainCategoryDTO) {
        boolean updated = mainCategoryServiceImpl.updateMainCategory(mainCategoryDTO);
        if (!updated) {
            log.warn("Main category not found for ID: {}", mainCategoryDTO.getId());
            return ResponseEntity.notFound().build();
        }
        log.info("Main category updated successfully: {}", mainCategoryDTO.getId());
        return ResponseEntity.ok("Category updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        boolean deleted = mainCategoryServiceImpl.deleteCategoryById(id);
        if (!deleted) {
            log.warn("Main category not found for deletion, ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        log.info("Main category deleted successfully, ID: {}", id);
        return ResponseEntity.ok("Category deleted successfully");
    }
}
