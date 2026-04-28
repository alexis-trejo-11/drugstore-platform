package io.github.alexisTrejo11.drugstore.carts.cart.adapter.input.web.dto.input;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;


public record CartItemRequest(
    @NotBlank @Size(min = 1, max = 100) String productId,
    @Positive @Max(100) int quantity) {
}
