package microservice.product_service.app.core.port.input.usecase;

import microservice.product_service.app.core.port.input.command.CreateProductCommand;
import microservice.product_service.app.core.port.input.command.UpdateProductCommand;
import microservice.product_service.app.core.domain.model.Product;
import microservice.product_service.app.core.domain.model.valueobjects.ProductID;

public interface ProductCommandUseCases {
  Product createProduct(CreateProductCommand command);

  Product updateProduct(UpdateProductCommand command);

  void deleteProduct(ProductID productId);

  void restoreProduct(ProductID productId);
}
