package microservice.product_service.app.application.usecase;

import microservice.product_service.app.application.mapper.ProductMapper;
import microservice.product_service.app.domain.exception.ProductNotFoundException;
import microservice.product_service.app.domain.model.Product;
import microservice.product_service.app.application.port.in.command.UpdateProductCommand;
import microservice.product_service.app.application.port.out.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateProductUseCase {
  private final ProductRepository repository;
  private final ProductMapper mapper;

  @Autowired
  public UpdateProductUseCase(ProductRepository repository, ProductMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  public Product updateProduct(UpdateProductCommand command) {
    Product existingProduct = repository.findById(command.getProductId())
        .orElseThrow(() -> new ProductNotFoundException(command.getProductId()));

    Product productUpdated  = mapper.applyUpdate(existingProduct, command);
    return repository.save(productUpdated);
  }
}
