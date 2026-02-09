package microservice.cart_service.app.product.core.application;

import lombok.Builder;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.ProductId;
import microservice.cart_service.app.product.core.domain.Product;

import java.math.BigDecimal;

@Builder
public record ProductInsertCommand(
		String id,
		String name,
		BigDecimal unitPrice,
		BigDecimal discountPerUnit,
		String description,
		boolean available
) {
	public Product.CreateProductParams toParams() {
		return new Product.CreateProductParams(
				new ProductId(this.id),
				this.name,
				this.unitPrice,
				this.discountPerUnit,
				this.description,
				this.available
		);
	}

	public ProductInsertCommand {
		if (id == null || id.isEmpty()) {
			throw new IllegalArgumentException("Product ID cannot be null or empty");
		}
	}
}
