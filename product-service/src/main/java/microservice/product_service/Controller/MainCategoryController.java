package microservice.product_service.Controller;

import at.backend.drugstore.microservice.common_models.DTO.Product.Category.MainCategoryDTO;
import microservice.product_service.Service.MainCategoryServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/api/products/main-categories")
public class MainCategoryController {

    private final MainCategoryServiceImpl mainCategoryServiceImpl;
    private static final Logger logger = LoggerFactory.getLogger(MainCategoryController.class);

    @Autowired
    public MainCategoryController(MainCategoryServiceImpl mainCategoryServiceImpl) {
        this.mainCategoryServiceImpl = mainCategoryServiceImpl;
    }

    /**
     * Inserts a new main category.
     *
     * @param mainCategoryDTO the main category DTO
     * @param bindingResult   the binding result for validation
     * @return ResponseEntity with a status message
     */
    @PostMapping
    public ResponseEntity<String> insertCategory(@Valid @RequestBody MainCategoryDTO mainCategoryDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.error("Invalid data provided for inserting main category");
            return ResponseEntity.badRequest().body("Invalid data");
        }

        mainCategoryServiceImpl.insertCategory(mainCategoryDTO);
        logger.info("Main category inserted successfully: {}", mainCategoryDTO.getName());
        return ResponseEntity.ok("Category inserted successfully");
    }

    /**
     * Retrieves a main category by ID along with its subcategories.
     *
     * @param id the ID of the main category
     * @return ResponseEntity with the main category DTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<MainCategoryDTO> getMainCategoryById(@PathVariable Long id) {
        MainCategoryDTO mainCategoryDTO = mainCategoryServiceImpl.findMainCategoryByIdWithCategoryAndSubCategory(id);
        if (mainCategoryDTO == null) {
            logger.warn("Main category not found for ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        logger.info("Main category found for ID: {}", id);
        return ResponseEntity.ok(mainCategoryDTO);
    }

    /**
     * Retrieves a main category by ID along with its products.
     *
     * @param id the ID of the main category
     * @return ResponseEntity with the main category DTO
     */
    @GetMapping("/{id}/products")
    public ResponseEntity<MainCategoryDTO> getMainCategoryByIdWithProducts(@PathVariable Long id) {
        MainCategoryDTO mainCategoryDTO = mainCategoryServiceImpl.findMainCategoryByIdWithProducts(id);
        if (mainCategoryDTO == null) {
            logger.warn("Main category not found for ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        logger.info("Main category with products found for ID: {}", id);
        return ResponseEntity.ok(mainCategoryDTO);
    }

    /**
     * Updates an existing main category.
     *
     * @param mainCategoryDTO the main category DTO
     * @param bindingResult   the binding result for validation
     * @return ResponseEntity with a status message
     */
    @PutMapping
    public ResponseEntity<String> updateMainCategory(@Valid @RequestBody MainCategoryDTO mainCategoryDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.error("Invalid data provided for updating main category");
            return ResponseEntity.badRequest().body("Invalid data");
        }

        boolean updated = mainCategoryServiceImpl.updateMainCategory(mainCategoryDTO);
        if (!updated) {
            logger.warn("Main category not found for ID: {}", mainCategoryDTO.getId());
            return ResponseEntity.notFound().build();
        }
        logger.info("Main category updated successfully: {}", mainCategoryDTO.getId());
        return ResponseEntity.ok("Category updated successfully");
    }

    /**
     * Deletes a main category by ID.
     *
     * @param id the ID of the main category
     * @return ResponseEntity with a status message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        boolean deleted = mainCategoryServiceImpl.deleteCategoryById(id);
        if (!deleted) {
            logger.warn("Main category not found for deletion, ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        logger.info("Main category deleted successfully, ID: {}", id);
        return ResponseEntity.ok("Category deleted successfully");
    }
}
