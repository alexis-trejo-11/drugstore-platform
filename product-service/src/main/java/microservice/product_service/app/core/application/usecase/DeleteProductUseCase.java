package microservice.product_service.app.core.application.usecase;

import microservice.product_service.app.core.domain.model.valueobjects.ProductID;
import microservice.product_service.app.core.port.output.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import microservice.product_service.app.core.domain.exception.ProductNotFoundException;

@Service
public class DeleteProductUseCase {
  private final ProductRepository repository;

  @Autowired
  public DeleteProductUseCase(ProductRepository repository) {
    this.repository = repository;
  }

  public void deleteProduct(ProductID productId) {
    boolean exists = repository.existsByID(productId);
    if (!exists) {
      throw new ProductNotFoundException(productId);
    }

    repository.deleteByID(productId);
  }
}
