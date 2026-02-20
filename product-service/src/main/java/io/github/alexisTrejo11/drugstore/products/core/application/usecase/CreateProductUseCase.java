package io.github.alexisTrejo11.drugstore.products.core.application.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.alexisTrejo11.drugstore.products.core.port.input.command.CreateProductCommand;
import io.github.alexisTrejo11.drugstore.products.core.port.output.ProductRepository;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.CreateProductParams;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.Product;

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
