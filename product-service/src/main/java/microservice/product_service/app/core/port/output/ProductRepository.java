package microservice.product_service.app.core.port.output;

import microservice.product_service.app.core.domain.model.Product;
import microservice.product_service.app.core.domain.model.valueobjects.SKU;
import microservice.product_service.app.core.domain.model.valueobjects.ProductID;
import microservice.product_service.app.core.domain.specification.ProductSearchCriteria;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface ProductRepository {
  Product save(Product product);

  Optional<Product> findByID(ProductID id);

  Optional<Product> findBySKU(SKU sku);

  Optional<Product> findByBarCode(String barcode);

  Page<Product> search(ProductSearchCriteria criteria);

  long count(ProductSearchCriteria criteria);

  boolean existsBySKU(SKU sku);

  boolean existsByBarCode(String barcode);

  boolean existsByID(ProductID id);

  void deleteByID(ProductID id);

  Optional<Product> findDeletedByID(ProductID id);
}
