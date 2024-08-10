package microservice.test_service.Service;

import at.backend.drugstore.microservice.common_models.DTOs.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Cart.CartItemInsertDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Cart.ClientEcommerceDataDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Cart.PurchaseFromCartDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Product.ProductDTO;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Products.ProductFacadeService;
import at.backend.drugstore.microservice.common_models.Utils.Result;

import microservice.test_service.Service.DomainService.CartDomainService;
import microservice.test_service.Model.Cart;
import microservice.test_service.Repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class ClientClientCartServiceImpl implements ClientCartService {

    private final CartRepository cartRepository;
    private final ProductFacadeService productService;
    private final CartDomainService cartDomainService;


    @Autowired
    public ClientClientCartServiceImpl(CartRepository cartRepository,
                                       ProductFacadeService productService,
                                       CartDomainService cartDomainService) {
        this.cartRepository = cartRepository;
        this.productService = productService;
        this.cartDomainService = cartDomainService;
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