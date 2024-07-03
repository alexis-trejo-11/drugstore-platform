package microservice.product_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import at.backend.drugstore.microservice.common_models.DTO.Product.ProductInsertDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.product_service.Model.*;

import java.util.List;

public interface ProductService {
    List<ProductDTO> getAllProducts();
    List<ProductDTO> getProductsById(List<Long> productId);
    ProductDTO getProductById(Long productId);
    List<ProductDTO> FindProductsBySupplier(Long supplierId);
    List<ProductDTO> findProductsByCategoryId(Long categoryId);
    List<ProductDTO> findProductsBySubCategory(Long subcategoryId);
    Result<Void> processInsertProduct(ProductInsertDTO productInsertDTO);
    boolean deleteProduct(Long productId);
    Result<Void> handleRelationShips(ProductInsertDTO productInsertDTO, Product product);





    }
