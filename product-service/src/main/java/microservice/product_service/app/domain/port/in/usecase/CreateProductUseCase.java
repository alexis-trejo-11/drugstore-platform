package microservice.product_service.app.domain.port.in.usecase;


import microservice.product_service.app.domain.model.Product;
import microservice.product_service.app.domain.port.in.command.CreateProductCommand;

public interface CreateProductUseCase {
    Product createProduct(CreateProductCommand command);
}

