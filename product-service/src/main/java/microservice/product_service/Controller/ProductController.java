package microservice.product_service.Controller;

import at.backend.drugstore.microservice.common_models.DTOs.Product.ProductDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Product.ProductInsertDTO;
import at.backend.drugstore.microservice.common_models.Utils.ResponseWrapper;

import microservice.product_service.Service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/v1/api/products")
public class ProductController {

    private final ProductService productService;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<ResponseWrapper<List<ProductDTO>>>> getAllProducts() {
        return productService.getAllProducts()
                .thenApply(productDTOS -> {
                    logger.info("All products retrieved successfully");
                    return ResponseEntity.ok(new ResponseWrapper<>(true, productDTOS, "Products retrieved successfully", HttpStatus.OK.value()));
                });
    }

    @PostMapping("/by-ids")
    public CompletableFuture<ResponseEntity<ResponseWrapper<List<ProductDTO>>>> getProductsById(@RequestBody Map<String, List<Long>> request) {
        List<Long> productIds = request.get("productIds");
        return productService.getProductsById(productIds)
                .thenApply(productDTOS -> {
                    logger.info("Products retrieved for IDs: {}", productIds);
                    return ResponseEntity.ok(new ResponseWrapper<>(true, productDTOS, "Products retrieved successfully", HttpStatus.OK.value()));
                });
    }

    @GetMapping("/{productId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<ProductDTO>>> getProductById(@PathVariable Long productId) {
        return productService.getProductById(productId)
                .thenApply(productDTO -> {
                    if (productDTO == null) {
                        logger.warn("Product not found for ID: {}", productId);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseWrapper<>(false, null, "Product not found", HttpStatus.NOT_FOUND.value()));
                    }
                    logger.info("Product retrieved for ID: {}", productId);
                    return ResponseEntity.ok(new ResponseWrapper<>(true, productDTO, "Product retrieved successfully", HttpStatus.OK.value()));
                });
    }

    @GetMapping("/by-supplier/{supplierId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<List<ProductDTO>>>> findProductsBySupplier(@PathVariable Long supplierId) {
        return productService.findProductsBySupplier(supplierId)
                .thenApply(productDTOS -> {
                    logger.info("Products retrieved for supplier ID: {}", supplierId);
                    return ResponseEntity.ok(new ResponseWrapper<>(true, productDTOS, "Products retrieved successfully", HttpStatus.OK.value()));
                });
    }

    @GetMapping("/by-category/{categoryId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<List<ProductDTO>>>> findProductsByCategoryId(@PathVariable Long categoryId) {
        return productService.findProductsByCategoryId(categoryId)
                .thenApply(productDTOS -> {
                    logger.info("Products retrieved for category ID: {}", categoryId);
                    return ResponseEntity.ok(new ResponseWrapper<>(true, productDTOS, "Products retrieved successfully", HttpStatus.OK.value()));
                });
    }

    @GetMapping("/by-subcategory/{subcategoryId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<List<ProductDTO>>>> findProductsBySubCategory(@PathVariable Long subcategoryId) {
        return productService.findProductsBySubCategory(subcategoryId)
                .thenApply(productDTOS -> {
                    logger.info("Products retrieved for subcategory ID: {}", subcategoryId);
                    return ResponseEntity.ok(new ResponseWrapper<>(true, productDTOS, "Products retrieved successfully", HttpStatus.OK.value()));
                });
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> createProduct(@Valid @RequestBody ProductInsertDTO productInsertDTO) {
        return productService.processInsertProduct(productInsertDTO)
                .thenApply(result -> {
                    if (result.isSuccess()) {
                        logger.info("Product created successfully");
                        return ResponseEntity.status(HttpStatus.CREATED)
                                .body(new ResponseWrapper<>(true, null, "Product created successfully", HttpStatus.CREATED.value()));
                    }

                    logger.error("Failed to create product: {}", result.getErrorMessage());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ResponseWrapper<>(false, null, result.getErrorMessage(), HttpStatus.BAD_REQUEST.value()));
                });
    }

    @PutMapping("/{productId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> updateProduct(@Valid @RequestBody ProductInsertDTO productInsertDTO,
                                                                                  @PathVariable Long productId) {
        return productService.updateProduct(productId, productInsertDTO)
                .thenApply(result -> {
                    if (result.isSuccess()) {
                        logger.info("Product updated successfully for ID: {}", productId);
                        return ResponseEntity.ok(new ResponseWrapper<>(true, null, "Product updated successfully", HttpStatus.OK.value()));
                    }

                    logger.error("Failed to update product for ID: {}: {}", productId, result.getErrorMessage());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ResponseWrapper<>(false, null, result.getErrorMessage(), HttpStatus.BAD_REQUEST.value()));
                });
    }

    @DeleteMapping("/{productId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> deleteProduct(@PathVariable Long productId) {
        return productService.deleteProduct(productId)
                .thenApply(isDeleted -> {
                    if (isDeleted) {
                        logger.info("Product deleted successfully for ID: {}", productId);
                        return ResponseEntity.ok(new ResponseWrapper<>(true, null, "Product deleted successfully", HttpStatus.OK.value()));
                    }

                    logger.warn("Failed to delete product for ID: {}", productId);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseWrapper<>(false, null, "Product not found", HttpStatus.NOT_FOUND.value()));
                });
    }
}
