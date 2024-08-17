package microservice.ecommerce_cart_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Cart.CartDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Cart.CartItemInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Cart.ClientEcommerceDataDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Cart.PurchaseFromCartDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.ProductDTO;
import at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Products.ProductFacadeService;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import microservice.ecommerce_cart_service.Model.Cart;
import microservice.ecommerce_cart_service.Repository.CartRepository;
import microservice.ecommerce_cart_service.Service.DomainService.CartDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
                                 @Qualifier("productFacadeService") ProductFacadeService productFacadeService,
                                 CartDomainService cartDomainService) {
        this.cartRepository = cartRepository;
        this.productFacadeService = productFacadeService;
        this.cartDomainService = cartDomainService;
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Result<Void>> addProductsCart(Long clientId, Long productId, int quantity) {
        // Fetch cart by client ID asynchronously
        CompletableFuture<Optional<Cart>> cartFuture = CompletableFuture.supplyAsync(() -> cartRepository.findByClientId(clientId));

        // Fetch product details asynchronously
        CompletableFuture<Result<ProductDTO>> productFuture = productFacadeService.getProductById(productId);

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
                        Cart cartUpdated = cartDomainService.addOrUpdateCartItem(cart, productDTO, quantity);
                        if (cartUpdated.getCartItems().isEmpty()) {
                            throw new RuntimeException("No items in cart");
                        }
                        cartDomainService.calculateCartNumbers(cartUpdated);
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
            cartDomainService.removeCartItem(cart, cartItemInsertDTO.getProductId(), cartItemInsertDTO.getQuantity());

            cartDomainService.calculateCartNumbers(cart);
            cartRepository.save(cart);

            return Result.success();
        });
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<CartDTO> processCartAndGePurchaseData(ClientEcommerceDataDTO clientEcommerceDataDTO, PurchaseFromCartDTO purchaseFromCartDTO) {
        return CompletableFuture.supplyAsync(() ->  {
            Cart cart = getCartByClientId(clientEcommerceDataDTO.getCartDTO().getClientId());
            return cartDomainService.purchaseAllItems(cart);
        });

    }

    private Cart getCartByClientId(Long clientId) {
        Optional<Cart> cartOptional = cartRepository.findByClientId(clientId);
        return cartOptional.get();
    }



}