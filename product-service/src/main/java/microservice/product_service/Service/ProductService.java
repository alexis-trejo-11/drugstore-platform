package microservice.product_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Product.ProductDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.ProductInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.ProductUpdateDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ProductService {
    List<ProductDTO> getProductsById(List<Long> productId);
    ProductDTO getProductById(Long productId);
    Page<ProductDTO> getAllProductsSortedByCategoryHierarchy(Pageable pageable);
    Page<ProductDTO> getProductsBySupplier(Long supplierId, Pageable pageable);
    Page<ProductDTO> getProductsByCategoryId(Long categoryId, Pageable pageable);
    Page<ProductDTO> getProductsBySubCategory(Long subcategoryId, Pageable pageable);

    Result<Void> createProduct(ProductInsertDTO productInsertDTO);
    Result<Void> updateProduct(ProductUpdateDTO productUpdateDTO);
    void deleteProduct(Long productId);
    }
