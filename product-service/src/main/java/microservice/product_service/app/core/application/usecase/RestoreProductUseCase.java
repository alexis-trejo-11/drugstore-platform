package microservice.product_service.app.core.application.usecase;

import microservice.product_service.app.core.port.output.ProductRepository;
import microservice.product_service.app.core.domain.exception.ProductValidationException;
import microservice.product_service.app.core.domain.model.Product;
import microservice.product_service.app.core.domain.model.valueobjects.ProductID;
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
    Product deletedProduct = repository.findDeletedByID(productId)
        .orElseThrow(() -> new ProductValidationException("Product not found in deleted records"));

    deletedProduct.restore();
    repository.save(deletedProduct);
  }
}
