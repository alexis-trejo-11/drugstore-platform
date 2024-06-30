package at.backend.drugstore.microservice.common_models.DTO.Cart;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class CartItemDTO {
    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("product_price")
    private BigDecimal productPrice;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("item_total")
    private BigDecimal itemTotal;
}