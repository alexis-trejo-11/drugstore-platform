package io.github.alexisTrejo11.drugstore.products.core.port.input.usecase;

import io.github.alexisTrejo11.drugstore.products.core.port.input.command.CreateProductCommand;
import io.github.alexisTrejo11.drugstore.products.core.port.input.command.UpdateProductCommand;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.Product;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.ProductID;

public interface ProductCommandUseCases {
  Product createProduct(CreateProductCommand command);

  Product updateProduct(UpdateProductCommand command);

  void deleteProduct(ProductID productId);

  void restoreProduct(ProductID productId);
}
