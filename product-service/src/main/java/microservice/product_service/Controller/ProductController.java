package microservice.product_service.Controller;

import at.backend.drugstore.microservice.common_models.DTO.Product.ProductInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import microservice.product_service.Service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/api/products")
public class ProductController {

    private final ProductService productService;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Retrieves all products.
     *
     * @return ResponseEntity with the list of product DTOs
     */
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> productDTOS = productService.getAllProducts();
        logger.info("All products retrieved successfully");
        return ResponseEntity.ok(productDTOS);
    }

    /**
     * Retrieves products by their IDs.
     *
     * @param productIds the list of product IDs
     * @return ResponseEntity with the list of product DTOs
     */
    @GetMapping("/by-ids")
    public ResponseEntity<List<ProductDTO>> getProductsById(@RequestParam List<Long> productIds) {
        List<ProductDTO> productDTOS = productService.getProductsById(productIds);
        logger.info("Products retrieved for IDs: {}", productIds);
        return ResponseEntity.ok(productDTOS);
    }

    /**
     * Retrieves a product by ID.
     *
     * @param id the ID of the product
     * @return ResponseEntity with the product DTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        ProductDTO productDTO = productService.getProductById(id);
        if (productDTO == null) {
            logger.warn("Product not found for ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        logger.info("Product found for ID: {}", id);
        return ResponseEntity.ok(productDTO);
    }

    /**
     * Retrieves products by supplier ID.
     *
     * @param supplierId the ID of the supplier
     * @return ResponseEntity with the list of product DTOs
     */
    @GetMapping("/by-supplier/{supplierId}")
    public ResponseEntity<List<ProductDTO>> getProductsBySupplier(@PathVariable Long supplierId) {
        List<ProductDTO> productDTOS = productService.FindProductsBySupplier(supplierId);
        logger.info("Products retrieved for supplier ID: {}", supplierId);
        return ResponseEntity.ok(productDTOS);
    }

    /**
     * Retrieves products by category ID.
     *
     * @param categoryId the ID of the category
     * @return ResponseEntity with the list of product DTOs
     */
    @GetMapping("/by-category/{categoryId}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable Long categoryId) {
        List<ProductDTO> productDTOS = productService.findProductsByCategoryId(categoryId);
        logger.info("Products retrieved for category ID: {}", categoryId);
        return ResponseEntity.ok(productDTOS);
    }

    /**
     * Retrieves products by subcategory ID.
     *
     * @param subcategoryId the ID of the subcategory
     * @return ResponseEntity with the list of product DTOs
     */
    @GetMapping("/by-subcategory/{subcategoryId}")
    public ResponseEntity<List<ProductDTO>> getProductsBySubCategory(@PathVariable Long subcategoryId) {
        List<ProductDTO> productDTOS = productService.findProductsBySubCategory(subcategoryId);
        logger.info("Products retrieved for subcategory ID: {}", subcategoryId);
        return ResponseEntity.ok(productDTOS);
    }

    /**
     * Inserts a new product.
     *
     * @param productInsertDTO the product insert DTO
     * @param bindingResult    the binding result for validation
     * @return ResponseEntity with a status message
     */
    @PostMapping
    public ResponseEntity<String> insertProduct(@Valid @RequestBody ProductInsertDTO productInsertDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.error("Invalid data provided for inserting product");
            return ResponseEntity.badRequest().body("Invalid data");
        }

        productService.insertProduct(productInsertDTO);
        logger.info("Product inserted successfully: {}", productInsertDTO.getName());
        return ResponseEntity.ok("Product inserted successfully");
    }

    /**
     * Updates an existing product.
     *
     * @param id               the ID of the product
     * @param productInsertDTO the product insert DTO
     * @param bindingResult    the binding result for validation
     * @return ResponseEntity with a status message
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductInsertDTO productInsertDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.error("Invalid data provided for updating product");
            return ResponseEntity.badRequest().body("Invalid data");
        }

        boolean updated = productService.updateProduct(id, productInsertDTO);
        if (!updated) {
            logger.warn("Product not found for ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        logger.info("Product updated successfully, ID: {}", id);
        return ResponseEntity.ok("Product updated successfully");
    }

    /**
     * Deletes a product by ID.
     *
     * @param id the ID of the product
     * @return ResponseEntity with a status message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        boolean deleted = productService.deleteProduct(id);
        if (!deleted) {
            logger.warn("Product not found for deletion, ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        logger.info("Product deleted successfully, ID: {}", id);
        return ResponseEntity.ok("Product deleted successfully");
    }
}
