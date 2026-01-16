package microservice.product_service.app.application.usecase;

import microservice.product_service.app.application.port.out.ProductRepository;
import microservice.product_service.app.domain.model.valueobjects.ProductID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RestoreProductUseCase {
  private final ProductRepository repository;

  @Autowired
  public RestoreProductUseCase(ProductRepository repository) {
    this.repository = repository;
  }

  public void restoreByID(ProductID productId) {
    repository.restoreByID(productId);
  }
}
