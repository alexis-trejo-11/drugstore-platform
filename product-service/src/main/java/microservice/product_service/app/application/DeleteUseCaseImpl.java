package microservice.product_service.app.application;

import microservice.product_service.app.domain.model.valueobjects.ProductID;
import microservice.product_service.app.application.port.in.usecase.DeleteProductUseCase;
import microservice.product_service.app.application.port.out.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteUseCaseImpl implements DeleteProductUseCase {
  private final ProductRepository repository;

  @Autowired
  public DeleteUseCaseImpl(ProductRepository repository) {
    this.repository = repository;
  }

  @Override
  public void deleteProduct(ProductID productId) {
    boolean exists = repository.existsByID(productId);
    if (!exists) {
      throw new RuntimeException();
    }

    repository.deleteByID(productId);
  }
}
