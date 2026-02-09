package microservice.product_service.app.application.port.input.usecase;

import microservice.product_service.app.application.port.input.command.CreateProductCommand;
import microservice.product_service.app.application.port.input.command.UpdateProductCommand;
import microservice.product_service.app.domain.model.Product;
import microservice.product_service.app.domain.model.valueobjects.ProductID;

public interface ProductCommandUseCases {
  Product createProduct(CreateProductCommand command);

  Product updateProduct(UpdateProductCommand command);

  void deleteProduct(ProductID productId);

  void restoreProduct(ProductID productId);
}
