package microservice.product_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Product.ProductDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.ProductInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.ProductUpdateDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;

import at.backend.drugstore.microservice.common_classes.Utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.extern.slf4j.Slf4j;
import microservice.product_service.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/v1/drugstore/products")
@Tag(name = "Product", description = "Product management APIs")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Retrieve products by IDs", description = "Fetch products by a list of IDs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class)))
    })
    @PostMapping("/by-ids")
    public ResponseEntity<ResponseWrapper<List<ProductDTO>>> getProductsById(@Parameter(description = "List of product IDs") @RequestBody Map<String, List<Long>> request) {
        List<Long> productIds = request.get("productIds");
        List<ProductDTO> productDTOS = productService.getProductsById(productIds);

        log.info("Products retrieved for IDs: {}", productIds);
        return ResponseEntity.ok(new ResponseWrapper<>(true, productDTOS, "Products retrieved successfully", HttpStatus.OK.value()));
    }

    @Operation(summary = "Retrieve a product by ID", description = "Fetch a product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class)))
    })
    @GetMapping("/{productId}")
    public ResponseEntity<ResponseWrapper<ProductDTO>> getProductById(@Parameter(description = "ID of the product") @PathVariable Long productId) {
        ProductDTO productDTO =  productService.getProductById(productId);

        if (productDTO == null) {
            log.warn("Product not found for ID: {}", productId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseWrapper<>(false, null, "Product not found", HttpStatus.NOT_FOUND.value()));
        }

        log.info("Product retrieved for ID: {}", productId);
        return ResponseEntity.ok(new ResponseWrapper<>(true, productDTO, "Product retrieved successfully", HttpStatus.OK.value()));
    }

    @Operation(summary = "Retrieve a all products sorting by category hierarchy", description = "Fetch a product all product data return in pages")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class)))
    })
    public ResponseEntity<ResponseWrapper<Page<ProductDTO>>> getAllProductsSortedByCategoryHierarchy(@RequestParam(defaultValue = "0") int page,
                                                                                                     @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDTO> productDTOPage = productService.getAllProductsSortedByCategoryHierarchy(pageable);
        return ResponseEntity.ok(new ResponseWrapper<>(true, productDTOPage, "Product Data Successfully Fetched.", 200));
    }


    @Operation(summary = "Retrieve products by supplier ID", description = "Fetch products by the supplier's ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class)))
    })
    @GetMapping("/by-supplier/{supplierId}")
    public ResponseEntity<ResponseWrapper<Page<ProductDTO>>> getProductsBySupplier(@Parameter(description = "ID of the supplier") @PathVariable Long supplierId,
                                                                                    @RequestParam(defaultValue = "0") int page,
                                                                                    @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDTO> productDTOPage = productService.getProductsBySupplier(supplierId, pageable);

        log.info("Products retrieved for supplier ID: {}", supplierId);
        return ResponseEntity.ok(new ResponseWrapper<>(true, productDTOPage, "Products retrieved successfully", HttpStatus.OK.value()));
    }

    @Operation(summary = "Retrieve products by category ID", description = "Fetch products by the category's ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class)))
    })
    @GetMapping("/by-category/{categoryId}")
    public ResponseEntity<ResponseWrapper<Page<ProductDTO>>> getProductsByCategoryId(@Parameter(description = "ID of the category") @PathVariable Long categoryId,
                                                                                      @RequestParam(defaultValue = "0") int page,
                                                                                      @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDTO> productDTOS = productService.getProductsByCategoryId(categoryId,pageable);

        log.info("Products retrieved for category ID: {}", categoryId);
        return ResponseEntity.ok(new ResponseWrapper<>(true, productDTOS, "Products retrieved successfully", HttpStatus.OK.value()));
    }

    @Operation(summary = "Retrieve products by subcategory ID", description = "Fetch products by the subcategory's ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class)))
    })
    @GetMapping("/by-subcategory/{subcategoryId}")
    public ResponseEntity<ResponseWrapper<Page<ProductDTO>>> findProductsBySubCategory(@Parameter(description = "ID of the subcategory") @PathVariable Long subcategoryId,
                                                                                       @RequestParam(defaultValue = "0") int page,
                                                                                       @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDTO> productDTOS = productService.getProductsBySubCategory(subcategoryId, pageable);

        log.info("Products retrieved for subcategory ID: {}", subcategoryId);
        return ResponseEntity.ok(new ResponseWrapper<>(true, productDTOS, "Products retrieved successfully", HttpStatus.OK.value()));
    }


    @Operation(summary = "Create a new product", description = "Add a new product to the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class)))
    })
    @PostMapping("/add")
    public ResponseEntity<ResponseWrapper<Void>> createProduct(@Parameter(description = "Product data to be added") @Valid @RequestBody ProductInsertDTO productInsertDTO) {
        Result<Void> createResult = productService.createProduct(productInsertDTO);
        if (!createResult.isSuccess()) {
            log.error("Failed to create product: {}", createResult.getErrorMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseWrapper<>(false, null, createResult.getErrorMessage(), HttpStatus.BAD_REQUEST.value()));
        }

        log.info("Product created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseWrapper<>(true, null, "Product created successfully", HttpStatus.CREATED.value()));
    }

    @Operation(summary = "Update a product", description = "Update an existing product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class)))
    })
    @PutMapping("/{productId}")
    public ResponseEntity<ResponseWrapper<Void>> updateProduct(@Parameter(description = "Product data to be updated") @Valid @RequestBody ProductUpdateDTO productUpdateDTO) {
        boolean isProductExisitng = productService.validateExisitingProduct(productUpdateDTO.getId());
        if (!isProductExisitng) {
            log.warn("Failed to delete product for ID: {}", productUpdateDTO.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseWrapper<>(false, null, "Product not found", HttpStatus.NOT_FOUND.value()));
        }

        Result<Void> updateResult = productService.updateProduct(productUpdateDTO);
        if (!updateResult.isSuccess()) {
            // Return Bad Request (400) if relationship validations fail
            return ResponseEntity.ok(new ResponseWrapper<>(true, null, updateResult.getErrorMessage(), HttpStatus.BAD_REQUEST.value()));
        }

        log.info("Product updated successfully for ID: {}", productUpdateDTO.getId());
        return ResponseEntity.ok(new ResponseWrapper<>(true, null, "Product updated successfully", HttpStatus.OK.value()));
    }

    @Operation(summary = "Delete a product", description = "Delete a product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product deleted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class)))
    })
    @DeleteMapping("/{productId}")
    public ResponseEntity<ResponseWrapper<Void>> deleteProduct(@Parameter(description = "ID of the product to be deleted") @PathVariable Long productId) {
        boolean isProductExisitng = productService.validateExisitingProduct(productId);
        if (!isProductExisitng) {
            log.warn("Failed to delete product for ID: {}", productId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseWrapper<>(false, null, "Product not found", HttpStatus.NOT_FOUND.value()));
        }

        productService.deleteProduct(productId);
        log.info("Product deleted successfully for ID: {}", productId);
        return ResponseEntity.ok(new ResponseWrapper<>(true, null, "Product deleted successfully", HttpStatus.OK.value()));
    }

    // To Validate an Entry of Product in Another Services
    @GetMapping("/validate/{productId}")
    public boolean validateExisitingProduct(@PathVariable Long productId) {
        return productService.validateExisitingProduct(productId);
    }
}