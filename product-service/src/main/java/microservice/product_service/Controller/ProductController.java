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
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/v1/api/products")
public class ProductController {

    private final ProductServiceImpl productServiceImpl;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    public ProductController(ProductServiceImpl productServiceImpl) {
        this.productServiceImpl = productServiceImpl;
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<ApiResponse<List<ProductDTO>>>> getAllProducts() {
        return productServiceImpl.getAllProducts()
                .thenApply(productDTOS -> {
                    logger.info("All products retrieved successfully");
                    return ResponseEntity.ok(new ApiResponse<>(true, productDTOS, "Products retrieved successfully", HttpStatus.OK.value()));
                });
    }

    @PostMapping("/by-ids")
    public CompletableFuture<ResponseEntity<ApiResponse<List<ProductDTO>>>> getProductsById(@RequestBody Map<String, List<Long>> request) {
        List<Long> productIds = request.get("productIds");
        return productServiceImpl.getProductsById(productIds)
                .thenApply(productDTOS -> {
                    logger.info("Products retrieved for IDs: {}", productIds);
                    return ResponseEntity.ok(new ApiResponse<>(true, productDTOS, "Products retrieved successfully", HttpStatus.OK.value()));
                });
    }

    @GetMapping("/{productId}")
    public CompletableFuture<ResponseEntity<ApiResponse<ProductDTO>>> getProductById(@PathVariable Long productId) {
        return productServiceImpl.getProductById(productId)
                .thenApply(productDTO -> {
                    if (productDTO == null) {
                        logger.warn("Product not found for ID: {}", productId);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Product not found", HttpStatus.NOT_FOUND.value()));
                    }
                    logger.info("Product found for ID: {}", productId);
                    return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, productDTO, "Product retrieved successfully", HttpStatus.OK.value()));
                });
    }

    @GetMapping("/by-supplier/{supplierId}")
    public CompletableFuture<ResponseEntity<ApiResponse<List<ProductDTO>>>> getProductsBySupplier(@PathVariable Long supplierId) {
        return productServiceImpl.findProductsBySupplier(supplierId)
                .thenApply(productDTOS -> {
                    logger.info("Products retrieved for supplier ID: {}", supplierId);
                    return ResponseEntity.ok(new ApiResponse<>(true, productDTOS, "Products retrieved successfully", HttpStatus.OK.value()));
                });
    }

    @GetMapping("/by-category/{categoryId}")
    public CompletableFuture<ResponseEntity<ApiResponse<List<ProductDTO>>>> getProductsByCategory(@PathVariable Long categoryId) {
        return productServiceImpl.findProductsByCategoryId(categoryId)
                .thenApply(productDTOS -> {
                    logger.info("Products retrieved for category ID: {}", categoryId);
                    return ResponseEntity.ok(new ApiResponse<>(true, productDTOS, "Products retrieved successfully", HttpStatus.OK.value()));
                });
    }

    @GetMapping("/by-subcategory/{subcategoryId}")
    public CompletableFuture<ResponseEntity<ApiResponse<List<ProductDTO>>>> getProductsBySubCategory(@PathVariable Long subcategoryId) {
        return productServiceImpl.findProductsBySubCategory(subcategoryId)
                .thenApply(productDTOS -> {
                    logger.info("Products retrieved for subcategory ID: {}", subcategoryId);
                    return ResponseEntity.ok(new ApiResponse<>(true, productDTOS, "Products retrieved successfully", HttpStatus.OK.value()));
                });
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> insertProduct(@Valid @RequestBody ProductInsertDTO productInsertDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            var validationErrors = ControllerValidation.handleValidationError(bindingResult);
            logger.error("Invalid data provided for inserting product");
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "Invalid data: " + validationErrors, HttpStatus.BAD_REQUEST.value())));
        }

        return productServiceImpl.processInsertProduct(productInsertDTO)
                .thenApply(result -> {
                    if (!result.isSuccess()) {
                        logger.error("Error inserting product: {}", result.getErrorMessage());
                        return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, result.getErrorMessage(), HttpStatus.BAD_REQUEST.value()));
                    }
                    logger.info("Product inserted successfully: {}", productInsertDTO.getName());
                    return ResponseEntity.ok(new ApiResponse<>(true, null, "Product inserted successfully", HttpStatus.OK.value()));
                });
    }

    @PutMapping("/{id}")
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductInsertDTO productInsertDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.error("Invalid data provided for updating product");
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "Invalid data", HttpStatus.BAD_REQUEST.value())));
        }

        return productServiceImpl.updateProduct(id, productInsertDTO)
                .thenApply(result -> {
                    if (!result.isSuccess()) {
                        logger.warn("Product not found for update, ID: {}", id);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Product not found", HttpStatus.NOT_FOUND.value()));
                    }
                    logger.info("Product updated successfully, ID: {}", id);
                    return ResponseEntity.ok(new ApiResponse<>(true, null, "Product updated successfully", HttpStatus.OK.value()));
                });
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity<ApiResponse<Void>>> deleteProduct(@PathVariable Long id) {
        return productServiceImpl.deleteProduct(id)
                .thenApply(deleted -> {
                    if (!deleted) {
                        logger.warn("Product not found for deletion, ID: {}", id);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Product not found", HttpStatus.NOT_FOUND.value()));
                    }
                    logger.info("Product deleted successfully, ID: {}", id);
                    return ResponseEntity.ok(new ApiResponse<>(true, null, "Product deleted successfully", HttpStatus.OK.value()));
                });
    }
}