package microservice.ecommerce_cart_service.Controller;

import at.backend.drugstore.microservice.common_models.DTO.Cart.*;
import at.backend.drugstore.microservice.common_models.Utils.ApiResponse;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import at.backend.drugstore.microservice.common_models.Validations.ControllerValidation;
import microservice.ecommerce_cart_service.Service.CartItemService;
import microservice.ecommerce_cart_service.Service.CartService;
import microservice.ecommerce_cart_service.Service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("v1/api/ecommerce/carts")
public class EcommerceCartController {

    private static final Logger logger = Logger.getLogger(EcommerceCartController.class.getName());

    private final CartService cartService;
    private final CartItemService cartItemService;
    private final PurchaseService purchaseService;

    @Autowired
    public EcommerceCartController(CartService cartService, CartItemService cartItemService, PurchaseService purchaseService) {
        this.cartService = cartService;
        this.cartItemService = cartItemService;
        this.purchaseService = purchaseService;
    }

    /**
     * Create a new cart for the specified client.
     *
     * @param clientId The ID of the client for whom the cart is to be created.
     * @return A ResponseEntity containing an ApiResponse with the result of the cart creation.
     */
    @PostMapping("/create/{clientId}")
    public ResponseEntity<ApiResponse<Void>> createCart(@PathVariable final Long clientId) {
        logger.info("Creating cart for client ID: " + clientId);
        Result<Void> cartResult = cartService.createCart(clientId);
        if (!cartResult.isSuccess()) {
            logger.warning("Failed to create cart for client ID: " + clientId + ". Error: " + cartResult.getErrorMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>(false, null, cartResult.getErrorMessage(), 409));
        }
        logger.info("Successfully created cart for client ID: " + clientId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, null, "Cart Successfully Created.", 201));
    }

    /**
     * Get the cart for the specified client.
     *
     * @param clientId The ID of the client whose cart is to be retrieved.
     * @return A ResponseEntity containing an ApiResponse with the client's cart.
     */
    @GetMapping("/client/{clientId}")
    public ResponseEntity<ApiResponse<CartDTO>> getCartByClientId(@PathVariable Long clientId) {
        logger.info("Fetching cart for client ID: " + clientId);
        CartDTO cartDTO = cartService.getCartByClientId(clientId);
        if (cartDTO == null) {
            logger.warning("No cart found for client ID: " + clientId);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>(false, null, null, 409));
        }
        logger.info("Successfully fetched cart for client ID: " + clientId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, cartDTO, null, 200));
    }

    /**
     * Add a product to the client's cart.
     *
     * @param clientId  The ID of the client.
     * @param productId The ID of the product to add to the cart.
     * @param quantity  The quantity of the product to add to the cart.
     * @return A ResponseEntity containing an ApiResponse with the result of the add operation.
     */
    @PostMapping("/product/{clientId}/{productId}/{quantity}")
    public ResponseEntity<ApiResponse<Void>> addProductToCart(@PathVariable final Long clientId, @PathVariable final Long productId, @PathVariable final int quantity) {
        logger.info("Adding product ID: " + productId + " with quantity: " + quantity + " to cart for client ID: " + clientId);
        Result<Void> cartResult = cartItemService.addProductsCart(clientId, productId, quantity);
        if (!cartResult.isSuccess()) {
            logger.warning("Failed to add product ID: " + productId + " to cart for client ID: " + clientId + ". Error: " + cartResult.getErrorMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, cartResult.getErrorMessage(), 404));
        }
        logger.info("Successfully added product ID: " + productId + " to cart for client ID: " + clientId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, null, "Products Successfully Added.", 200));
    }

    /**
     * Delete a product from the client's cart.
     *
     * @param clientId           The ID of the client.
     * @param cartItemInsertDTO  The DTO containing the product details to be deleted from the cart.
     * @param bindingResult      The binding result for validation errors.
     * @return A ResponseEntity containing an ApiResponse with the result of the delete operation.
     */
    @DeleteMapping("/product/{clientId}")
    public ResponseEntity<ApiResponse<?>> deleteProductFromCart(@Valid @PathVariable final Long clientId, @RequestBody final CartItemInsertDTO cartItemInsertDTO, BindingResult bindingResult) {
        logger.info("Deleting product from cart for client ID: " + clientId);
        if (bindingResult.hasErrors()) {
            var validationErrors = ControllerValidation.handleValidationError(bindingResult);
            logger.warning("Validation errors while deleting product from cart for client ID: " + clientId + ". Errors: " + validationErrors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, validationErrors, "Validation Errors", 400));
        }

        Result<Void> deleteResult = cartItemService.deleteProductFromCart(clientId, cartItemInsertDTO);
        if (!deleteResult.isSuccess()) {
            logger.warning("Failed to delete product from cart for client ID: " + clientId + ". Error: " + deleteResult.getErrorMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, null, deleteResult.getErrorMessage(), 400));
        }
        logger.info("Successfully deleted product from cart for client ID: " + clientId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, null, "Product Successfully Deleted", 200));
    }

    /**
     * Purchase products from the client's cart.
     *
     * @param clientId  The ID of the client.
     * @param addressId The ID of the address to ship the products to.
     * @param cardId    The ID of the card to charge for the purchase.
     * @return A ResponseEntity containing an ApiResponse with the result of the purchase operation.
     */
    @PostMapping("/purchase")
    public ResponseEntity<ApiResponse<?>> purchaseProductsFromCart(@RequestParam Long clientId,
                                                                   @RequestParam Long addressId,
                                                                   @RequestParam Long cardId) {
        logger.info("Processing purchase for client ID: " + clientId);
        Result<ClientEcommerceDataDTO> ecommerceDataDTOResult = purchaseService.prepareClientData(clientId);
        if (!ecommerceDataDTOResult.isSuccess()) {
            logger.warning("Failed to prepare client data for purchase for client ID: " + clientId + ". Error: " + ecommerceDataDTOResult.getErrorMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, ecommerceDataDTOResult.getErrorMessage(), 404));
        }

        Result<List<CartItemDTO>> purchaseResult = cartItemService.processCartAndGetItems(ecommerceDataDTOResult.getData());
        if (!purchaseResult.isSuccess()) {
            logger.warning("Failed to process cart items for purchase for client ID: " + clientId + ". Error: " + purchaseResult.getErrorMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, null, purchaseResult.getErrorMessage(), 400));
        }

        purchaseService.processPurchase(ecommerceDataDTOResult.getData(), cardId, purchaseResult.getData(), addressId);
        logger.info("Successfully processed purchase for client ID: " + clientId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, null, "Order Created Payment Will Be Validate Soon.", 200));
    }
}
