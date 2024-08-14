package microservice.product_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Product.ProductDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.ProductInsertDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ProductService {
    CompletableFuture<List<ProductDTO>> getAllProducts();
    CompletableFuture<List<ProductDTO>> getProductsById(List<Long> productId);
    CompletableFuture<ProductDTO> getProductById(Long productId);
    CompletableFuture<List<ProductDTO>> findProductsBySupplier(Long supplierId);
    CompletableFuture<List<ProductDTO>> findProductsByCategoryId(Long categoryId);
    CompletableFuture<List<ProductDTO>> findProductsBySubCategory(Long subcategoryId);
    CompletableFuture<Result<Void>> processInsertProduct(ProductInsertDTO productInsertDTO);
    CompletableFuture<Boolean> deleteProduct(Long productId);
    CompletableFuture<Result<Void>> updateProduct(Long productId, ProductInsertDTO productInsertDTO);
    }
