package io.github.alexisTrejo11.drugstore.carts.product.core.port.output;

import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.ProductId;
import io.github.alexisTrejo11.drugstore.carts.product.core.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
  Page<Product> findProducts(Pageable pageable);

  Optional<Product> findProductById(String productId);

  Product save(Product product);

  void delete(Product product);

  List<Product> findAvailableByIdIn(List<ProductId> productIds);

  boolean existsById(String productId);
}
