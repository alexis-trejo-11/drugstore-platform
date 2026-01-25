package microservice.cart_service.app.cart.core.application.queries;

import microservice.cart_service.app.cart.core.domain.model.valueobjects.CustomerId;

public record GetCartByCustomerIdQuery(CustomerId customerId) {
}
