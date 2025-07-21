package microservice.ecommerce_cart_service.app.application.command.cart_items;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.CustomerId;
import microservice.ecommerce_cart_service.app.infrastructure.port.in.web.dto.CartItemRequest;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddItemsToCartCommand {
    private CustomerId customerId;
    private Set<CartItemRequest> items;

}

