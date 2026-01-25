package microservice.cart_service.app.product.core.port.in;

import microservice.cart_service.app.cart.core.domain.model.valueobjects.ProductId;
import microservice.cart_service.app.product.core.domain.Product;

import java.util.List;
import java.util.Set;

// Use cases for product-related operations.
//
// Product Use Cases only has query methods cause the only way to modify products
// is through listening events from product-service.
public interface ProductUseCases {
  List<Product> findAvailableByIdIn(Set<ProductId> productIds);

  List<Product> findByIdIn(List<ProductId> productIds);

  // TODO: Add search
}
