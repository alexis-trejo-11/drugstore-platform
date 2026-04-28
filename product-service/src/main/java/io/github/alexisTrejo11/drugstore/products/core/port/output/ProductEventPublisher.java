package io.github.alexisTrejo11.drugstore.products.core.port.output;

import io.github.alexisTrejo11.drugstore.products.core.domain.model.Product;

public interface ProductEventPublisher {
	void publishProductCreated(Product product);
	void publishProductUpdated(Product product);
	void publishProductDeleted(String productId, Product product);
}