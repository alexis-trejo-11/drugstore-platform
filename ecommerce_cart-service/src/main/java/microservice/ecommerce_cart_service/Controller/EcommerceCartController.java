package microservice.ecommerce_cart_service.Controller;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.DTO.Cart.CartItemInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Cart.PurchaseSummaryDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.ExternalService.Adress.ExternalAddressService;
import at.backend.drugstore.microservice.common_models.ExternalService.Client.ExternalClientService;
import at.backend.drugstore.microservice.common_models.Utils.ErrorResponseUtil;
import at.backend.drugstore.microservice.common_models.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.ecommerce_cart_service.Service.CartItemService;
import microservice.ecommerce_cart_service.Service.CartService;
import microservice.ecommerce_cart_service.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/ecommerce/carts")
public class EcommerceCartController {

    private static final Logger logger = Logger.getLogger(EcommerceCartController.class.getName());

    private final CartService cartService;
    private final CartItemService cartItemService;
    private final OrderService orderService;
    private final ExternalClientService externalClientService;
    private final ExternalAddressService externalAddressService;

    @Autowired
    public EcommerceCartController(CartService cartService, CartItemService cartItemService, OrderService orderService, ExternalClientService externalClientService, ExternalAddressService externalAddressService) {
        this.cartService = cartService;
        this.cartItemService = cartItemService;
        this.orderService = orderService;
        this.externalClientService = externalClientService;
        this.externalAddressService = externalAddressService;
    }

    @PostMapping("/create/{clientId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> createCart(@PathVariable final Long clientId) {
        return cartService.createCart(clientId)
                .thenApply(result -> {
                    if (!result.isSuccess()) {
                        HttpStatus notFound = HttpStatus.NOT_FOUND;
                        ResponseWrapper<Void> errorResponse = ErrorResponseUtil.createErrorResponse(notFound, result.getErrorMessage());
                        return ResponseEntity.status(notFound).body(errorResponse);
                    } else {
                        ResponseWrapper<Void> response = new ResponseWrapper<>(result.getData(), null, HttpStatus.CREATED);
                        return ResponseEntity.status(HttpStatus.CREATED).body(response);
                    }
                })
                .exceptionally(ex -> {
                    logger.log(Level.SEVERE, "Error creating cart for client " + clientId, ex);
                    HttpStatus internalServerError = HttpStatus.INTERNAL_SERVER_ERROR;
                    ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, ex.getMessage(), HttpStatus.CREATED);
                    return ResponseEntity.status(internalServerError).body(errorResponse);
                });
    }

    @GetMapping("/client/{clientId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<CartDTO>>> getCartByClientId(
            @PathVariable @Min(1) Long clientId) {

        if (clientId <= 0) {
            ResponseWrapper<CartDTO> errorResponse = ErrorResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Client ID must be greater than 0");
                     return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
        }

        return cartService.getCartByClientId(clientId)
                .thenApply(result -> {
                    if (!result.isSuccess()) {
                        ResponseWrapper<CartDTO> errorResponse = ErrorResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, result.getErrorMessage());
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                    } else {
                        ResponseWrapper<CartDTO> response = new ResponseWrapper<>(result.getData(), null, HttpStatus.OK);
                        return ResponseEntity.status(HttpStatus.OK).body(response);
                    }
                })
                .exceptionally(ex -> {
                    ResponseWrapper<CartDTO> errorResponse = ErrorResponseUtil.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);

                });
    }

    @PostMapping("/product/{clientId}/{productId}/{quantity}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> addProductToCart(@PathVariable final Long clientId, @PathVariable final Long productId, @PathVariable final int quantity) {

        return cartItemService.addProductsCart(clientId, productId, quantity)
                .thenApply(result -> {
                    if (result.isSuccess()) {
                        ResponseWrapper<Void> response = new ResponseWrapper<>(null, null, HttpStatus.OK);
                        return ResponseEntity.status(HttpStatus.OK).body(response);
                    } else {
                        ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, result.getErrorMessage(), HttpStatus.NOT_FOUND);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                    }
                })
                .exceptionally(ex -> {
                    logger.log(Level.SEVERE, "Error adding product to cart", ex);
                    ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                });
    }

    @DeleteMapping("/product/{clientId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> deleteProductFromCart(@Valid @PathVariable final Long clientId, @RequestBody final CartItemInsertDTO cartItemInsertDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, errors.toString(), HttpStatus.BAD_REQUEST);
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
        }

        return cartItemService.deleteProductFromCart(clientId, cartItemInsertDTO)
                .thenApply(result -> {
                    if (result.isSuccess()) {
                        ResponseWrapper<Void> response = new ResponseWrapper<>(null, null, HttpStatus.OK);
                        return ResponseEntity.status(HttpStatus.OK).body(response);
                    } else {
                        HttpStatus notFound = HttpStatus.NOT_FOUND;
                        ResponseWrapper<Void> errorResponse = ErrorResponseUtil.createErrorResponse(notFound, result.getErrorMessage());
                        return ResponseEntity.status(notFound).body(errorResponse);
                    }
                })
                .exceptionally(ex -> {
                    logger.log(Level.SEVERE, "Error deleting product from cart", ex);
                    HttpStatus internalServerError = HttpStatus.INTERNAL_SERVER_ERROR;
                    ResponseWrapper<Void> errorResponse = ErrorResponseUtil.createErrorResponse(internalServerError, ex.getMessage());
                    return ResponseEntity.status(internalServerError).body(errorResponse);
                });
    }


    @PostMapping("/purchase")
    public CompletionStage<ResponseEntity<ResponseWrapper<Void>>> purchaseProductsFromCart(@RequestParam  Long clientId, @RequestParam Long addressId) {

        Result<ClientDTO> clientDTOResult = externalClientService.findClientById(clientId);
        if (!clientDTOResult.isSuccess()) {
                ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, clientDTOResult.getErrorMessage(), clientDTOResult.getStatus());
            return CompletableFuture.completedFuture(ResponseEntity.status(clientDTOResult.getStatus()).body(errorResponse));
        }

        Result<AddressDTO> addressDTOResult = externalAddressService.getAddressId(addressId);
        if (!addressDTOResult.isSuccess()) {
            ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, addressDTOResult.getErrorMessage(), addressDTOResult.getStatus());
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
        }

        boolean isAddressValidate = orderService.ValidateAddress(clientDTOResult.getData(), addressDTOResult.getData());
        if (!isAddressValidate) {
           ResponseWrapper<Void> responseWrapper = new ResponseWrapper<>(null, "Invalid Address", HttpStatus.FORBIDDEN);
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseWrapper));
        }

        return cartItemService.purchaseProductsFromCart(clientId)
                .thenCompose(result -> {
                    if (result.isSuccess()) {
                        return orderService.CreateOrder(result.getData(), addressDTOResult.getData(), clientDTOResult.getData() )
                                .thenApply(orderResult -> {
                                    if (orderResult.isSuccess()) {
                                        ResponseWrapper<Void> response = new ResponseWrapper<>(null, "Order Created, Payment Will Be Authorized Soon!", HttpStatus.OK);
                                        return ResponseEntity.status(HttpStatus.OK).body(response);
                                    } else {
                                        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
                                        ResponseWrapper<Void> errorResponse = ErrorResponseUtil.createErrorResponse(badRequest, orderResult.getErrorMessage());
                                        return ResponseEntity.status(badRequest).body(errorResponse);
                                    }
                                });
                    } else {
                        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
                        ResponseWrapper<Void> errorResponse = ErrorResponseUtil.createErrorResponse(badRequest, result.getErrorMessage());
                        return CompletableFuture.completedFuture(ResponseEntity.status(badRequest).body(errorResponse));
                    }
                })
                .exceptionally(ex -> {
                    logger.log(Level.SEVERE, "Error purchasing products from cart", ex);
                    HttpStatus internalServerError = HttpStatus.INTERNAL_SERVER_ERROR;
                    ResponseWrapper<Void> errorResponse = ErrorResponseUtil.createErrorResponse(internalServerError, ex.getMessage());
                    return ResponseEntity.status(internalServerError).body(errorResponse);
                });
    }
}
