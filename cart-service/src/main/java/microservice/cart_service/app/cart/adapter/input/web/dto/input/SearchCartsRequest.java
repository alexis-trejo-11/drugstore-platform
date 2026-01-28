package microservice.cart_service.app.cart.adapter.input.web.dto.input;

import microservice.cart_service.app.cart.core.application.queries.SearchCartsQuery;
import microservice.cart_service.app.cart.core.domain.specficication.CartSearchCriteria;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SearchCartsRequest(
		String cartId,
		String customerId,
		LocalDateTime createdAfter,
		LocalDateTime createdBefore,
		LocalDateTime updatedAfter,
		LocalDateTime updatedBefore,

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

		Boolean hasAfterwardItems,
		List<String> afterwardProductIds,
		Integer minAfterwardQuantity,
		Integer maxAfterwardQuantity,
		LocalDateTime afterwardAddedAfter,
		LocalDateTime afterwardAddedBefore,

		Boolean productAvailable,
		BigDecimal minProductPrice,
		BigDecimal maxProductPrice,


		Boolean includeCartItems,
		Boolean includeAfterwardItems,

		String sortBy,
		String sortDirection
) {

	public SearchCartsQuery toQuery(Pageable pageable) {
		var criteria = CartSearchCriteria.builder()
				.cartId(cartId)
				.customerId(customerId)
				.createdAfter(createdAfter)
				.createdBefore(createdBefore)
				.updatedAfter(updatedAfter)
				.updatedBefore(updatedBefore)
				.productIds(productIds)
				.productName(productName)
				.productNameContains(productNameContains)
				.hasItems(hasItems)
				.minItemQuantity(minItemQuantity)
				.maxItemQuantity(maxItemQuantity)
				.minItemPrice(minItemPrice)
				.maxItemPrice(maxItemPrice)
				.minDiscountPerUnit(minDiscountPerUnit)
				.maxDiscountPerUnit(maxDiscountPerUnit)
				.minTotalItems(minTotalItems)
				.maxTotalItems(maxTotalItems)
				.hasAfterwardItems(hasAfterwardItems)
				.afterwardProductIds(afterwardProductIds)
				.minAfterwardQuantity(minAfterwardQuantity)
				.maxAfterwardQuantity(maxAfterwardQuantity)
				.afterwardAddedAfter(afterwardAddedAfter)
				.afterwardAddedBefore(afterwardAddedBefore)
				.productAvailable(productAvailable)
				.minProductPrice(minProductPrice)
				.maxProductPrice(maxProductPrice)
				.includeCartItems(includeCartItems)
				.includeAfterwardItems(includeAfterwardItems)
				.sortBy(sortBy)
				.sortDirection(sortDirection)
				.build();

		return new SearchCartsQuery(criteria, pageable);
	}
}
