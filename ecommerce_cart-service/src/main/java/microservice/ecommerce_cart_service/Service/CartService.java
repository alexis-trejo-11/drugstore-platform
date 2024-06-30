package microservice.ecommerce_cart_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.DTO.Cart.CartItemDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.ecommerce_cart_service.Model.Cart;
import microservice.ecommerce_cart_service.Repository.CartRepository;
import microservice.ecommerce_cart_service.Utils.ModelTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class CartService {

    private final CartRepository cartRepository;


    @Autowired
    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public CompletableFuture<Result<Void>> createCart(Long clientId) {
        try {
            Optional<Cart> optionalCart = cartRepository.findByClientId(clientId);
            if (optionalCart.isPresent()) {
                return CompletableFuture.completedFuture(Result.error("Client Already Has a Cart"));
            }

            Cart cart = new Cart(clientId);
            cartRepository.saveAndFlush(cart);

            return CompletableFuture.completedFuture(Result.success());
        } catch (Exception e) {
            return CompletableFuture.failedFuture(new Throwable("An Error Occurred Creating The Cart", e));
        }
    }


    public CompletableFuture<Result<CartDTO>> getCartByClientId(Long clientId) {
        try {
            // Find Cart
            Optional<Cart> optionalCart = cartRepository.findByClientId(clientId);
            if (optionalCart.isEmpty()) {
                return CompletableFuture.completedFuture(Result.error("Cart With Client Id:" + clientId + " Not Found"));
            }
            Cart cart = optionalCart.get();

            // Create Cart DTO
            CartDTO cartDTO = ModelTransformer.cartToDTO(cart);

            // Create Cart Items DTO
            List<CartItemDTO> cartItemDTOS = ModelTransformer.cartItemsToDTOs(cart.getCartItems());
            cartDTO.setCartItems(cartItemDTOS);

            return CompletableFuture.completedFuture(Result.success(cartDTO));
        } catch (Exception e) {
           return CompletableFuture.failedFuture(new Throwable("An Error Occurred Searching The Cart"));
        }
    }



}
