package microservice.ecommerce_cart_service.Controller;

import at.backend.drugstore.microservice.common_models.DTO.Cart.*;
import at.backend.drugstore.microservice.common_models.Utils.ApiResponse;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.ecommerce_cart_service.Model.CartItem;
import microservice.ecommerce_cart_service.Service.AfterwardsService;
import microservice.ecommerce_cart_service.Service.Implementations.AfterwardsServiceImpl;
import microservice.ecommerce_cart_service.Service.CartItemService;
import microservice.ecommerce_cart_service.Service.CartService;
import microservice.ecommerce_cart_service.Service.PurchaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@RestController
@RequestMapping("v1/api/ecommerce/client-carts")
public class CartClientController {

    private static final Logger logger = Logger.getLogger(CartController.class.getName());
    private final CartService cartService;
    private final CartItemService cartItemService;
    private final PurchaseService purchaseService;
    private final AfterwardsService afterwardsService;

    public CartClientController(CartService cartService,
                                CartItemService cartItemService,
                                PurchaseService purchaseService,
                                AfterwardsServiceImpl afterwardsService) {
        this.cartService = cartService;
        this.cartItemService = cartItemService;
        this.purchaseService = purchaseService;
        this.afterwardsService = afterwardsService;
    }


    @GetMapping("/client/{clientId}")
    public CompletableFuture<ResponseEntity<ApiResponse<CartDTO>>> getCartByClientId(@PathVariable Long clientId) {
        logger.info("Fetching cart for client ID: " + clientId);
        return cartService.getCartByClientId(clientId).thenApply(optionalCartDTO -> {
            if (optionalCartDTO.isEmpty()) {
                logger.warning("No cart found for client ID: " + clientId);
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse<>(false, null, "No cart found for client ID: " + clientId, 409));
            }

            logger.info("Successfully fetched cart for client ID: " + clientId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse<>(true, optionalCartDTO.get(), "Cart successfully fetched.", 200));
        });
    }

    @PostMapping("/product/{clientId}")
    public CompletableFuture<ResponseEntity<ApiResponse<Void>>> addProductToCart(@Valid @RequestBody final CartItemInsertDTO cartItemInsertDTO,
                                                                                 @PathVariable final Long clientId) {
        var productId = cartItemInsertDTO.getProductId();
        var quantity = cartItemInsertDTO.getQuantity();
        logger.info("Adding product ID: " + productId + " with quantity: " + quantity + " to cart for client ID: " + clientId);

        return cartItemService.addProductsCart(clientId, productId, quantity)
                .thenApply(cartResult -> {
                    if (!cartResult.isSuccess()) {
                        logger.warning("Failed to add product ID: " + productId + " to cart for client ID: " + clientId + ". Error: " + cartResult.getErrorMessage());
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ApiResponse<>(false, null, cartResult.getErrorMessage(), 404));
                    }

                    logger.info("Successfully added product ID: " + productId + " to cart for client ID: " + clientId);
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new ApiResponse<>(true, null, "Products successfully added.", 200));
                });
    }


    @DeleteMapping("/product/{clientId}")
    public CompletableFuture<ResponseEntity<ApiResponse<Void>>> deleteProductFromCart(@Valid @PathVariable final Long clientId,
                                                                                      @RequestBody final CartItemInsertDTO cartItemInsertDTO) {
        logger.info("Deleting product from cart for client ID: " + clientId);
        return cartItemService.deleteProductFromCart(clientId, cartItemInsertDTO)
                .thenApply(deleteResult -> {
                    if (!deleteResult.isSuccess()) {
                        logger.warning("Failed to delete product from cart for client ID: " + clientId + ". Error: " + deleteResult.getErrorMessage());
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new ApiResponse<>(false, null, deleteResult.getErrorMessage(), 400));
                    }

                    logger.info("Successfully deleted product from cart for client ID: " + clientId);
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new ApiResponse<>(true, null, "Product Successfully Deleted", 200));
                });
    }

    @PostMapping("/purchase")
    public CompletableFuture<ResponseEntity<ApiResponse<Void>>> purchaseProductsFromCart(@Valid @RequestBody PurchaseFromCartDTO purchaseFromCartDTO) {
        Long clientId = purchaseFromCartDTO.getClientId();
        logger.info("Processing purchase for client ID: " + clientId);
        return purchaseService.prepareClientData(clientId)
                .thenCompose(ecommerceDataDTOResult -> {
                    if (!ecommerceDataDTOResult.isSuccess()) {
                        logger.warning("Failed to prepare client data for purchase for client ID: " + clientId + ". Error: " + ecommerceDataDTOResult.getErrorMessage());
                        return CompletableFuture.completedFuture(
                                ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(new ApiResponse<>(false, null, ecommerceDataDTOResult.getErrorMessage(), 404))
                        );
                    }

                    return cartItemService.processCartAndGePurchaseData(ecommerceDataDTOResult.getData(), purchaseFromCartDTO)
                            .thenCompose(purchaseData -> {
                                var cardId = purchaseFromCartDTO.getCardId();
                                var addressId = purchaseFromCartDTO.getAddressId();

                                // Asynchronously process the purchase
                                return purchaseService.processPurchase(ecommerceDataDTOResult.getData(), cardId, purchaseData, addressId)
                                        .thenApply(v -> {
                                            logger.info("Successfully processed purchase for client ID: " + clientId);
                                            return ResponseEntity.status(HttpStatus.OK)
                                                    .body(new ApiResponse<>(true, null, "Order Created. Payment Will Be Validated Soon.", 200));
                                        });
                            });
                });
    }

    @GetMapping("/client-afterwards/{clientId}")
    public CompletableFuture<ResponseEntity<ApiResponse<List<CartItemDTO>>>> getAfterwardProductsByClientId(@Valid @PathVariable final Long clientId) {
        return afterwardsService.getAfterwardsByClientId(clientId)
                .thenApply(cartItemDTOS -> ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse<>(true, cartItemDTOS, "Afterward items successfully fetched!", 200)));
    }

    @PostMapping("/move-to-afterwards/{clientId}/{productId}")
    public CompletableFuture<ResponseEntity<ApiResponse<Void>>> moveProductToAfterwards(@Valid @PathVariable final Long clientId,
                                                                                        @PathVariable final Long productId) {
        return afterwardsService.moveProductToAfterwards(clientId, productId)
                .thenApply(afterwardsResult -> {
                    if (!afterwardsResult.isSuccess()) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new ApiResponse<>(false, null, afterwardsResult.getErrorMessage(), 400));
                    }

                    logger.info("Successfully moved product ID: " + productId + " to afterwards for client ID: " + clientId);
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new ApiResponse<>(true, null, "Product successfully moved to Afterwards", 200));
                });
    }

    @DeleteMapping("/return-to-afterwards/{clientId}/{productId}")
    public  CompletableFuture<ResponseEntity<ApiResponse<Void>>> returnProductToCart(@Valid @PathVariable final Long clientId,
                                                                                     @PathVariable final Long productId) {
        return afterwardsService.returnProductToCart(clientId, productId).thenApply(returnResult -> {
            if (!returnResult.isSuccess()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, null, returnResult.getErrorMessage(), 400));
            }

            logger.info("Successfully move product ID: " + productId + " to afterwards for client ID: " + clientId);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, null, "Product Successfully Returned To Cart.", 200));
        });
    }

}
