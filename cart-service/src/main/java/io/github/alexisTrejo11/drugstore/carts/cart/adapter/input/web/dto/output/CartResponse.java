package io.github.alexisTrejo11.drugstore.carts.cart.adapter.input.web.dto.output;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Builder
public record CartResponse(
    String cartId,
    String customerId,
    BigDecimal subtotal,
		BigDecimal discount,
		BigDecimal total,
    LocalDateTime updatedAt,
    List<CartItemResponse> itemDetails,
    List<AfterwardResponse> afterwards) {
}
