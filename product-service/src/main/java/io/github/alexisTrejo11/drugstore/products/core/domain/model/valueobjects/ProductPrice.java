package io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects;


import io.github.alexisTrejo11.drugstore.products.core.domain.exception.ProductValueObjectException;

import java.math.BigDecimal;

public record ProductPrice(BigDecimal value) {

	public static ProductPrice create(BigDecimal value) {
		if (value == null) {
			throw new ProductValueObjectException("ProductPrice", "Price cannot be null");
		}
		if (value.compareTo(BigDecimal.ZERO) < 0) {
			throw new ProductValueObjectException("ProductPrice", "Price cannot be negative");
		}

		if (value.scale() > 2) {
			throw new ProductValueObjectException("ProductPrice", "Price cannot have more than two decimal places");
		}

		if (value.precision() - value.scale() > 10) {
			throw new ProductValueObjectException("ProductPrice", "Price integer part cannot have more than 10 digits");
		}
		return new ProductPrice(value);
	}

}
