package microservice.product_service.app.core.application.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import microservice.product_service.app.core.port.input.command.CreateProductCommand;
import microservice.product_service.app.core.port.output.ProductRepository;
import microservice.product_service.app.core.domain.model.CreateProductParams;
import microservice.product_service.app.core.domain.model.Product;

@Service
public class CreateProductUseCase {
  private final ProductRepository repository;

  @Autowired
  public CreateProductUseCase(ProductRepository repository) {
    this.repository = repository;
  }

  public Product createProduct(CreateProductCommand command) {
    CreateProductParams params = command.toCreateParams();
    Product newProduct = Product.create(params);

    return repository.save(newProduct);
  }
}
