package microservice.ecommerce_cart_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.DTO.Cart.CartItemDTO;
import at.backend.drugstore.microservice.common_models.DTO.Cart.CartItemInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Cart.ClientEcommerceDataDTO;
import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import at.backend.drugstore.microservice.common_models.ExternalService.Products.ExternalProductService;
import at.backend.drugstore.microservice.common_models.ExternalService.Products.ExternalProductServiceImpl;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.ecommerce_cart_service.Model.Cart;
import microservice.ecommerce_cart_service.Model.CartItem;
import microservice.ecommerce_cart_service.Repository.CartItemRepository;
import microservice.ecommerce_cart_service.Repository.CartRepository;
import microservice.ecommerce_cart_service.Utils.ModelTransformer;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;


@Service
public class CartItemService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ExternalProductService productService;

    @Autowired
    public CartItemService(CartRepository cartRepository, CartItemRepository cartItemRepository, ExternalProductServiceImpl productService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productService = productService;
    }

    @Async
    @Transactional
    public CompletableFuture<Result<Void>> addProductsCart(Long clientId, Long productId, int quantity) {
        try {
            // Find Cart
            Optional<Cart> cartOptional = cartRepository.findByClientId(clientId);
            if (cartOptional.isEmpty()) {
                return CompletableFuture.completedFuture(Result.error("Cart With Client Id: " + clientId + " Not Found"));
            }

            // Find Product
            Result<ProductDTO> productResult = productService.getProductById(productId);
            if (!productResult.isSuccess()) {
                return CompletableFuture.completedFuture(Result.error(productResult.getErrorMessage()));
            }

            // Append Product To Cart
            Cart cart = cartOptional.get();
            ProductDTO productDTO = productResult.getData();

            // Get Cart Items
            List<CartItem> cartItems = cart.getCartItems();

            // Find Cart Item Or Create New One
            Optional<CartItem> existingCartItem = cartItems.stream()
                    .filter(cartItem -> cartItem.getProductId().equals(productId))
                    .findFirst();

            if (existingCartItem.isPresent()) {
                // Update quantity if item exists
                CartItem cartItem = existingCartItem.get();
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
                cartItem.setItemTotal(cartItem.getProductPrice().multiply(new BigDecimal(cartItem.getQuantity())));
            } else {
                // Create a new cart item if it doesn't exist
                CartItem newCartItem = ModelTransformer.productDtoToCartItem(productDTO, quantity, cart);
                cartItems.add(newCartItem);
            }

            // Calculate New Total
            cart.setTotalPrice(calculateCartTotal(cart));

            cartRepository.saveAndFlush(cart);

            return CompletableFuture.completedFuture(Result.success(null));

        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }
    @Async
    @Transactional
    public CompletableFuture<Result<Void>> deleteProductFromCart(Long clientId, CartItemInsertDTO cartItemInsertDTO) {
        try {
            // Find Cart
            Optional<Cart> cartOptional = cartRepository.findByClientId(clientId);
            if (cartOptional.isEmpty()) {
                return CompletableFuture.completedFuture(Result.error("Cart With Client Id: " + clientId + " Not Found"));
            }

            // Find Product
            Result<ProductDTO> productResult = productService.getProductById(cartItemInsertDTO.getProductId());
            if (!productResult.isSuccess()) {
                return CompletableFuture.completedFuture(Result.error("Product With Id: " + cartItemInsertDTO.getProductId() + " Not Found"));
            }

            // Remove Product From Cart
            Cart cart = cartOptional.get();
            List<CartItem> cartItems = cart.getCartItems();

            // Find the Cart Item with the given product ID
            Optional<CartItem> optionalCartItem = cartItems.stream()
                    .filter(cartItem -> cartItem.getProductId().equals(cartItemInsertDTO.getProductId()))
                    .findFirst();

            if (optionalCartItem.isEmpty()) {
                return CompletableFuture.completedFuture(Result.error("Product With Id: " + cartItemInsertDTO.getProductId() + " Not Found In Cart"));
            }

            CartItem cartItem = optionalCartItem.get();

            // Remove item or reduce quantity
            if (cartItem.getQuantity() <= cartItemInsertDTO.getQuantity()) {
                cartItems.remove(cartItem); // Remove from the cart's list of items
                cartItemRepository.deleteById(cartItem.getId()); // Remove from the database
            } else {
                // Reduce quantity
                cartItem.setQuantity(cartItem.getQuantity() - cartItemInsertDTO.getQuantity());
                cartItem.setItemTotal(cartItem.getProductPrice().multiply(new BigDecimal(cartItem.getQuantity())));
                cartItemRepository.saveAndFlush(cartItem);
            }

            // Calculate new total price
            cart.setTotalPrice(calculateCartTotal(cart));

            cartRepository.saveAndFlush(cart);

            return CompletableFuture.completedFuture(Result.success(null));

        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async
    @Transactional
    public Result<List<CartItemDTO>> purchaseProductsFromCart(ClientEcommerceDataDTO clientEcommerceDataDTO) {
        try {
            // Find Cart
            Optional<Cart> cartOptional = cartRepository.findByClientId(clientEcommerceDataDTO.clientDTO.getId());
            if (cartOptional.isEmpty()) {
                return Result.error("Cart Not Found");
            }

            Cart cart = cartOptional.get();

            // Initialize lazy-loaded collection
            List<CartItem> cartItems = cart.getCartItems();
            cartItems.size();
            List<CartItemDTO> cartItemDTOS = ModelTransformer.cartItemsToDTOs(cartItems);

            if (cartItems.isEmpty()) {
                return new Result<>(false, null, "No products to purchase in the cart", HttpStatus.BAD_REQUEST);
            }

            // Update total price
            cart.setTotalPrice(calculateCartTotal(cart));
            cartRepository.saveAndFlush(cart);

            // Delete all cart items
            cartItemRepository.deleteAll(cartItems);

            // Clear the cart items list to ensure no orphan references
            cart.getCartItems().clear();
            cartRepository.saveAndFlush(cart);

            // Convert cart items to DTOs
            return Result.success(cartItemDTOS);
        } catch (Exception e) {
            throw new RuntimeException("An Error Occurred Updating Cart",e);
        }
    }

    private BigDecimal calculateCartTotal(Cart cart) {
        return cart.getCartItems().stream()
                .map(CartItem::getItemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
