package microservice.product_service.app.application.usecase.decorator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import microservice.product_service.app.application.port.in.command.CreateProductCommand;
import microservice.product_service.app.application.port.in.command.UpdateProductCommand;
import microservice.product_service.app.application.port.in.usecase.ProductCommandUseCases;
import microservice.product_service.app.application.usecase.JoinedProductUseCases;
import microservice.product_service.app.domain.model.Product;
import microservice.product_service.app.domain.model.valueobjects.ProductID;

@Service
@Primary
public class CachingProductCommandUseCases implements ProductCommandUseCases {

  private final JoinedProductUseCases delegate;
  private final CacheManager cacheManager;

  @Autowired
  public CachingProductCommandUseCases(JoinedProductUseCases delegate, CacheManager cacheManager) {
    this.delegate = delegate;
    this.cacheManager = cacheManager;
  }

  @Override
  public Product createProduct(CreateProductCommand command) {
    Product product = delegate.createProduct(command);
    evictCachesForProduct(product.getId().toString());
    return product;
  }

  @Override
  public Product updateProduct(UpdateProductCommand command) {
    Product product = delegate.updateProduct(command);
    evictCachesForProduct(product.getId().toString());
    return product;
  }

  @Override
  public void deleteProduct(ProductID productId) {
    delegate.deleteProduct(productId);
    evictCachesForProduct(productId.toString());
  }

  @Override
  public void restoreProduct(ProductID productId) {
    delegate.restoreProduct(productId);
    evictCachesForProduct(productId.toString());
  }

  private void evictCachesForProduct(String id) {
    Cache byId = cacheManager.getCache("productById");
    if (byId != null) {
      byId.evict(id);
    }

    // Clear entire SKU and barcode caches since we don't have the specific keys
    Cache bySKU = cacheManager.getCache("productBySKU");
    if (bySKU != null) {
      bySKU.clear();
    }

    Cache byBarcode = cacheManager.getCache("productByBarcode");
    if (byBarcode != null) {
      byBarcode.clear();
    }

    Cache search = cacheManager.getCache("productSearch");
    if (search != null) {
      search.clear();
    }
  }
}
