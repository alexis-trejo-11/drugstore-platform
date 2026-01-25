package microservice.cart_service.app.cart.adapter.in.web.mapper;

import org.springframework.stereotype.Component;

import microservice.cart_service.app.cart.adapter.in.web.dto.output.CartResponse;
import microservice.cart_service.app.cart.core.domain.model.Cart;

@Component
public class CartControllerMaper {

  public CartResponse entityToResponse(Cart cart) {
    if (cart == null) {
      return null;
    }

    return null; // Implement the mapping logic here
  }

}
