package microservice.product_service.app.application.usecase;

import microservice.product_service.app.application.mapper.ProductMapper;
import microservice.product_service.app.domain.model.Product;
import microservice.product_service.app.application.port.in.command.CreateProductCommand;
import microservice.product_service.app.application.port.out.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateProductUseCase {
  private final ProductRepository repository;
  private final ProductMapper mapper;

  @Autowired
  public CreateProductUseCase(ProductRepository repository, ProductMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  public Product createProduct(CreateProductCommand command) {
    Product newProduct = mapper.createCommandToProduct(command);
    return repository.save(newProduct);
  }
}
