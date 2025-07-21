package microservice.ecommerce_cart_service.app.application.dto;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.CartId;
import org.springframework.data.domain.Page;

@AllArgsConstructor
@NoArgsConstructor
public class AfterwardsSummary {
    CartId cartId;
    Page<CartItemDetails> afterwardsItems;
}
