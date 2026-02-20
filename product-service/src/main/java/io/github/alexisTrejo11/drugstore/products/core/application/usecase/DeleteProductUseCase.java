package io.github.alexisTrejo11.drugstore.products.core.application.usecase;

import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.ProductID;
import io.github.alexisTrejo11.drugstore.products.core.port.output.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.alexisTrejo11.drugstore.products.core.domain.exception.ProductNotFoundException;

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
