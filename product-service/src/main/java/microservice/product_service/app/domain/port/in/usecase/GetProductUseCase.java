package microservice.product_service.app.domain.port.in.usecase;

import microservice.product_service.app.domain.model.Product;
import microservice.product_service.app.domain.model.ProductId;

public interface GetProductUseCase {
    Product getProduct(ProductId productId);
}
