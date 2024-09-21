package microservice.ecommerce_cart_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Cart.CartItemInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.ProductDTO;
import at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Products.ProductFacadeService;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import microservice.ecommerce_cart_service.Model.Cart;
import microservice.ecommerce_cart_service.Repository.CartRepository;
import microservice.ecommerce_cart_service.Service.DomainService.CartDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class ClientCartServiceImpl implements ClientCartService {

    private final CartRepository cartRepository;
    private final ProductFacadeService productFacadeService;
    private final CartDomainService cartDomainService;

    @Autowired
    public ClientCartServiceImpl(CartRepository cartRepository,
                                 CartDomainService cartDomainService,
                                 @Qualifier("productFacadeService") ProductFacadeService productFacadeService) {
        this.cartRepository = cartRepository;
        this.productFacadeService = productFacadeService;
        this.cartDomainService = cartDomainService;
    }

    @Override
    @Transactional
    @Cacheable(value = "carts", key = "#clientId", unless = "#result == null")
    public Result<Void> addProductsCart(Long clientId, Long productId, int quantity) {
        // Fetch cart by client ID asynchronously
        CompletableFuture<Optional<Cart>> cartFuture = CompletableFuture.supplyAsync(() -> cartRepository.findByClientId(clientId));
        Optional<Cart> optionalCart = cartFuture.join();
        if (optionalCart.isEmpty()) {
            return Result.error("Cart with Client ID: " + clientId + " not found");
        }
        Cart cart = optionalCart.get();

        // Fetch product details asynchronously
        CompletableFuture<Result<ProductDTO>> productFuture = productFacadeService.getProductById(productId);
        Result<ProductDTO> productDTOResult = productFuture.join();
        if (!productDTOResult.isSuccess()) {
            return Result.error(productDTOResult.getErrorMessage());
        }
        ProductDTO productDTO = productDTOResult.getData();

        // Process cart update asynchronously
        Cart cartUpdated = cartDomainService.addOrUpdateCartItem(cart, productDTO, quantity);
        if (cartUpdated.getCartItems().isEmpty()) {
            throw new RuntimeException("No items in cart");
        }

        cartDomainService.calculateCartNumbers(cartUpdated);
        cartRepository.saveAndFlush(cartUpdated);

        return  Result.success();
    }

    @Override
    @Transactional
    public Result<Void> deleteProductFromCart(Long clientId, CartItemInsertDTO cartItemInsertDTO) {
        // Fetch the cart asynchronously
        CompletableFuture<Optional<Cart>> cartFuture = CompletableFuture.supplyAsync(() -> cartRepository.findByClientId(clientId));
        Optional<Cart> optionalCart = cartFuture.join();
        if (optionalCart.isEmpty()) {
            return Result.error("Cart not found for client id: " + clientId);
        }

        // Process cart update asynchronously
        Cart cart = optionalCart.get();
        cartDomainService.removeCartItem(cart, cartItemInsertDTO.getProductId(), cartItemInsertDTO.getQuantity());

        cartDomainService.calculateCartNumbers(cart);
        cartRepository.save(cart);

        return Result.success();
    }

    @Override
    @Transactional
    @Async("taskExecutor")
    public CompletableFuture<Void> clearOutCart(Long cartId) {
        return CompletableFuture.runAsync(() -> {
            Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new RuntimeException("An Error Clearing Out Cart"));
            cartDomainService.removeItems(cart);
        });
    }
}