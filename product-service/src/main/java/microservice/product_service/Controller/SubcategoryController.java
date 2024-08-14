package microservice.product_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.SubcategoryDTO;
import lombok.extern.slf4j.Slf4j;
import microservice.product_service.Service.SubcategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/api/products/subcategories")
public class SubcategoryController {

    private final SubcategoryServiceImpl subcategoryServiceImpl;

    @Autowired
    public SubcategoryController(SubcategoryServiceImpl subcategoryServiceImpl) {
        this.subcategoryServiceImpl = subcategoryServiceImpl;
    }

    @PostMapping
    public ResponseEntity<String> insertCategory(@Valid @RequestBody SubcategoryDTO subcategoryDTO) {
        subcategoryServiceImpl.insertCategory(subcategoryDTO);
        log.info("Subcategory inserted successfully: {}", subcategoryDTO.getName());
        return ResponseEntity.ok("Subcategory inserted successfully");
    }


    @GetMapping
    public ResponseEntity<List<SubcategoryDTO>> getAllSubCategories() {
        List<SubcategoryDTO> subcategoryReturnDTOS = subcategoryServiceImpl.findAllSubCategories();
        log.info("All subcategories retrieved successfully");
        return ResponseEntity.ok(subcategoryReturnDTOS);
    }


    @GetMapping("/{id}")
    public ResponseEntity<SubcategoryDTO> getSubCategoryById(@PathVariable Long id) {
        SubcategoryDTO subcategoryReturnDTO = subcategoryServiceImpl.findSubCategoryByIdWithProducts(id);
        if (subcategoryReturnDTO == null) {
            log.warn("Subcategory not found for ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        log.info("Subcategory found for ID: {}", id);
        return ResponseEntity.ok(subcategoryReturnDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateCategory(@PathVariable Long id, @Valid @RequestBody SubcategoryDTO subcategoryDTO) {
        SubcategoryDTO updatedSubcategory = subcategoryServiceImpl.updateCategory(id, subcategoryDTO);
        if (updatedSubcategory == null) {
            log.warn("Subcategory not found for ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        log.info("Subcategory updated successfully: {}", id);
        return ResponseEntity.ok("Subcategory updated successfully");
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        SubcategoryDTO subcategoryReturnDTO = subcategoryServiceImpl.deleteCategory(id);
        if (subcategoryReturnDTO == null) {
            log.warn("Subcategory not found for deletion, ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        log.info("Subcategory deleted successfully, ID: {}", id);
        return ResponseEntity.ok("Subcategory deleted successfully");
    }
}
