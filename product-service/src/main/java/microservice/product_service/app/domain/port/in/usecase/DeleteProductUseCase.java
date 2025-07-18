package microservice.product_service.app.domain.port.in.usecase;

import microservice.product_service.app.domain.model.ProductId;

public interface DeleteProductUseCase {
    void deleteProduct(ProductId productId);
}
