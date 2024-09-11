package microservice.ecommerce_cart_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Cart.*;
import at.backend.drugstore.microservice.common_classes.Security.AuthSecurity;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
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
    @PostMapping(value = "/product/{clientId}", produces = MediaType.APPLICATION_JSON_VALUE)
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
    @DeleteMapping("/product/{clientId}")
    public ResponseEntity<ResponseWrapper<Void>> deleteProductFromCart(@Valid @PathVariable final Long clientId, @RequestBody final CartItemInsertDTO cartItemInsertDTO) {
        log.info("Deleting product from cart for client ID: {}", clientId);

        Result<?> deleteResult = clientCartService.deleteProductFromCart(clientId, cartItemInsertDTO);
        if (!deleteResult.isSuccess()) {
            log.warn("Failed to delete product from cart for client ID: {}. Error: {}", clientId, deleteResult.getErrorMessage());
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

        // Bring Data From Another Services
        var ecommerceDataFuture = purchaseServiceFacade.prepareClientData(clientId);
        Result<ClientEcommerceDataDTO> ecommerceDataDTOResult  = ecommerceDataFuture.join();

        if (!ecommerceDataDTOResult.isSuccess()) {
            log.warn("purchaseProductsFromCart -> Failed to prepare client data for purchase for client ID: {}. Error: {}", clientId, ecommerceDataDTOResult.getErrorMessage());
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseWrapper<>(false, null, ecommerceDataDTOResult.getErrorMessage(), 404));
        }

        var purchaseData = clientCartService.processCartAndGetPurchaseData(ecommerceDataDTOResult.getData(), purchaseFromCartDTO);
        Long cardId = purchaseFromCartDTO.getCardId();
        Long addressId = purchaseFromCartDTO.getAddressId();

        // Asynchronously process the purchase
        CompletableFuture<Void> processPurchaseFuture = purchaseServiceFacade.processPurchase(ecommerceDataDTOResult.getData(), cardId, purchaseData, addressId);
        processPurchaseFuture.join();

        log.info("purchaseProductsFromCart -> Successfully processed purchase for client ID: {}", clientId);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseWrapper<>(true, null, "Order Created. Payment Will Be Validated Soon.", 200));
    }

    @Operation(summary = "Get afterward products by client ID", description = "Retrieve the list of products marked for afterwards for a specific client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Afterward items successfully fetched")
    })
    @GetMapping("/client-afterwards/{clientId}")
    public ResponseWrapper<List<CartItemDTO>> getAfterwardProductsByClientId(HttpServletRequest request) {
        Long clientId = authSecurity.getClientIdFromToken(request);
        log.info("getAfterwardProductsByClientId -> Fetching cart for client ID: {}", clientId);

        List<CartItemDTO> cartItemDTOS = afterwardsService.getAfterwardsByClientId(clientId);
        return ResponseWrapper.ok("Afterward Products", "Retrieve", cartItemDTOS);
    }

    @Operation(summary = "Move product to afterwards", description = "Move a specific product from cart to afterwards list for a client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product successfully moved to afterwards"),
            @ApiResponse(responseCode = "400", description = "Failed to move product to afterwards")
    })
    @PostMapping("/move-to-afterwards/{clientId}/{productId}")
    public ResponseEntity<ResponseWrapper<Void>> moveProductToAfterwards(@Valid @PathVariable final Long clientId, @PathVariable final Long productId) {
        Result<Void> afterwardsResult = afterwardsService.moveProductToAfterwards(clientId, productId);
        if (!afterwardsResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseWrapper.badRequest(afterwardsResult.getErrorMessage()));
        }

        log.info("Successfully moved product ID: {} to afterwards for client ID: {}", productId, clientId);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseWrapper<>(true, null, "Product successfully moved to Afterwards", 200));
    }

    @Operation(summary = "Return product to cart", description = "Return a specific product from afterwards list to the cart for a client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product successfully returned to cart"),
            @ApiResponse(responseCode = "400", description = "Failed to return product to cart")
    })
    @DeleteMapping("/return-to-afterwards/{clientId}/{productId}")
    public ResponseEntity<ResponseWrapper<Void>> returnProductToCart(@Valid @PathVariable final Long clientId, @PathVariable final Long productId) {
        Result<Void> returnResult = afterwardsService.returnProductToCart(clientId, productId);
            if (!returnResult.isSuccess()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseWrapper.badRequest(returnResult.getErrorMessage()));
            }

            log.info("returnProductToCart -> Successfully moved product ID: {} to afterwards for client ID: {}", productId, clientId);
            return ResponseEntity.status(HttpStatus.OK).body(ResponseWrapper.ok("Product", "Restore"));
    }
}
