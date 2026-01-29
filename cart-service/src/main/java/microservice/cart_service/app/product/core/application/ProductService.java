package microservice.cart_service.app.product.core.application;

import libs_kernel.response.Result;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.ProductId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import microservice.cart_service.app.product.core.domain.Product;
import microservice.cart_service.app.product.core.port.in.ProductUseCases;
import microservice.cart_service.app.product.core.port.out.ProductRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ProductService implements ProductUseCases {
  private final ProductRepository productRepository;



  @Autowired
  public ProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  public List<ProductId> getUnavailableProductsIn(List<ProductId> productIds) {
    // Placeholder implementation
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public List<ProductId> getOutOfStockProductsIn(List<ProductId> productIds) {
    // Placeholder implementation
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Page<Product> getProducts(Pageable pageable) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Product getProductById(String productId) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void createProduct(Product product) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateProduct(Product product) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void deleteProduct(String productId) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public Result<Void> validateAllExistAndAvailableByIdIn( Map<ProductId, Integer> productQuantitiesMap) {
    return Result.success();
  }
}
