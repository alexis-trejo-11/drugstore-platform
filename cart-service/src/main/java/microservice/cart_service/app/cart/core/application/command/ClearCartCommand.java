package microservice.cart_service.app.cart.core.application.command;

import microservice.cart_service.app.cart.core.domain.model.valueobjects.CustomerId;

public record ClearCartCommand(CustomerId customerId) {
}
