package microservice.product_service.app.application.usecase;

import microservice.product_service.app.domain.model.Product;
import microservice.product_service.app.application.port.in.query.SearchProductsQuery;
import microservice.product_service.app.application.port.out.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchProductsUseCase {
  private final ProductRepository repository;

  @Autowired
  public SearchProductsUseCase(ProductRepository repository) {
    this.repository = repository;
  }

  public List<Product> searchProducts(SearchProductsQuery query) {
    return repository.findAll();
  }
}
