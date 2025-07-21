package microservice.ecommerce_cart_service.app.application.command.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.ProductId;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuyCartProductsCommand {
    private Set<ProductId> productsToExclude;
    private String paymentReference;
}
