package microservice.ecommerce_cart_service.Controller;

import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import microservice.ecommerce_cart_service.Service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Slf4j
@RestController
@RequestMapping("v1/api/ecommerce/carts")
@Tag(name = "Drugstore Microservice API (Cart Service)", description = "Service for managing cart payments")

public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @Operation(summary = "Create a new cart", description = "Create a new cart for a specific client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cart successfully created"),
            @ApiResponse(responseCode = "409", description = "Failed to create cart due to a conflict")
    })
    @PostMapping("/create/{clientId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> createCart(@PathVariable final Long clientId) {
        log.info("Creating cart for client ID: {}", clientId);
        return cartService.createCart(clientId).thenApply(cartResult -> {
            if (!cartResult.isSuccess()) {
                log.warn("Failed to create cart for client ID: {}. Error: {}", clientId, cartResult.getErrorMessage());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseWrapper<>(false, null, cartResult.getErrorMessage(), 409));
            }

            log.info("Successfully created cart for client ID: {}", clientId);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseWrapper<>(true, null, "Cart Successfully Created.", 201));
        });
    }
}
