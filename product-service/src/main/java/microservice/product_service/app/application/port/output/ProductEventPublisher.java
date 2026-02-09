package microservice.product_service.app.application.port.output;

import microservice.product_service.app.domain.model.Product;
import microservice.product_service.app.domain.model.valueobjects.ProductID;

public interface ProductEventPublisher {
    void publishProductCreated(Product product);
    void publishProductUpdated(Product product);
    void publishProductDeleted(ProductID productId);
    void publishStockUpdated(ProductID productId, int oldStock, int newStock);
}