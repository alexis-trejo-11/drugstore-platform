package microservice.ecommerce_cart_service.Controller;

import at.backend.drugstore.microservice.common_models.DTO.Cart.*;
import at.backend.drugstore.microservice.common_models.DTO.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentInsertDTO;
import at.backend.drugstore.microservice.common_models.ExternalService.Adress.ExternalAddressService;
import at.backend.drugstore.microservice.common_models.ExternalService.Client.ExternalClientService;
import at.backend.drugstore.microservice.common_models.Utils.ErrorResponseUtil;
import at.backend.drugstore.microservice.common_models.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import at.backend.drugstore.microservice.common_models.Validations.CustomControllerResponse;
import at.backend.drugstore.microservice.common_models.Validations.ControllerValidation;
import microservice.ecommerce_cart_service.Service.CartItemService;
import microservice.ecommerce_cart_service.Service.CartService;
import microservice.ecommerce_cart_service.Service.ExternalService;
import microservice.ecommerce_cart_service.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/ecommerce/carts")
public class EcommerceCartController {

    private static final Logger logger = Logger.getLogger(EcommerceCartController.class.getName());

    private final CartService cartService;
    private final CartItemService cartItemService;
    private final OrderService orderService;
    private final ExternalService externalService;

    @Autowired
    public EcommerceCartController(CartService cartService, CartItemService cartItemService,
                                   OrderService orderService, ExternalClientService externalClientService,
                                   ExternalAddressService externalAddressService, ExternalService externalService) {
        this.cartService = cartService;
        this.cartItemService = cartItemService;
        this.orderService = orderService;
        this.externalService = externalService;
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
            @PathVariable Long clientId) {

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
    public ResponseEntity<?> purchaseProductsFromCart(@RequestParam Long clientId,
                                                      @RequestParam Long addressId,
                                                      @RequestParam Long cardId) {
        try {
            // Get External Service Data
            Result<ClientEcommerceDataDTO> ecommerceDataDTOResult = externalService.getExternalServiceDataById(clientId);
            if (!ecommerceDataDTOResult.isSuccess()) {
               return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ecommerceDataDTOResult.getErrorMessage());
            }
            var clientData = ecommerceDataDTOResult.getData();

            // Update Cart
            Result<List<CartItemDTO>> purchaseResult = cartItemService.purchaseProductsFromCart(clientData);
            if (!purchaseResult.isSuccess()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CustomControllerResponse(
                        HttpStatus.NOT_FOUND,
                        purchaseResult.getErrorMessage()
                ));
            }

            List<CartItemDTO> itemsToPurchase  = purchaseResult.getData();


            // Create Order
            Result<Void> orderResult = orderService.CreateOrder(itemsToPurchase, clientData, addressId);
            if (!orderResult.isSuccess()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new CustomControllerResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        orderResult.getErrorMessage()));
            }

            // Proceed to create payment if order is created successfully
            externalService.makePayment(clientData, cardId);

            // Return Success Response
            return ResponseEntity.status(HttpStatus.OK).body(new CustomControllerResponse(
                    HttpStatus.OK,
                    "Success!")
            );
        } catch (Exception ex) {
            // Handle Exceptions
            logger.log(Level.SEVERE, "Error purchasing products from cart", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

