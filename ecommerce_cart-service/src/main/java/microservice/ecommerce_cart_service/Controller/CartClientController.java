package microservice.ecommerce_cart_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Cart.*;
import at.backend.drugstore.microservice.common_classes.Security.AuthSecurity;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import microservice.ecommerce_cart_service.Service.ClientCartService;
import microservice.ecommerce_cart_service.Service.CartService;
import microservice.ecommerce_cart_service.Service.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("v1/drugstore/ecommerce-carts/clients")
@Tag(name = "Drugstore Microservice API (Cart Service)", description = "Service for managing carts")

public class CartClientController {

    private final CartService cartService;
    private final ClientCartService clientCartService;
    private final PurchaseService purchaseService;
    private final AuthSecurity authSecurity;

    public CartClientController(CartService cartService,
                                ClientCartService clientCartService,
                                PurchaseService purchaseService,
                                AuthSecurity authSecurity) {
        this.cartService = cartService;
        this.clientCartService = clientCartService;
        this.purchaseService = purchaseService;
        this.authSecurity = authSecurity;
    }

    @Operation(summary = "Fetch cart by client ID", description = "Retrieve the cart details for a specific client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart successfully fetched"),
            @ApiResponse(responseCode = "409", description = "No cart found for the client ID")
    })
    @GetMapping
    public ResponseEntity<ResponseWrapper<CartDTO>> getCartByClientId(HttpServletRequest request) {
        Long clientId = authSecurity.getClientIdFromToken(request);
        log.info("Fetching cart for client ID: {}", clientId);

        CartDTO cartDTO = cartService.getCartByClientId(clientId);
        log.info("Successfully fetched cart for client ID: {}", clientId);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseWrapper.found(cartDTO, "Cart"));
    }

    @Operation(summary = "Add product to cart", description = "Add a specific product to the cart for a given client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product successfully added to cart"),
            @ApiResponse(responseCode = "404", description = "Product or cart not found")
    })
    @PostMapping(value = "/product")
    public ResponseEntity<ResponseWrapper<Void>> addProductToCart(@Valid @RequestBody CartItemInsertDTO cartItemInsertDTO, HttpServletRequest request) {
        Long clientId = authSecurity.getClientIdFromToken(request);
        log.info("addProductToCart -> Fetching cart for client ID: {}", clientId);

        var productId = cartItemInsertDTO.getProductId();
        var quantity = cartItemInsertDTO.getQuantity();
        log.info("Adding product ID: {} with productQuantity: {} to cart for client ID: {}", productId, quantity, clientId);

         Result<Void> cartResult = clientCartService.addProductsCart(clientId, productId, quantity);
        if (!cartResult.isSuccess()) {
            log.warn("Failed to add product ID: {} to cart for client ID: {}. Error: {}", productId, clientId, cartResult.getErrorMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseWrapper<>(false, null, cartResult.getErrorMessage(), 404));
        }

        log.info("Successfully added product ID: {} to cart for client ID: {}", productId, clientId);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseWrapper<>(true, null, "Products successfully added.", 200));
    }

    @Operation(summary = "Delete product from cart", description = "Remove a specific product from the cart for a given client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product successfully deleted from cart"),
            @ApiResponse(responseCode = "400", description = "Failed to delete product from cart")
    })
    @DeleteMapping("/product")
    public ResponseEntity<ResponseWrapper<Void>> deleteProductFromCart(@Valid HttpServletRequest request, @RequestBody final CartItemInsertDTO cartItemInsertDTO) {
        Long clientId = authSecurity.getClientIdFromToken(request);
        log.info("deleteProductFromCart -> Deleting product from cart for client ID: {}", clientId);

        Result<Void> deleteResult = clientCartService.deleteProductFromCart(clientId, cartItemInsertDTO);
        if (!deleteResult.isSuccess()) {
            log.warn("deleteProductFromCart -> Failed to delete product from cart for client ID: {}. Error: {}", clientId, deleteResult.getErrorMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseWrapper<>(false, null, deleteResult.getErrorMessage(), 400));
        }

        log.info("Successfully deleted product from cart for client ID: {}", clientId);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseWrapper<>(true, null, "Product Successfully Deleted", 200));
    }

    @Operation(summary = "Purchase products from cart", description = "Process the purchase of products from the cart for a specific client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order created and payment will be validated soon"),
            @ApiResponse(responseCode = "404", description = "Failed to prepare client data for purchase")
    })
    @PostMapping("/purchase")
    public ResponseEntity<ResponseWrapper<Void>> purchaseProductsFromCart(@Valid @RequestBody PurchaseFromCartDTO purchaseFromCartDTO, HttpServletRequest request) {
        Long clientId = authSecurity.getClientIdFromToken(request);
        log.info("purchaseProductsFromCart -> Fetching cart for client ID: {}", clientId);

        // Bring Client Data
        var ecommerceDataFuture = purchaseService.prepareClientData(clientId);
        Result<ClientEcommerceDataDTO> ecommerceDataDTOResult  = ecommerceDataFuture.join();

        if (!ecommerceDataDTOResult.isSuccess()) {
            log.warn("purchaseProductsFromCart -> Failed to prepare client data for purchase for client ID: {}. Error: {}", clientId, ecommerceDataDTOResult.getErrorMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseWrapper<>(false, null, ecommerceDataDTOResult.getErrorMessage(), 400));
        }

        Long cardId = purchaseFromCartDTO.getCardId();
        Long addressId = purchaseFromCartDTO.getAddressId();

        // Procces Purchase running in bakcgorund
        purchaseService.processPurchase(ecommerceDataDTOResult.getData(), cardId, addressId);
        log.info("purchaseProductsFromCart -> processing purchase for client ID: {}", clientId);

        // Dont Wait To Proccess Purchase and Return Response
        return ResponseEntity.status(HttpStatus.OK).body(ResponseWrapper.success("Purchase Requested. Payment Will Be Validated Soon."));
    }
}
