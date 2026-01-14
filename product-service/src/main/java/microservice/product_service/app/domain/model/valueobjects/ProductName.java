package microservice.product_service.app.domain.model.valueobjects;

import microservice.product_service.app.domain.exception.ProductValueObjectException;

public record ProductName(String value) {
	public static final ProductName NONE = new ProductName("");

	public static ProductName create(String value) {
		if (value == null || value.trim().isEmpty()) {
			throw new ProductValueObjectException("ProductName", "Product name cannot be null or empty");
		}
		if (value.length() > 100) {
			throw new ProductValueObjectException("ProductName", "Product name cannot exceed 100 characters");
		}
		return new ProductName(value);
	}
}
