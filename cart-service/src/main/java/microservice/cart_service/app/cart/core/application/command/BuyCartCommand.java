package microservice.cart_service.app.cart.core.application.command;

import microservice.cart_service.app.cart.core.domain.model.valueobjects.CustomerId;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.ProductId;

import java.util.Set;

public record BuyCartCommand(
    CustomerId customerId,
    Set<ProductId> productsToExclude,
    String paymentReference) {
}
