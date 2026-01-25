package microservice.cart_service.app.product.core.application;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import microservice.cart_service.app.cart.core.domain.exception.ProductNotFoundException;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.ProductId;
import microservice.cart_service.app.product.core.domain.Product;
import microservice.cart_service.app.product.core.port.out.ProductRepository;

@Service
public class ProductService {
  private final ProductRepository productRepository;

  @Autowired
  public ProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  public List<Product> getAllProducts() {
    // Implementation to retrieve all products
    return Collections.emptyList();
  }

  public List<Product> findAvailableByIdIn(List<ProductId> productIds) {
    var products = productRepository.findAvailableByIdIn(productIds);

    List<String> foundProductIds = products.stream()
        .map(product -> product.getId().toString())
        .toList();

    List<String> missingProductIds = productIds.stream()
        .map(ProductId::toString)
        .filter(id -> !foundProductIds.contains(id))
        .toList();

    if (!missingProductIds.isEmpty()) {
      throw new ProductNotFoundException(String.valueOf(missingProductIds));
    }
    return products;
  }
}
