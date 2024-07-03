package microservice.product_service.Controller;

import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import at.backend.drugstore.microservice.common_models.DTO.Product.ProductInsertDTO;
import at.backend.drugstore.microservice.common_models.Utils.ApiResponse;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import at.backend.drugstore.microservice.common_models.Validations.ControllerValidation;

import microservice.product_service.Service.ProductServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/api/products")
public class ProductController {

    private final ProductServiceImpl productServiceImpl;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    public ProductController(ProductServiceImpl productServiceImpl) {
        this.productServiceImpl = productServiceImpl;
    }

    /**
     * Retrieves all products.
     *
     * @return ResponseEntity with the list of product DTOs
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getAllProducts() {
        List<ProductDTO> productDTOS = productServiceImpl.getAllProducts();
        logger.info("All products retrieved successfully");
        return ResponseEntity.ok(new ApiResponse<>(true, productDTOS, "Products retrieved successfully", HttpStatus.OK.value()));
    }

    /**
     * Retrieves products by their IDs.
     *
     * @param productIds the list of product IDs
     * @return ResponseEntity with the list of product DTOs
     */
    @GetMapping("/by-ids")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getProductsById(@RequestParam List<Long> productIds) {
        List<ProductDTO> productDTOS = productServiceImpl.getProductsById(productIds);
        logger.info("Products retrieved for IDs: {}", productIds);
        return ResponseEntity.ok(new ApiResponse<>(true, productDTOS, "Products retrieved successfully", HttpStatus.OK.value()));
    }

    /**
     * Retrieves a product by ID.
     *
     * @param productId the ID of the product
     * @return ResponseEntity with the product DTO
     */
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductById(@PathVariable Long productId) {
        ProductDTO productDTO = productServiceImpl.getProductById(productId);
        if (productDTO == null) {
            logger.warn("Product not found for ID: {}", productId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Product not found", HttpStatus.NOT_FOUND.value()));
        }
        logger.info("Product found for ID: {}", productId);
        return ResponseEntity.ok(new ApiResponse<>(true, productDTO, "Product retrieved successfully", HttpStatus.OK.value()));
    }

    /**
     * Retrieves products by supplier ID.
     *
     * @param supplierId the ID of the supplier
     * @return ResponseEntity with the list of product DTOs
     */
    @GetMapping("/by-supplier/{supplierId}")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getProductsBySupplier(@PathVariable Long supplierId) {
        List<ProductDTO> productDTOS = productServiceImpl.FindProductsBySupplier(supplierId);
        logger.info("Products retrieved for supplier ID: {}", supplierId);
        return ResponseEntity.ok(new ApiResponse<>(true, productDTOS, "Products retrieved successfully", HttpStatus.OK.value()));
    }

    /**
     * Retrieves products by category ID.
     *
     * @param categoryId the ID of the category
     * @return ResponseEntity with the list of product DTOs
     */
    @GetMapping("/by-category/{categoryId}")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getProductsByCategory(@PathVariable Long categoryId) {
        List<ProductDTO> productDTOS = productServiceImpl.findProductsByCategoryId(categoryId);
        logger.info("Products retrieved for category ID: {}", categoryId);
        return ResponseEntity.ok(new ApiResponse<>(true, productDTOS, "Products retrieved successfully", HttpStatus.OK.value()));
    }

    /**
     * Retrieves products by subcategory ID.
     *
     * @param subcategoryId the ID of the subcategory
     * @return ResponseEntity with the list of product DTOs
     */
    @GetMapping("/by-subcategory/{subcategoryId}")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getProductsBySubCategory(@PathVariable Long subcategoryId) {
        List<ProductDTO> productDTOS = productServiceImpl.findProductsBySubCategory(subcategoryId);
        logger.info("Products retrieved for subcategory ID: {}", subcategoryId);
        return ResponseEntity.ok(new ApiResponse<>(true, productDTOS, "Products retrieved successfully", HttpStatus.OK.value()));
    }

    /**
     * Inserts a new product.
     *
     * @param productInsertDTO the product insert DTO
     * @param bindingResult    the binding result for validation
     * @return ResponseEntity with a status message
     */
    @PostMapping
    public ResponseEntity<ApiResponse<String>> insertProduct(@Valid @RequestBody ProductInsertDTO productInsertDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            var validationErrors = ControllerValidation.handleValidationError(bindingResult);
            logger.error("Invalid data provided for inserting product");
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "Invalid data: " + validationErrors, HttpStatus.BAD_REQUEST.value()));
        }

        Result<Void> insertResult = productServiceImpl.processInsertProduct(productInsertDTO);
        if (!insertResult.isSuccess()) {
            logger.error("Error inserting product: {}", insertResult.getErrorMessage());
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, insertResult.getErrorMessage(), HttpStatus.BAD_REQUEST.value()));
        }

        logger.info("Product inserted successfully: {}", productInsertDTO.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, null, "Product inserted successfully", HttpStatus.OK.value()));
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
    public ResponseEntity<ApiResponse<String>> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductInsertDTO productInsertDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.error("Invalid data provided for updating product");
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "Invalid data", HttpStatus.BAD_REQUEST.value()));
        }

        Result<Void> updatedResult = productServiceImpl.updateProduct(id, productInsertDTO);
        if (!updatedResult.isSuccess()) {
            logger.warn("Product not found for update, ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Product not found", HttpStatus.NOT_FOUND.value()));
        }

        logger.info("Product updated successfully, ID: {}", id);
        return ResponseEntity.ok(new ApiResponse<>(true, null, "Product updated successfully", HttpStatus.OK.value()));
    }

    /**
     * Deletes a product by ID.
     *
     * @param id the ID of the product
     * @return ResponseEntity with a status message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable Long id) {
        boolean deleted = productServiceImpl.deleteProduct(id);
        if (!deleted) {
            logger.warn("Product not found for deletion, ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Product not found", HttpStatus.NOT_FOUND.value()));
        }

        logger.info("Product deleted successfully, ID: {}", id);
        return ResponseEntity.ok(new ApiResponse<>(true, null, "Product deleted successfully", HttpStatus.OK.value()));
    }
}
