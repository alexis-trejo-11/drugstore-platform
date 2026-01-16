package microservice.product_service.app.application.usecase.decorator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import microservice.product_service.app.application.port.in.query.GetProductByIDQuery;
import microservice.product_service.app.application.port.in.query.SearchProductsQuery;
import microservice.product_service.app.application.port.in.usecase.ProductQueryUseCases;
import microservice.product_service.app.application.usecase.JoinedProductUseCases;
import microservice.product_service.app.domain.model.Product;

@Service
@Primary
public class CachingProductQueryUseCases implements ProductQueryUseCases {

  private final JoinedProductUseCases delegate;
  private final Cache productByIdCache;
  private final Cache productSearchCache;
  private final ObjectMapper objectMapper;

  @Autowired
  public CachingProductQueryUseCases(JoinedProductUseCases delegate, CacheManager cacheManager,
      ObjectMapper objectMapper) {
    this.delegate = delegate;
    this.productByIdCache = cacheManager.getCache("productById");
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
