package microservice.product_service.app.domain.port.in.usecase;

import microservice.product_service.app.domain.model.Product;
import microservice.product_service.app.domain.model.ProductId;

public interface UpdateStockUseCase {
    Product updateStock(ProductId productId, int quantity);
}
