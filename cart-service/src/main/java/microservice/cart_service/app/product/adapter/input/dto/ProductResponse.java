package microservice.cart_service.app.product.adapter.input.dto;

import lombok.Builder;
import microservice.cart_service.app.product.core.domain.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ProductResponse(
		String id,
		String name,
		BigDecimal unitPrice,
		BigDecimal discountPerUnit,
		String description,
		boolean available,
		LocalDateTime createdAt,
		LocalDateTime updatedAt) {

	public static ProductResponse from(Product product) {
		return ProductResponse.builder()
				.id(product.getId() != null ? product.getId().value() : null)
				.name(product.getName())
				.unitPrice(product.getUnitPrice())
				.discountPerUnit(product.getDiscountPerUnit())
				.description(product.getDescription())
				.available(product.isAvailable())
				.createdAt(product.getCreatedAt())
				.updatedAt(product.getUpdatedAt())
				.build();
	}
}
