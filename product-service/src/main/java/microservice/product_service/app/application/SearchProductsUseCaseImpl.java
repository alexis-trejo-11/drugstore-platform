package microservice.product_service.app.application;

import microservice.product_service.app.domain.model.Product;
import microservice.product_service.app.application.port.in.query.SearchProductsQuery;
import microservice.product_service.app.application.port.in.usecase.SearchProductsUseCase;
import microservice.product_service.app.application.port.out.ProductRepository;
import microservice.product_service.app.domain.specification.ProductSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchProductsUseCaseImpl implements SearchProductsUseCase {
  private final ProductRepository repository;

  @Autowired
    public SearchProductsUseCaseImpl(ProductRepository repository) {
        this.repository = repository;
    }

@Override
    public List<Product> searchProducts(SearchProductsQuery query) {
        ProductSearchCriteria criteria = ProductSearchCriteria.builder()
                .name(query.getName())
                .category(query.getCategory())
                .manufacturer(query.getManufacturer())
                .requiresPrescription(query.getRequiresPrescription())
                .onlyActive(query.getOnlyAvailable())
                .excludeDeleted(true)
                .page(query.getPage())
                .size(query.getSize())
                .build();

        Page<Product> page = repository.search(criteria);
        return page.getContent();
