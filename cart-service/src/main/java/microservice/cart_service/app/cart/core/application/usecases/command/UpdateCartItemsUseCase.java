package microservice.cart_service.app.cart.core.application.usecases.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.transaction.Transactional;
import libs_kernel.response.Result;
import microservice.cart_service.app.cart.core.domain.exception.CartNotFoundException;
import microservice.cart_service.app.cart.core.domain.exception.CartValidationException;
import microservice.cart_service.app.cart.core.domain.model.CreateCartItemParams;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.ProductId;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.Quantity;
import microservice.cart_service.app.product.core.application.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import microservice.cart_service.app.cart.core.application.command.UpdateCartCommand;
import microservice.cart_service.app.cart.core.domain.model.Cart;
import microservice.cart_service.app.cart.core.domain.model.CartItem;
import microservice.cart_service.app.cart.core.port.out.CartRepository;

@Component
public class UpdateCartItemsUseCase {
	private final CartRepository cartRepository;
	private final ProductService productService;

	@Autowired
	public UpdateCartItemsUseCase(CartRepository cartRepository, ProductService productService) {
		this.cartRepository = cartRepository;
		this.productService = productService;
	}

	@Transactional
	public Cart execute(UpdateCartCommand command) {
		validateProductsAvailability(command);

		Cart cart = cartRepository.findByCustomerIdWithItems(command.customerId())
				.orElseThrow(() -> new CartNotFoundException(command.customerId()));

		applyCartUpdates(command, cart);

		return cartRepository.save(cart);
	}

	private void validateProductsAvailability(UpdateCartCommand command) {
		if (command.productQuantitiesMap() == null || command.productQuantitiesMap().isEmpty()) {
			return;
		}

		Result<Void> availableResult = productService.validateAllExistAndAvailableByIdIn(
				command.productQuantitiesMap()
		);

		if (!availableResult.isSuccess()) {
			throw new CartValidationException(availableResult.getMessage());
		}
	}

	public void applyCartUpdates(UpdateCartCommand command, Cart cart) {
		List<ProductId> productsToRemove = cart.getItems().stream()
				.map(CartItem::getProductId)
				.filter(productId -> !command.productQuantitiesMap().containsKey(productId))
				.toList();

		if (!productsToRemove.isEmpty()) {
			cart.removeItems(productsToRemove);
		}

		List<CartItem> updatedItems = new ArrayList<>();

		for (Map.Entry<ProductId, Integer> entry : command.productQuantitiesMap().entrySet()) {
			ProductId productId = entry.getKey();
			Integer quantityValue = entry.getValue();
			Quantity quantity = Quantity.of(quantityValue);

			Optional<CartItem> existingItem = cart.findItemByProductId(productId);

			if (existingItem.isPresent()) {
				CartItem item = existingItem.get();
				item.updateQuantity(quantity);
				updatedItems.add(item);
			} else {
				CreateCartItemParams params = new CreateCartItemParams(
						cart.getId(),
						productId,
						quantity
				);
				CartItem newItem = CartItem.create(params);
				updatedItems.add(newItem);
			}
		}

		cart.setItems(updatedItems);
	}
}