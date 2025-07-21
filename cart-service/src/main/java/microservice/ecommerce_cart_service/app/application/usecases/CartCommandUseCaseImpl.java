package microservice.ecommerce_cart_service.app.application.usecases;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import microservice.ecommerce_cart_service.app.application.command.cart.ClearCartCommand;
import microservice.ecommerce_cart_service.app.application.command.cart.CreateCartCommand;
import microservice.ecommerce_cart_service.app.application.command.cart_items.AddItemsToCartCommand;
import microservice.ecommerce_cart_service.app.application.command.cart_items.RemoveItemFromCartCommand;
import microservice.ecommerce_cart_service.app.application.command.cart_items.UpdateCartItemQuantityCommand;
import microservice.ecommerce_cart_service.app.domain.entities.Cart;
import microservice.ecommerce_cart_service.app.domain.entities.CartItem;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.CartId;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.CustomerId;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.Product;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.ProductId;
import microservice.ecommerce_cart_service.app.domain.excpetions.ProductNotFoundException;
import microservice.ecommerce_cart_service.app.domain.port.in.usecase.CartCommandUseCase;
import microservice.ecommerce_cart_service.app.domain.port.out.repositories.CartItemRepository;
import microservice.ecommerce_cart_service.app.domain.port.out.repositories.CartRepository;
import microservice.ecommerce_cart_service.app.domain.port.out.repositories.ProductFacadeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartCommandUseCaseImpl implements CartCommandUseCase {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductFacadeService productService;

    @Override
    public Cart createCart(CreateCartCommand command) {
        Cart cart = new Cart(command.getCustomerId());
        return cartRepository.save(cart);
    }

    @Override
    public Cart addItemsToCart(AddItemsToCartCommand command) {
        Cart cart = getCustomerCart(command.getCustomerId());

        List<CartItem> newItems = createCartItems(command, cart.getId());
        cart.addItems(newItems);

        return cartRepository.save(cart);
    }

    @Override
    public Cart removeItemFromCart(RemoveItemFromCartCommand command) {
        Cart cart = getCustomerCart(command.getCustomerId());
        List<CartItem> items = cartItemRepository.listByCartIdAndProductIdIn(cart.getId(), command.getProductIdSet());

        cart.removeItems(items);
        cartItemRepository.bulkDelete(items);
        cartRepository.save(cart);

        return cart;
    }

    @Override
    public Cart updateCartItemQuantity(UpdateCartItemQuantityCommand command) {
        Cart cart = getCustomerCart(command.getCustomerId());

        cart.updateItemQuantity(command.getProductId(), command.getNewQuantity());
        cartRepository.save(cart);

        return cart;
    }

    @Override
    public void clearCart(ClearCartCommand command) {
        Cart cart = cartRepository.findById(command.getCartId())
                .orElseThrow(() -> new EntityNotFoundException("Cart Not Found"));
        cart.clearCart();
        cartRepository.save(cart);
    }

    private List<CartItem> createCartItems(AddItemsToCartCommand command, CartId cartId) {
        Set<ProductId> productIds = command.getItems().stream()
                .map(items -> new ProductId(items.getProductId()))
                .collect(Collectors.toSet());
        validatesAvailableProducts(productIds);

         List<CartItem> items = command.getItems().stream().map(cartItemRequest -> CartItem.builder()
                 .cartId(cartId)
                 .quantity(cartItemRequest.getQuantity())
                 .productId(new ProductId(cartItemRequest.getProductId()))
                 .build()).toList();
        cartItemRepository.bulkSave(items);

        return items;
    }

    public void validatesAvailableProducts(Set<ProductId> productIds) {
        List<Product> products = productService.findAvailableByIdIn(productIds);

        Set<String> foundProductIds = products.stream()
                .map(product -> product.productId().toString())
                .collect(Collectors.toSet());

        List<String> missingProductIds = productIds.stream()
                .map(ProductId::toString)
                .filter(id -> !foundProductIds.contains(id))
                .toList();

        if (!missingProductIds.isEmpty()) {
            throw new ProductNotFoundException(String.valueOf(missingProductIds));
        }
    }
    private Cart getCustomerCart(CustomerId customerId) {
        return cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Cart Not Found")
        );
    }
}
