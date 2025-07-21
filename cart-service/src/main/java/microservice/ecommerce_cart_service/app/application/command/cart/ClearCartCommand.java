package microservice.ecommerce_cart_service.app.application.command.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.CartId;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClearCartCommand {
    private CartId cartId;
}
