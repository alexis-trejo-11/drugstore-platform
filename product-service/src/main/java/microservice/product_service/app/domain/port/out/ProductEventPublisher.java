package microservice.product_service.app.domain.port.out;

import microservice.product_service.app.domain.model.Product;
import microservice.product_service.app.domain.model.ProductId;

public interface ProductEventPublisher {
    void publishProductCreated(Product product);
    void publishProductUpdated(Product product);
    void publishProductDeleted(ProductId productId);
    void publishStockUpdated(ProductId productId, int oldStock, int newStock);
}