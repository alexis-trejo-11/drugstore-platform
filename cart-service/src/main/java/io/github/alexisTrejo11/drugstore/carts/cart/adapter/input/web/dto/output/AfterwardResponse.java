package io.github.alexisTrejo11.drugstore.carts.cart.adapter.input.web.dto.output;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record AfterwardResponse(
		String productId,
		String productName,
		int quantity,
		BigDecimal unitPrice,
		BigDecimal discountPerUnit,
		BigDecimal subtotal,
		LocalDateTime movedAt,
		LocalDateTime updatedAt) {
}
