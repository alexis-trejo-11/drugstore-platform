package microservice.product_service.Controller;

import at.backend.drugstore.microservice.common_models.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import at.backend.drugstore.microservice.common_models.DTO.Product.ProductInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import microservice.product_service.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("products/all")
    public CompletableFuture<ResponseEntity<ResponseWrapper<List<ProductDTO>>>> getAllProducts() {
        return productService.getAll()
                .thenApplyAsync(productDTOS -> {
                    ResponseWrapper<List<ProductDTO>> responseWrapper = new ResponseWrapper<>(productDTOS, null);
                    return ResponseEntity.status(HttpStatus.OK).body(responseWrapper);
                })
                .exceptionally(ex -> {
                    ResponseWrapper<List<ProductDTO>> responseWrapper = new ResponseWrapper<>(null, ex.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseWrapper);
                });
    }

    @GetMapping("products/{productId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<ProductDTO>>> getProductById(@PathVariable Long productId) {
        return productService.getProductById(productId)
                .thenApply(result -> {
                    if (!result.isSuccess()) {
                        ResponseWrapper<ProductDTO> errorResponse = new ResponseWrapper<>(null, result.getErrorMessage());
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                    } else {
                        ResponseWrapper<ProductDTO> response = new ResponseWrapper<>(result.getData(), null);
                        return ResponseEntity.status(HttpStatus.OK).body(response);
                    }
                }).exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        }


    @PostMapping("/products/many")
    public CompletableFuture<ResponseEntity<ResponseWrapper<List<ProductDTO>>>> getProductsById(@RequestBody Map<String, List<Long>> request) {
            List<Long> productIds = request.get("productIds");
            return productService.getProductsById(productIds)
                    .thenApplyAsync(result -> {
                            if (!result.isSuccess()) {
                                ResponseWrapper<List<ProductDTO>> response = new ResponseWrapper<>(null, result.getErrorMessage(), HttpStatus.NOT_FOUND);
                                return ResponseEntity.status(HttpStatus.OK).body(response);
                            }

                        ResponseWrapper<List<ProductDTO>> response = new ResponseWrapper<>(result.getData(), null, HttpStatus.OK);
                        return ResponseEntity.status(HttpStatus.OK).body(response);
                    }).exceptionally(ex -> {
                        ResponseWrapper<List<ProductDTO>> responseWrapper = new ResponseWrapper<>(null, ex.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseWrapper);
                    });
            }

    @GetMapping("products/supplier/{supplierId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<List<ProductDTO>>>> findProductsBySupplier(@PathVariable Long supplierId) {
                return productService.FindProductsBySupplier(supplierId)
                        .thenApplyAsync(productDTOS -> {
                            ResponseWrapper<List<ProductDTO>> response = new ResponseWrapper<>(productDTOS, null);
                            return ResponseEntity.status(HttpStatus.OK).body(response);
                        }).exceptionally(ex -> {
                            ResponseWrapper<List<ProductDTO>> responseWrapper = new ResponseWrapper<>(null, ex.getMessage());
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseWrapper);
                        });
    }

    @GetMapping("products/category/{categoryId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<List<ProductDTO>>>> findProductsByCategoryId(@PathVariable Long categoryId) {
        return productService.findProductsByCategoryId(categoryId)
                .thenApplyAsync(productDTOS -> {
                    ResponseWrapper<List<ProductDTO>> response = new ResponseWrapper<>(productDTOS, null);
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                }).exceptionally(ex -> {
                    ResponseWrapper<List<ProductDTO>> responseWrapper = new ResponseWrapper<>(null, ex.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseWrapper);
                });
    }

    // Find Products By Subcategory ID
    @GetMapping("products/subcategory/{subcategoryId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<List<ProductDTO>>>> findProductsBySubCategory(@PathVariable Long subcategoryId) {
        return productService.findProductsBySubCategory(subcategoryId)
                .thenApplyAsync(productDTOS -> {
                    ResponseWrapper<List<ProductDTO>> response = new ResponseWrapper<>(productDTOS, null);
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                }).exceptionally(ex -> {
                    ResponseWrapper<List<ProductDTO>> responseWrapper = new ResponseWrapper<>(null, ex.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseWrapper);
                });
        }

    @PostMapping("admin/products/add")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> insertProduct(@Valid @RequestBody ProductInsertDTO productInsertDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Handle validation errors
            String errorMessages = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            ResponseWrapper<Void> responseWrapper = new ResponseWrapper<>(null, errorMessages);
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWrapper));
        }
        return productService.insertProduct(productInsertDTO)
                .thenApplyAsync(result -> {
                    if (!result.isSuccess()) {
                        ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, result.getErrorMessage());
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
                    } else {
                        ResponseWrapper<Void> response = new ResponseWrapper<>(result.getData(), null);
                        return ResponseEntity.status(HttpStatus.CREATED).body(response);
                    }
                } );
    }


    // Update Product
    @PutMapping("products/{productId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> updateProduct(@PathVariable Long productId, @RequestBody ProductInsertDTO productInsertDTO) {
        return productService.updateProduct(productId, productInsertDTO)
                .thenApplyAsync(result -> {
                    if (!result.isSuccess()) {
                        if (result.getErrorMessage().contains("Product")) {
                            ResponseWrapper<Void> errorResponseNotFound = new ResponseWrapper<>(null, result.getErrorMessage());
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseNotFound);
                        } else {
                            ResponseWrapper<Void> errorResponseBadRequest = new ResponseWrapper<>(null, result.getErrorMessage());
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseBadRequest);
                        }
                    } else {
                        ResponseWrapper<Void> response = new ResponseWrapper<>(null, null);
                        return ResponseEntity.status(HttpStatus.OK).body(response);
                    }
                }).exceptionally(ex -> {
                    ResponseWrapper<Void> responseWrapper = new ResponseWrapper<>(null, ex.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseWrapper);
                });
    }

    // Delete Product
    @DeleteMapping("products/{productId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> deleteProduct(@PathVariable Long productId) {
        return productService.deleteProduct(productId)
                .thenApplyAsync(result -> {
                    if (!result.isSuccess()) {
                        ResponseWrapper<Void> responseWrapper = new ResponseWrapper<>(null, result.getErrorMessage());
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
                    } else {
                        ResponseWrapper<Void> responseWrapper = new ResponseWrapper<>(null,null);
                        return ResponseEntity.status(HttpStatus.OK).body(responseWrapper);
                    }
                }).exceptionally(ex -> {
            ResponseWrapper<Void> responseWrapper = new ResponseWrapper<>(null, ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseWrapper);
        });

    }
}
