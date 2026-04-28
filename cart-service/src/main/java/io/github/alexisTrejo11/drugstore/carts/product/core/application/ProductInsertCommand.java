package io.github.alexisTrejo11.drugstore.carts.product.core.application;

import lombok.Builder;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.ProductId;
import io.github.alexisTrejo11.drugstore.carts.product.core.domain.Product;

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
