package microservice.ecommerce_cart_service.app.application.usecases;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import microservice.ecommerce_cart_service.app.application.dto.CartSummary;
import microservice.ecommerce_cart_service.app.application.queries.GetCartByCustomerIdQuery;
import microservice.ecommerce_cart_service.app.application.queries.GetCartByIdQuery;
import microservice.ecommerce_cart_service.app.domain.entities.Cart;
import microservice.ecommerce_cart_service.app.domain.entities.CartItem;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.Product;
import microservice.ecommerce_cart_service.app.domain.port.in.usecase.CartQueryUseCase;
import microservice.ecommerce_cart_service.app.domain.port.out.repositories.CartItemRepository;
import microservice.ecommerce_cart_service.app.domain.port.out.repositories.CartRepository;
import microservice.ecommerce_cart_service.app.domain.port.out.repositories.ProductFacadeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartQueryUseCaseImpl implements CartQueryUseCase {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductFacadeService productFacadeService;

    @Override
    public CartSummary getCartById(GetCartByIdQuery query) {
        Cart cart = getCartWithItems(query);
        if (cart.getCartItems().isEmpty()) {
            return getCartWithDetails(cart);
        }

        List<Product> products = productFacadeService.findByIdIn(cart.getProductsIdsIn());
        return getCartWithDetails(cart, products);
    }

    @Override
    public CartSummary getCartByCustomerId(GetCartByCustomerIdQuery query) {
        Cart cart = getCartWithItems(query);
        if (cart.getCartItems().isEmpty()) {
            return getCartWithDetails(cart);
        }

        List<Product> products = productFacadeService.findByIdIn(cart.getProductsIdsIn());
        return getCartWithDetails(cart, products);
    }


    private Cart getCartWithItems(GetCartByIdQuery query) {
        Cart cart = cartRepository.findById(query.getCartId())
                .orElseThrow(() -> new EntityNotFoundException("cart not found"));

        List<CartItem> cartItems = cartItemRepository.listByCartId(query.getCartId(), query.getItemsQuery());
        cart.setItems(cartItems);

        return cart;
    }

    private Cart getCartWithItems(GetCartByCustomerIdQuery query) {
      return null;
    }

    public CartSummary getCartWithDetails(Cart cart, List<Product> products) {
        CartSummary cartSummary = CartSummary.emptyCart(cart);
        if ((cart.getCartItems().isEmpty())) {
            return cartSummary;
        }
        cartSummary.appendProductData(products);
        cartSummary.calculatePrices();

        return cartSummary;
    }

    public CartSummary getCartWithDetails(Cart cart) {
        return CartSummary.emptyCart(cart);
    }
}
