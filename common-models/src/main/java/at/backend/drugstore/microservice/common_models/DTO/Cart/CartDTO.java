package at.backend.drugstore.microservice.common_models.DTO.Cart;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class CartDTO {
    private Long id;
    private Long userId;
    private List<CartItemDTO> cartItems;
    private BigDecimal totalPrice;

    @JsonIgnore
    public List<Long> getProductsIds() {
        return cartItems.stream()
                .map(CartItemDTO::getProductId)
                .collect(Collectors.toList());

    }
}