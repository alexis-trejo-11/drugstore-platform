package microservice.product_service.app.application.usecase;

import microservice.product_service.app.domain.model.valueobjects.ProductID;
import microservice.product_service.app.application.port.out.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import microservice.product_service.app.domain.exception.ProductNotFoundException;

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
