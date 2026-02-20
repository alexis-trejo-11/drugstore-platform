package io.github.alexisTrejo11.drugstore.products.core.application.usecase;

import io.github.alexisTrejo11.drugstore.products.core.port.output.ProductRepository;
import io.github.alexisTrejo11.drugstore.products.core.domain.exception.ProductValidationException;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.Product;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.ProductID;
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
