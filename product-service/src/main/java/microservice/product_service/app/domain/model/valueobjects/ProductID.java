package microservice.product_service.app.domain.model.valueobjects;

import java.util.UUID;

public record ProductID(String value) {
	public static ProductID generate() {
		return new ProductID(UUID.randomUUID().toString());
	}

	public static ProductID from(String value) {
		return new ProductID(value);
	}

	public static ProductID from(UUID value) {
		return new ProductID(value.toString());
	}

	@Override
	public String toString() {
		return value.toString();
	}
}
