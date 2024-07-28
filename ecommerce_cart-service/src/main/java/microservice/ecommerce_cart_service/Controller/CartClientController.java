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
    public ResponseEntity<ApiResponse<CartDTO>> getCartByClientId(@PathVariable Long clientId) {
        logger.info("Fetching cart for client ID: " + clientId);
        Optional<CartDTO> optionalCartDTO = cartService.getCartByClientId(clientId);
        if (optionalCartDTO.isEmpty()) {
            logger.warning("No cart found for client ID: " + clientId);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>(false, null, null, 409));
        }

        logger.info("Successfully fetched cart for client ID: " + clientId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, optionalCartDTO.get(), "Cart successfully fetched.", 200));
    }

    @PostMapping("/product/{clientId}")
    public ResponseEntity<ApiResponse<Void>> addProductToCart(@Valid @PathVariable final Long clientId, @RequestBody final CartItemInsertDTO cartItemInsertDTO) {
        var productId = cartItemInsertDTO.getProductId();
        var quantity = cartItemInsertDTO.getQuantity();
        logger.info("Adding product ID: " + productId + " with quantity: " + quantity + " to cart for client ID: " + clientId);

        Result<Void> cartResult = cartItemService.addProductsCart(clientId, productId, quantity);
        if (!cartResult.isSuccess()) {
            logger.warning("Failed to add product ID: " + productId + " to cart for client ID: " + clientId + ". Error: " + cartResult.getErrorMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, cartResult.getErrorMessage(), 404));
        }
        logger.info("Successfully added product ID: " + productId + " to cart for client ID: " + clientId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, null, "Products Successfully Added.", 200));
    }

    @DeleteMapping("/product/{clientId}")
    public ResponseEntity<ApiResponse<Void>> deleteProductFromCart(@Valid @PathVariable final Long clientId, @RequestBody final CartItemInsertDTO cartItemInsertDTO) {
        logger.info("Deleting product from cart for client ID: " + clientId);
        Result<Void> deleteResult = cartItemService.deleteProductFromCart(clientId, cartItemInsertDTO);
        if (!deleteResult.isSuccess()) {
            logger.warning("Failed to delete product from cart for client ID: " + clientId + ". Error: " + deleteResult.getErrorMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, null, deleteResult.getErrorMessage(), 400));
        }
        logger.info("Successfully deleted product from cart for client ID: " + clientId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, null, "Product Successfully Deleted", 200));
    }

    @PostMapping("/purchase")
    public ResponseEntity<ApiResponse<Void>> purchaseProductsFromCart(@Valid PurchaseFromCartDTO purchaseFromCartDTO) {
        var clientId = purchaseFromCartDTO.getClientId();
        logger.info("Processing purchase for client ID: " + clientId);

        Result<ClientEcommerceDataDTO> ecommerceDataDTOResult = purchaseService.prepareClientData(clientId);
        if (!ecommerceDataDTOResult.isSuccess()) {
            logger.warning("Failed to prepare client data for purchase for client ID: " + clientId + ". Error: " + ecommerceDataDTOResult.getErrorMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, ecommerceDataDTOResult.getErrorMessage(), 404));
        }

        var purchaseData  = cartItemService.processCartAndGePurchaseData(ecommerceDataDTOResult.getData(), purchaseFromCartDTO);
        var cardId = purchaseFromCartDTO.getCardId();
        var addressId = purchaseFromCartDTO.getAddressId();

        purchaseService.processPurchase(ecommerceDataDTOResult.getData(), cardId, purchaseData, addressId);
        logger.info("Successfully processed purchase for client ID: " + clientId);

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, null, "Order Created Payment Will Be Validate Soon.", 200));
    }

    @PostMapping("/move-to-afterwards/{clientId}/{productId}")
    public ResponseEntity<ApiResponse<Void>> moveProductToAfterwards(@Valid @PathVariable final Long clientId,  @PathVariable final Long productId) {
        Result<Void> afterwardsResult = afterwardsService.moveProductToAfterwards(clientId, productId);
        if (!afterwardsResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, null, afterwardsResult.getErrorMessage(), 400));
        }
        logger.info("Successfully move product ID: " + productId + " to afterwards for client ID: " + clientId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, null, "Product Successfully Move It To Afterwards", 200));
    }

    @DeleteMapping("/return-to-afterwards/{clientId}/{productId}")
    public ResponseEntity<ApiResponse<Void>> returnProductToCart(@Valid @PathVariable final Long clientId,  @PathVariable final Long productId) {
        afterwardsService.returnProductToCart(clientId, productId);
        logger.info("Successfully move product ID: " + productId + " to afterwards for client ID: " + clientId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, null, "Product Successfully Returned To Cart.", 200));
    }

    @GetMapping("/client-afterwards/{clientId}")
    public ResponseEntity<ApiResponse<List<CartItemDTO>>> getAfterwardProductsByClientId(@Valid @PathVariable final Long clientId) {
        List<CartItemDTO> cartItemDTOS = afterwardsService.getAfterwardsByClientId(clientId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, cartItemDTOS, "Afterward items successfully fetched!.", 200));
    }
}
