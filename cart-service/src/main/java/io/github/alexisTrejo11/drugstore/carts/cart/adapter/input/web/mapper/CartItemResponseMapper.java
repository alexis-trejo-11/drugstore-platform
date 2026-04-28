package io.github.alexisTrejo11.drugstore.carts.cart.adapter.input.web.mapper;

import io.github.alexisTrejo11.drugstore.carts.cart.adapter.input.web.dto.output.AfterwardResponse;
import io.github.alexisTrejo11.drugstore.carts.cart.adapter.input.web.dto.output.CartItemResponse;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.AfterwardsItem;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.CartItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CartItemResponseMapper {

	public CartItemResponse fromDomain(CartItem cartItem) {
		if (cartItem == null) {
			return null;
		}

		return CartItemResponse.builder()
				.productId(cartItem.getProductId() != null ? cartItem.getProductId().value() : null)
				.productName(cartItem.getProductName())
				.quantity(cartItem.getQuantity() != null ? cartItem.getQuantity().value() : 0)
				.unitPrice(cartItem.getUnitPrice() != null ? cartItem.getUnitPrice().value() : null)
				.discountPerUnit(cartItem.getDiscountPerUnit())
				.subtotal(cartItem.calculateSubtotal() != null ? cartItem.calculateSubtotal().value() : null)
				.addedAt(cartItem.getTimeStamps() != null ? cartItem.getTimeStamps().getCreatedAt() : null)
				.updatedAt(cartItem.getTimeStamps() != null ? cartItem.getTimeStamps().getUpdatedAt() : null)
				.build();
	}

	public AfterwardResponse fromDomainAfterwards(AfterwardsItem afterwards) {
		if (afterwards == null) {
			return null;
		}

		return AfterwardResponse.builder()
				.productId(afterwards.getProductId() != null ? afterwards.getProductId().value() : null)
				.productName(afterwards.getProductName())
				.quantity(afterwards.getQuantity() != null ? afterwards.getQuantity().value() : 0)
				.unitPrice(afterwards.getUnitPrice() != null ? afterwards.getUnitPrice().value() : null)
				.discountPerUnit(afterwards.getDiscountPerUnit())
				.subtotal(afterwards.calculateSubtotal() != null ? afterwards.calculateSubtotal().value() : null)
				.movedAt(afterwards.getTimeStamps() != null ? afterwards.getTimeStamps().getCreatedAt() : null)
				.updatedAt(afterwards.getTimeStamps() != null ? afterwards.getTimeStamps().getUpdatedAt() : null)
				.build();
	}

	public List<AfterwardResponse> fromDomainsAfterwards(List<AfterwardsItem> afterwardsItems) {
		if (afterwardsItems == null) {
			return List.of();
		}

		return afterwardsItems.stream()
				.map(this::fromDomainAfterwards)
				.toList();
	}

	public List<CartItemResponse> fromDomains(List<CartItem> cartItems) {
		if (cartItems == null) {
			return List.of();
		}

		return cartItems.stream()
				.map(this::fromDomain)
				.toList();
	}
}
