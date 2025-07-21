package microservice.ecommerce_cart_service.app.domain.port.in.usecase;

import microservice.ecommerce_cart_service.app.application.dto.CartSummary;
import microservice.ecommerce_cart_service.app.application.queries.GetCartByCustomerIdQuery;
import microservice.ecommerce_cart_service.app.application.queries.GetCartByIdQuery;

public interface CartQueryUseCase {
    CartSummary getCartById(GetCartByIdQuery query);
    CartSummary getCartByCustomerId(GetCartByCustomerIdQuery query);
    //CartSummary getCartSummary(GetCartSummaryQuery query);
}
