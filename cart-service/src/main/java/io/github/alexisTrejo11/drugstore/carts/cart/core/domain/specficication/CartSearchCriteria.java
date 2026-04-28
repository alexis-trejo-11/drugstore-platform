package io.github.alexisTrejo11.drugstore.carts.cart.core.domain.specficication;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;

@Builder
public record CartSearchCriteria(
		// Cart filters
		String cartId,
		String customerId,
		LocalDateTime createdAfter,
		LocalDateTime createdBefore,
		LocalDateTime updatedAfter,
		LocalDateTime updatedBefore,

		// Cart item filters
		List<String> productIds,
		String productName,
		String productNameContains,
		Boolean hasItems,
		Integer minItemQuantity,
		Integer maxItemQuantity,
		BigDecimal minItemPrice,
		BigDecimal maxItemPrice,
		BigDecimal minDiscountPerUnit,
		BigDecimal maxDiscountPerUnit,
		Integer minTotalItems,
		Integer maxTotalItems,

		// Afterward items filters
		Boolean hasAfterwardItems,
		List<String> afterwardProductIds,
		Integer minAfterwardQuantity,
		Integer maxAfterwardQuantity,
		LocalDateTime afterwardAddedAfter,
		LocalDateTime afterwardAddedBefore,

		// Product filters (for items in cart)
		Boolean productAvailable,
		BigDecimal minProductPrice,
		BigDecimal maxProductPrice,

		// Combination filters
		boolean includeCartItems,
		boolean includeAfterwardItems,

		// Ordering
		String sortBy,
		String sortDirection) {

	public static CartSearchCriteria defaultCriteria() {
		return CartSearchCriteria.builder()
				.includeCartItems(Boolean.TRUE)
				.includeAfterwardItems(Boolean.TRUE)
				.sortBy("updatedAt")
				.sortDirection("DESC")
				.build();
	}

}
