package microservice.product_service.app.core.port.output;

import microservice.product_service.app.core.domain.model.Product;
import microservice.product_service.app.core.domain.model.valueobjects.ProductID;

public interface ProductEventPublisher {
    void publishProductCreated(Product product);
    void publishProductUpdated(Product product);
    void publishProductDeleted(ProductID productId);
    void publishStockUpdated(ProductID productId, int oldStock, int newStock);
}