package microservice.product_service.app.application.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import microservice.product_service.app.application.port.in.query.SearchProductsQuery;
import microservice.product_service.app.application.port.out.ProductRepository;
import microservice.product_service.app.domain.model.Product;
import microservice.product_service.app.domain.specification.ProductSearchCriteria;

@Service
public class SearchProductsUseCase {
  private final ProductRepository repository;

  @Autowired
  public SearchProductsUseCase(ProductRepository repository) {
    this.repository = repository;
  }

  public Page<Product> searchProducts(SearchProductsQuery query) {
    if (query == null) {
      return null;
    }

    ProductSearchCriteria criteria = ProductSearchCriteria.builder()
        .name(query.getName())
        .category(query.getCategory())
        .manufacturer(query.getManufacturer())
        .requiresPrescription(query.getRequiresPrescription())
        .onlyActive(Boolean.TRUE.equals(query.getOnlyAvailable()))
        .excludeDeleted(true)
        .page(query.getPage())
        .size(query.getSize())
        .build();

    return repository.search(criteria);
  }
}
