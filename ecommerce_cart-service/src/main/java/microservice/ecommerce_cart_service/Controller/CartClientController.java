package microservice.ecommerce_cart_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Cart.*;
import at.backend.drugstore.microservice.common_classes.Middleware.AuthSecurity;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import microservice.ecommerce_cart_service.Service.AfterwardsService;
import microservice.ecommerce_cart_service.Service.AfterwardsServiceImpl;
import microservice.ecommerce_cart_service.Service.ClientCartService;
import microservice.ecommerce_cart_service.Service.CartService;
import microservice.ecommerce_cart_service.Service.FacadeService.PurchaseServiceFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("v1/api/ecommerce/client-carts")
@Tag(name = "Drugstore Microservice API (Cart Service)", description = "Service for managing carts")

public class CartClientController {

    private final CartService cartService;
    private final ClientCartService clientCartService;
    private final PurchaseServiceFacade purchaseServiceFacade;
    private final AfterwardsService afterwardsService;
    private final AuthSecurity authSecurity;

    public CartClientController(CartService cartService,
                                ClientCartService clientCartService,
                                PurchaseServiceFacade purchaseServiceFacade,
                                AfterwardsServiceImpl afterwardsService,
                                AuthSecurity authSecurity) {
        this.cartService = cartService;
        this.clientCartService = clientCartService;
        this.purchaseServiceFacade = purchaseServiceFacade;
        this.afterwardsService = afterwardsService;
        this.authSecurity = authSecurity;
    }

    @Operation(summary = "Fetch cart by client ID", description = "Retrieve the cart details for a specific client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart successfully fetched"),
            @ApiResponse(responseCode = "409", description = "No cart found for the client ID")
    })
    @GetMapping(value = "/client")
    public CompletableFuture<ResponseEntity<ResponseWrapper<CartDTO>>> getCartByClientId(HttpServletRequest request) {
        Long clientId = authSecurity.getClientIdFromToken(request);
        log.info("Fetching cart for client ID: {}", clientId);

        return cartService.getCartByClientId(clientId).thenApply(optionalCartDTO -> {
            if (optionalCartDTO.isEmpty()) {
                log.warn("No cart found for client ID: {}", clientId);
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ResponseWrapper<>(false, null, "No cart found for client ID: " + clientId, 409));
            }

            log.info("Successfully fetched cart for client ID: {}", clientId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseWrapper<>(true, optionalCartDTO.get(), "Cart successfully fetched.", 200));
        });
    }

    @Operation(summary = "Add product to cart", description = "Add a specific product to the cart for a given client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product successfully added to cart"),
            @ApiResponse(responseCode = "404", description = "Product or cart not found")
    })
    @PostMapping(value = "/product/{clientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> addProductToCart( @Valid @RequestBody final CartItemInsertDTO cartItemInsertDTO,
                                                                                      @PathVariable final Long clientId) {
        var productId = cartItemInsertDTO.getProductId();
        var quantity = cartItemInsertDTO.getQuantity();
        log.info("Adding product ID: {} with quantity: {} to cart for client ID: {}", productId, quantity, clientId);

        return clientCartService.addProductsCart(clientId, productId, quantity)
                .thenApply(cartResult -> {
                    if (!cartResult.isSuccess()) {
                        log.warn("Failed to add product ID: {} to cart for client ID: {}. Error: {}", productId, clientId, cartResult.getErrorMessage());
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ResponseWrapper<>(false, null, cartResult.getErrorMessage(), 404));
                    }

                    log.info("Successfully added product ID: {} to cart for client ID: {}", productId, clientId);
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new ResponseWrapper<>(true, null, "Products successfully added.", 200));
                });
    }

    @Operation(summary = "Delete product from cart", description = "Remove a specific product from the cart for a given client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product successfully deleted from cart"),
            @ApiResponse(responseCode = "400", description = "Failed to delete product from cart")
    })
    @DeleteMapping("/product/{clientId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> deleteProductFromCart(
            @Valid @PathVariable final Long clientId,
            @RequestBody final CartItemInsertDTO cartItemInsertDTO) {
        log.info("Deleting product from cart for client ID: {}", clientId);
        return clientCartService.deleteProductFromCart(clientId, cartItemInsertDTO)
                .thenApply(deleteResult -> {
                    if (!deleteResult.isSuccess()) {
                        log.warn("Failed to delete product from cart for client ID: {}. Error: {}", clientId, deleteResult.getErrorMessage());
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new ResponseWrapper<>(false, null, deleteResult.getErrorMessage(), 400));
                    }

                    log.info("Successfully deleted product from cart for client ID: {}", clientId);
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new ResponseWrapper<>(true, null, "Product Successfully Deleted", 200));
                });
    }

    @Operation(summary = "Purchase products from cart", description = "Process the purchase of products from the cart for a specific client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order created and payment will be validated soon"),
            @ApiResponse(responseCode = "404", description = "Failed to prepare client data for purchase")
    })
    @PostMapping("/purchase")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> purchaseProductsFromCart(@Valid @RequestBody PurchaseFromCartDTO purchaseFromCartDTO) {
        Long clientId = purchaseFromCartDTO.getClientId();
        log.info("Processing purchase for client ID: {}", clientId);
        return purchaseServiceFacade.prepareClientData(clientId)
                .thenCompose(ecommerceDataDTOResult -> {
                    if (!ecommerceDataDTOResult.isSuccess()) {
                        log.warn("Failed to prepare client data for purchase for client ID: {}. Error: {}", clientId, ecommerceDataDTOResult.getErrorMessage());
                        return CompletableFuture.completedFuture(
                                ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(new ResponseWrapper<>(false, null, ecommerceDataDTOResult.getErrorMessage(), 404))
                        );
                    }

                    return clientCartService.processCartAndGePurchaseData(ecommerceDataDTOResult.getData(), purchaseFromCartDTO)
                            .thenCompose(purchaseData -> {
                                var cardId = purchaseFromCartDTO.getCardId();
                                var addressId = purchaseFromCartDTO.getAddressId();

                                // Asynchronously process the purchase
                                return purchaseServiceFacade.processPurchase(ecommerceDataDTOResult.getData(), cardId, purchaseData, addressId)
                                        .thenApply(v -> {
                                            log.info("Successfully processed purchase for client ID: {}", clientId);
                                            return ResponseEntity.status(HttpStatus.OK)
                                                    .body(new ResponseWrapper<>(true, null, "Order Created. Payment Will Be Validated Soon.", 200));
                                        });
                            });
                });
    }

    @Operation(summary = "Get afterward products by client ID", description = "Retrieve the list of products marked for afterwards for a specific client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Afterward items successfully fetched")
    })
    @GetMapping("/client-afterwards/{clientId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<List<CartItemDTO>>>> getAfterwardProductsByClientId(@Valid @PathVariable final Long clientId) {
        return afterwardsService.getAfterwardsByClientId(clientId)
                .thenApply(cartItemDTOS -> ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseWrapper<>(true, cartItemDTOS, "Afterward items successfully fetched!", 200)));
    }

    @Operation(summary = "Move product to afterwards", description = "Move a specific product from cart to afterwards list for a client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product successfully moved to afterwards"),
            @ApiResponse(responseCode = "400", description = "Failed to move product to afterwards")
    })
    @PostMapping("/move-to-afterwards/{clientId}/{productId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> moveProductToAfterwards(@Valid @PathVariable final Long clientId,
                                                                                            @PathVariable final Long productId) {
        return afterwardsService.moveProductToAfterwards(clientId, productId)
                .thenApply(afterwardsResult -> {
                    if (!afterwardsResult.isSuccess()) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new ResponseWrapper<>(false, null, afterwardsResult.getErrorMessage(), 400));
                    }

                    log.info("Successfully moved product ID: {} to afterwards for client ID: {}", productId, clientId);
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new ResponseWrapper<>(true, null, "Product successfully moved to Afterwards", 200));
                });
    }

    @Operation(summary = "Return product to cart", description = "Return a specific product from afterwards list to the cart for a client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product successfully returned to cart"),
            @ApiResponse(responseCode = "400", description = "Failed to return product to cart")
    })
    @DeleteMapping("/return-to-afterwards/{clientId}/{productId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> returnProductToCart(@Valid @PathVariable final Long clientId,
                                                                                        @PathVariable final Long productId) {
        return afterwardsService.returnProductToCart(clientId, productId).thenApply(returnResult -> {
            if (!returnResult.isSuccess()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseWrapper<>(false, null, returnResult.getErrorMessage(), 400));
            }

            log.info("Successfully moved product ID: {} to afterwards for client ID: {}", productId, clientId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseWrapper<>(true, null, "Product Successfully Returned To Cart.", 200));
        });
    }
}
