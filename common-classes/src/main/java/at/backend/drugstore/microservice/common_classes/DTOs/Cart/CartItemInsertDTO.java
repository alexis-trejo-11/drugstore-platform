package at.backend.drugstore.microservice.common_classes.DTOs.Cart;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
public class CartItemInsertDTO {

    @NotNull(message = "product_id is obligatory")
    @Positive(message = "product_id must be positive")
    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("quantity")
    @NotNull(message = "quantity is obligatory")
    @Positive(message = "quantity must be positive")
    private int quantity;
}