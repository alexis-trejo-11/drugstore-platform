package microservice.cart_service.app.cart.core.application.usecases.command;

import java.util.Optional;

import jakarta.transaction.Transactional;
import microservice.cart_service.app.cart.core.domain.model.CreateCartItemParams;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.Quantity;
import microservice.cart_service.app.product.core.port.out.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import microservice.cart_service.app.cart.core.application.command.UpdateCartCommand;
import microservice.cart_service.app.cart.core.domain.model.Cart;
import microservice.cart_service.app.cart.core.domain.model.CartItem;
import microservice.cart_service.app.cart.core.port.out.CartRepository;

@Component
public class UpdateCartItemsUseCase {
  private final CartRepository cartRepository;
  private final ProductRepository productRepository;

  @Autowired
  public UpdateCartItemsUseCase(CartRepository cartRepository, ProductRepository productRepository) {
    this.cartRepository = cartRepository;
    this.productRepository = productRepository;
  }

  @Transactional
  public Cart execute(UpdateCartCommand command) {
    Cart cart = cartRepository.findByCustomerIdWithItems(command.customerId())
        .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

    applyCartUpdates(command, cart);

    // TODO: Validate product availability calling product-service and inventory-service

    return cartRepository.save(cart);
  }

  public void applyCartUpdates(UpdateCartCommand command, Cart cart) {
    command.productQuantitiesMap().forEach((productId, quantity) -> {
      Optional<CartItem> existingItem = cart.findItemByProductId(productId);

      if (existingItem.isPresent()) {
        cart.updateItemQuantity(productId, quantity);
      } else {
        CreateCartItemParams params = new CreateCartItemParams(cart.getId(), productId, Quantity.of(quantity));
        CartItem newItem = CartItem.create(params);
        cart.addItem(newItem);
      }
    });
  }
}
