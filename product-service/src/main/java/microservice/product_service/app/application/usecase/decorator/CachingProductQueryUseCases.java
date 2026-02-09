package microservice.product_service.app.application.usecase.decorator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import microservice.product_service.app.application.port.input.query.GetProductByBarCodeQuery;
import microservice.product_service.app.application.port.input.query.GetProductByIDQuery;
import microservice.product_service.app.application.port.input.query.GetProductBySKUQuery;
import microservice.product_service.app.application.port.input.query.SearchProductsQuery;
import microservice.product_service.app.application.port.input.usecase.ProductQueryUseCases;
import microservice.product_service.app.application.usecase.JoinedProductUseCases;
import microservice.product_service.app.domain.model.Product;

@Service
@Primary
public class CachingProductQueryUseCases implements ProductQueryUseCases {

  private final JoinedProductUseCases delegate;
  private final Cache productByIdCache;
  private final Cache productBySKUCache;
  private final Cache productByBarcodeCache;
  private final Cache productSearchCache;
  private final ObjectMapper objectMapper;

  @Autowired
  public CachingProductQueryUseCases(JoinedProductUseCases delegate, CacheManager cacheManager,
      ObjectMapper objectMapper) {
    this.delegate = delegate;
    this.productByIdCache = cacheManager.getCache("productById");
    this.productBySKUCache = cacheManager.getCache("productBySKU");
    this.productByBarcodeCache = cacheManager.getCache("productByBarcode");
    this.productSearchCache = cacheManager.getCache("productSearch");
    this.objectMapper = objectMapper;
  }

  @Override
  public Product getProductByID(GetProductByIDQuery query) {
    if (query == null) {
      return null;
    }

    String key = query.productId().toString();
    if (productByIdCache == null) {
      return delegate.getProductByID(query);
    }

    Cache.ValueWrapper wrapper = productByIdCache.get(key);
    if (wrapper != null) {
      Object cached = wrapper.get();
      if (cached instanceof Product) {
        return (Product) cached;
      }
      // convert from LinkedHashMap (JSON) to Product
      try {
        return objectMapper.convertValue(cached, Product.class);
      } catch (IllegalArgumentException ex) {
        // fallback to delegate if conversion fails
        return delegate.getProductByID(query);
      }
    }

    Product product = delegate.getProductByID(query);
    productByIdCache.put(key, product);
    return product;
  }

  @Override
  public Product getProductBySKU(GetProductBySKUQuery query) {
    if (query == null) {
      return null;
    }

    String key = query.sku().getCode();
    if (productBySKUCache == null) {
      return delegate.getProductBySKU(query);
    }

    Cache.ValueWrapper wrapper = productBySKUCache.get(key);
    if (wrapper != null) {
      Object cached = wrapper.get();
      if (cached instanceof Product) {
        return (Product) cached;
      }
      // convert from LinkedHashMap (JSON) to Product
      try {
        return objectMapper.convertValue(cached, Product.class);
      } catch (IllegalArgumentException ex) {
        // fallback to delegate if conversion fails
        return delegate.getProductBySKU(query);
      }
    }

    Product product = delegate.getProductBySKU(query);
    if (product != null) {
      productBySKUCache.put(key, product);
    }
    return product;
  }

  @Override
  public Product getProductByBarcode(GetProductByBarCodeQuery query) {
    if (query == null) {
      return null;
    }

    String key = query.barCode();
    if (productByBarcodeCache == null) {
      return delegate.getProductByBarcode(query);
    }

    Cache.ValueWrapper wrapper = productByBarcodeCache.get(key);
    if (wrapper != null) {
      Object cached = wrapper.get();
      if (cached instanceof Product) {
        return (Product) cached;
      }
      // convert from LinkedHashMap (JSON) to Product
      try {
        return objectMapper.convertValue(cached, Product.class);
      } catch (IllegalArgumentException ex) {
        // fallback to delegate if conversion fails
        return delegate.getProductByBarcode(query);
      }
    }

    Product product = delegate.getProductByBarcode(query);
    if (product != null) {
      productByBarcodeCache.put(key, product);
    }
    return product;
  }

  @Override
  public Page<Product> searchProducts(SearchProductsQuery query) {
    if (query == null) {
      return null;
    }

    String key = buildSearchKey(query);
    if (productSearchCache == null) {
      return delegate.searchProducts(query);
    }

    Cache.ValueWrapper wrapper = productSearchCache.get(key);
    if (wrapper != null) {
      Object cached = wrapper.get();
      if (cached instanceof Page) {
        return (Page<Product>) cached;
      }
      try {
        var pageType = objectMapper.getTypeFactory().constructParametricType(PageImpl.class, Product.class);
        Page<Product> cachedPage = objectMapper.convertValue(cached, pageType);
        return cachedPage;
      } catch (IllegalArgumentException ex) {
        return delegate.searchProducts(query);
      }
    }

    Page<Product> page = delegate.searchProducts(query);
    productSearchCache.put(key, page);
    return page;
  }

  private String buildSearchKey(SearchProductsQuery q) {
    return String.format("%s|%s|%s|%s|%s|%d|%d",
        q.getName(),
        q.getCategory() == null ? "" : q.getCategory().name(),
        q.getManufacturer(),
        q.getRequiresPrescription(),
        q.getOnlyAvailable(),
        q.getPage(),
        q.getSize());
  }
}
