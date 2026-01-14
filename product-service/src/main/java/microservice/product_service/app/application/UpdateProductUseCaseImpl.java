package microservice.product_service.app.application;

import microservice.product_service.app.application.mapper.ProductMapper;
import microservice.product_service.app.domain.exception.ProductNotFoundException;
import microservice.product_service.app.domain.model.Product;
import microservice.product_service.app.application.port.in.command.UpdateProductCommand;
import microservice.product_service.app.application.port.in.usecase.UpdateProductUseCase;
import microservice.product_service.app.application.port.out.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateProductUseCaseImpl implements UpdateProductUseCase {
  private final ProductRepository repository;
  private final ProductMapper mapper;

  @Autowired
  public UpdateProductUseCaseImpl(ProductRepository repository, ProductMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public Product updateProduct(UpdateProductCommand command) {
    Product existingProduct = repository.findByID(command.getProductId())
        .orElseThrow(() -> new ProductNotFoundException(command.getProductId()));

    mapper.applyUpdate(existingProduct, command);
    return repository.save(existingProduct);
  }
}
