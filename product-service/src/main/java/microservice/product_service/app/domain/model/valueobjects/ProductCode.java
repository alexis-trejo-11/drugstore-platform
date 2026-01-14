package microservice.product_service.app.domain.model.valueobjects;

import microservice.product_service.app.domain.exception.ProductValueObjectException;

public record ProductCode(String value) {
	public static final ProductCode NONE = new ProductCode("");


	public static ProductCode create(String value) {
		if (value == null || value.trim().isEmpty()) {
			throw new ProductValueObjectException("ProductCode", "Product code cannot be null or empty");
		}
		if (!value.matches("[A-Z0-9]{3,20}")) {
			throw new ProductValueObjectException("ProductCode", "Product code must be 3-20 alphanumeric characters");
		}

		return new ProductCode(value);
	}
}
