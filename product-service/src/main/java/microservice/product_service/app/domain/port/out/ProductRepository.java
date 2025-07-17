package microservice.product_service.app.domain.port.out;

import microservice.product_service.app.domain.model.Product;
import microservice.product_service.app.domain.model.ProductCategory;
import microservice.product_service.app.domain.model.ProductId;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(ProductId productId);
    void deleteById(ProductId productId);
    boolean existsById(ProductId productId);
    List<Product> findAll();
    List<Product> findByCategory(ProductCategory category);
    List<Product> findByManufacturer(String manufacturer);
    List<Product> findByNameContaining(String name);
    List<Product> findExpiredProducts();
    List<Product> findLowStockProducts(int threshold);
}
