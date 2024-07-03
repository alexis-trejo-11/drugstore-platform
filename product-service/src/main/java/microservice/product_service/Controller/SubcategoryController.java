package microservice.product_service.Controller;

import at.backend.drugstore.microservice.common_models.DTO.Product.Category.SubcategoryDTO;
import microservice.product_service.Service.SubcategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/api/products/subcategories")
public class SubcategoryController {

    private final SubcategoryService subcategoryService;
    private static final Logger logger = LoggerFactory.getLogger(SubcategoryController.class);

    @Autowired
    public SubcategoryController(SubcategoryService subcategoryService) {
        this.subcategoryService = subcategoryService;
    }

    /**
     * Inserts a new subcategory.
     *
     * @param subcategoryDTO the subcategory DTO
     * @param bindingResult  the binding result for validation
     * @return ResponseEntity with a status message
     */
    @PostMapping
    public ResponseEntity<String> insertCategory(@Valid @RequestBody SubcategoryDTO subcategoryDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.error("Invalid data provided for inserting subcategory");
            return ResponseEntity.badRequest().body("Invalid data");
        }

        subcategoryService.insertCategory(subcategoryDTO);
        logger.info("Subcategory inserted successfully: {}", subcategoryDTO.getName());
        return ResponseEntity.ok("Subcategory inserted successfully");
    }

    /**
     * Retrieves all subcategories.
     *
     * @return ResponseEntity with the list of subcategory DTOs
     */
    @GetMapping
    public ResponseEntity<List<SubcategoryDTO>> getAllSubCategories() {
        List<SubcategoryDTO> subcategoryReturnDTOS = subcategoryService.findAllSubCategories();
        logger.info("All subcategories retrieved successfully");
        return ResponseEntity.ok(subcategoryReturnDTOS);
    }

    /**
     * Retrieves a subcategory by ID along with its products.
     *
     * @param id the ID of the subcategory
     * @return ResponseEntity with the subcategory DTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<SubcategoryDTO> getSubCategoryById(@PathVariable Long id) {
        SubcategoryDTO subcategoryReturnDTO = subcategoryService.findSubCategoryByIdWithProducts(id);
        if (subcategoryReturnDTO == null) {
            logger.warn("Subcategory not found for ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        logger.info("Subcategory found for ID: {}", id);
        return ResponseEntity.ok(subcategoryReturnDTO);
    }

    /**
     * Updates an existing subcategory.
     *
     * @param id             the ID of the subcategory
     * @param subcategoryDTO the subcategory DTO
     * @param bindingResult  the binding result for validation
     * @return ResponseEntity with a status message
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> updateCategory(@PathVariable Long id, @Valid @RequestBody SubcategoryDTO subcategoryDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.error("Invalid data provided for updating subcategory");
            return ResponseEntity.badRequest().body("Invalid data");
        }

        SubcategoryDTO updatedSubcategory = subcategoryService.updateCategory(id, subcategoryDTO);
        if (updatedSubcategory == null) {
            logger.warn("Subcategory not found for ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        logger.info("Subcategory updated successfully: {}", id);
        return ResponseEntity.ok("Subcategory updated successfully");
    }

    /**
     * Deletes a subcategory by ID.
     *
     * @param id the ID of the subcategory
     * @return ResponseEntity with a status message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        SubcategoryDTO subcategoryReturnDTO = subcategoryService.deleteCategory(id);
        if (subcategoryReturnDTO == null) {
            logger.warn("Subcategory not found for deletion, ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        logger.info("Subcategory deleted successfully, ID: {}", id);
        return ResponseEntity.ok("Subcategory deleted successfully");
    }
}
