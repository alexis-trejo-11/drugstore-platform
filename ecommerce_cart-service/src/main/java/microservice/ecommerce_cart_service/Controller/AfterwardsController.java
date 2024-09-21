package microservice.ecommerce_cart_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Cart.CartItemDTO;
import at.backend.drugstore.microservice.common_classes.Security.AuthSecurity;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import microservice.ecommerce_cart_service.Service.AfterwardsService;
import microservice.ecommerce_cart_service.Service.AfterwardsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/drugstore/ecommerce-carts/afterwards")
public class AfterwardsController {

    public final AuthSecurity authSecurity;
    public final AfterwardsService afterwardsService;

    @Autowired
    public AfterwardsController(AuthSecurity authSecurity, AfterwardsService afterwardsService) {
        this.authSecurity = authSecurity;
        this.afterwardsService = afterwardsService;
    }

    @Operation(summary = "Get afterward products by client ID", description = "Retrieve the list of products marked for afterwards for a specific client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Afterward items successfully fetched")
    })
    @GetMapping
    public ResponseWrapper<List<CartItemDTO>> getAfterwardProductsByClientId(HttpServletRequest request) {
        Long clientId = authSecurity.getClientIdFromToken(request);
        log.info("getAfterwardProductsByClientId -> Fetching afterward items for client ID: {}", clientId);

        List<CartItemDTO> cartItemDTOS = afterwardsService.getAfterwardsByClientId(clientId);
        return ResponseWrapper.ok(cartItemDTOS ,"Afterward Products","Retrieve");
    }

    @Operation(summary = "Move product to afterwards", description = "Move a specific product from cart to afterwards list for a client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product successfully moved to afterwards"),
            @ApiResponse(responseCode = "400", description = "Failed to move product to afterwards")
    })
    @PostMapping("/move/{productId}")
    public ResponseEntity<ResponseWrapper<Void>> moveProductToAfterwards(@PathVariable final Long productId,
                                                                         HttpServletRequest request) {
        Long clientId = authSecurity.getClientIdFromToken(request);
        log.info("moveProductToAfterwards -> Moving afterward items for client ID: {}", clientId);

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
    @DeleteMapping("/return/{productId}")
    public ResponseEntity<ResponseWrapper<Void>> returnProductToCart(@Valid @PathVariable final Long productId,
                                                                     HttpServletRequest request) {
        Long clientId = authSecurity.getClientIdFromToken(request);
        log.info("returnProductToCart -> Returning afterward items for client ID: {}", clientId);

        Result<Void> returnResult = afterwardsService.returnProductToCart(clientId, productId);
        if (!returnResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseWrapper.badRequest(returnResult.getErrorMessage()));
        }

        log.info("returnProductToCart -> Successfully moved product ID: {} to afterwards for client ID: {}", productId, clientId);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseWrapper.ok("Product", "Restore"));
    }
}
