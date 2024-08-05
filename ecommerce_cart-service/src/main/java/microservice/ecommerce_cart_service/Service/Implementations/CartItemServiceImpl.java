package microservice.ecommerce_cart_service.Service.Implementations;

import at.backend.drugstore.microservice.common_models.DTO.Cart.*;
import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import at.backend.drugstore.microservice.common_models.ExternalService.Products.ExternalProductService;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.ecommerce_cart_service.Mappers.CartItemMapper;
import microservice.ecommerce_cart_service.Mappers.CartMapper;
import microservice.ecommerce_cart_service.Model.Cart;
import microservice.ecommerce_cart_service.Model.CartItem;
import microservice.ecommerce_cart_service.Repository.CartItemRepository;
import microservice.ecommerce_cart_service.Repository.CartRepository;
import microservice.ecommerce_cart_service.Service.Extensions.CartCalculator;
import microservice.ecommerce_cart_service.Service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class CartItemServiceImpl implements CartItemService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ExternalProductService productService;
    private final CartItemManager cartItemManager;
    private final CartCalculator cartCalculator;
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;

    @Autowired
    public CartItemServiceImpl(CartRepository cartRepository,
                               CartItemRepository cartItemRepository,
                               ExternalProductService productService,
                               CartItemManager cartItemManager,
                               CartCalculator cartCalculator,
                               CartMapper cartMapper,
                               CartItemMapper cartItemMapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productService = productService;
        this.cartItemManager = cartItemManager;
        this.cartCalculator = cartCalculator;
        this.cartMapper = cartMapper;
        this.cartItemMapper = cartItemMapper;
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Result<Void>> addProductsCart(Long clientId, Long productId, int quantity) {
        // Fetch cart by client ID asynchronously
        CompletableFuture<Optional<Cart>> cartFuture = CompletableFuture.supplyAsync(() -> cartRepository.findByClientId(clientId));

        // Fetch product details asynchronously
        CompletableFuture<Result<ProductDTO>> productFuture = productService.getProductById(productId);

        // Combine the results and process them
        return cartFuture.thenCombine(productFuture, (cartOptional, productResult) -> {
            if (cartOptional.isEmpty()) {
                return Result.error("Cart with Client ID: " + clientId + " not found");
            }
            if (!productResult.isSuccess()) {
                return Result.error(productResult.getErrorMessage());
            }

            Cart cart = cartOptional.get();
            ProductDTO productDTO = productResult.getData();

            // Process cart update asynchronously
            return CompletableFuture.runAsync(() -> {
                        Cart cartUpdated = cartItemManager.addOrUpdateCartItem(cart, productDTO, quantity);
                        if (cartUpdated.getCartItems().isEmpty()) {
                            throw new RuntimeException("No items in cart");
                        }
                        cartCalculator.calculateCartNumbers(cartUpdated);
                        cartRepository.save(cartUpdated);
                    }).thenApply(v -> Result.success())
                    .exceptionally(ex -> Result.error("An error occurred: " + ex.getMessage()))
                    .join(); // Ensure this is executed synchronously to return Result<Void>
        });
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Result<?>> deleteProductFromCart(Long clientId, CartItemInsertDTO cartItemInsertDTO) {
        // Fetch the cart asynchronously
        CompletableFuture<Optional<Cart>> cartFuture = CompletableFuture.supplyAsync(() -> cartRepository.findByClientId(clientId));

        // Process cart update asynchronously
        return cartFuture.thenApply(cartOptional -> {
            if (cartOptional.isEmpty()) {
                return Result.error("Cart not found for client id: " + clientId);
            }

            Cart cart = cartOptional.get();
            cartItemManager.removeCartItem(cart, cartItemInsertDTO.getProductId(), cartItemInsertDTO.getQuantity());

            cartCalculator.calculateCartNumbers(cart);
            cartRepository.save(cart);

            return Result.success();
        }).exceptionally(ex -> {
            // Handle exceptions and return an error result
            return Result.error("An error occurred: " + ex.getMessage());
        });
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<CartDTO> processCartAndGePurchaseData(ClientEcommerceDataDTO clientEcommerceDataDTO, PurchaseFromCartDTO purchaseFromCartDTO) {
        return CompletableFuture.supplyAsync(() ->  {
            Cart cart = getCartByClientId(clientEcommerceDataDTO.getCartDTO().getClientId());
            return purchaseAllItems(cart);
        });

    }

    private Cart getCartByClientId(Long clientId) {
        Optional<Cart> cartOptional = cartRepository.findByClientId(clientId);
        return cartOptional.get();
    }

    private CartDTO purchaseAllItems(Cart cart) {
        // Calculate cart numbers before creating DTO
        Cart CartProccessed = cartCalculator.calculateCartNumbers(cart);

        // Create CartDTO with updated cart information
        var purchaseData = cartMapper.entityToDTO(CartProccessed);

        // Map cart items to DTOs
        List<CartItemDTO> productsToPurchase = CartProccessed.getCartItems().stream()
                .map(cartItemMapper::entityToDTO)
                .collect(Collectors.toList());

        purchaseData.setCartItems(productsToPurchase);

        // Clear cart items after processing
        List<CartItem> itemsToRemove = new ArrayList<>(cart.getCartItems());
        cart.getCartItems().clear();
        cartRepository.save(cart);

        // Delete cart items from the database
        cartItemRepository.deleteAll(itemsToRemove);

        // Update numbers
        cartCalculator.calculateCartNumbers(cart);

        return purchaseData;
    }

}