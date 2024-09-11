package at.backend.drugstore.microservice.common_classes.DTOs.Cart;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
@NoArgsConstructor
public class CartItemInsertDTO {

    @NotNull(message = "product_id is obligatory")
    @Positive(message = "product_id must be positive")
    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("productQuantity")
    @NotNull(message = "productQuantity is obligatory")
    @Positive(message = "productQuantity must be positive")
    private int quantity;
}