package io.github.alexisTrejo11.drugstore.products.core.application.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import io.github.alexisTrejo11.drugstore.products.core.port.input.query.SearchProductsQuery;
import io.github.alexisTrejo11.drugstore.products.core.port.output.ProductRepository;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.Product;
import io.github.alexisTrejo11.drugstore.products.core.domain.specification.ProductSearchCriteria;

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
