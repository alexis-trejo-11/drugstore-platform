package at.backend.drugstore.microservice.common_models.DTO.Cart;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class CartDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("client_id")
    private Long clientId;

    @JsonProperty("subtotal")
    private BigDecimal subtotal;

    @JsonProperty("number_of_product")
    private int numberOfProduct() {
        return cartItems.size();
    }
    private List<CartItemDTO> cartItems;

    @JsonIgnore
    public List<Long> getProductsIds() {
        return cartItems.stream()
                .map(CartItemDTO::getProductId)
                .collect(Collectors.toList());

    }
}