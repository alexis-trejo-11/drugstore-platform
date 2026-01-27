package microservice.cart_service.app.product.adapter.in.dto;

import microservice.cart_service.app.product.core.domain.Product;

public record ProductResponse() {

	public static ProductResponse from(Product product) {
		return new ProductResponse();
	}
}
