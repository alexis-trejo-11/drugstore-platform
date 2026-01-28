package microservice.cart_service.app.cart.adapter.input.web.dto.input;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CartItemRequest(
    @NotBlank @Length(min = 1, max = 100) String productId,
    @Positive @Max(100) int quantity) {
}
