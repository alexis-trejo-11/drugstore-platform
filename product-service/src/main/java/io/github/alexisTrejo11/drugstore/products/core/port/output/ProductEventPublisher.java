package io.github.alexisTrejo11.drugstore.products.core.port.output;

import io.github.alexisTrejo11.drugstore.products.core.domain.model.Product;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.ProductID;

public interface ProductEventPublisher {
    void publishProductCreated(Product product);
    void publishProductUpdated(Product product);
    void publishProductDeleted(ProductID productId);
    void publishStockUpdated(ProductID productId, int oldStock, int newStock);
}