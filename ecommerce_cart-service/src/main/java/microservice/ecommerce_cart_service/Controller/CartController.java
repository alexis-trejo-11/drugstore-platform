package microservice.ecommerce_cart_service.Controller;

import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import microservice.ecommerce_cart_service.Service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@RestController
@RequestMapping("v1/api/ecommerce/carts")
public class CartController {

    private static final Logger logger = Logger.getLogger(CartController.class.getName());
    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/create/{clientId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> createCart(@PathVariable final Long clientId) {
        logger.info("Creating cart for client ID: " + clientId);
        return cartService.createCart(clientId).thenApply(cartResult -> {
            if (!cartResult.isSuccess()) {
                logger.warning("Failed to create cart for client ID: " + clientId + ". Error: " + cartResult.getErrorMessage());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseWrapper<>(false, null, cartResult.getErrorMessage(), 409));
            }

            logger.info("Successfully created cart for client ID: " + clientId);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseWrapper<>(true, null, "Cart Successfully Created.", 201));
        });
    }
}
