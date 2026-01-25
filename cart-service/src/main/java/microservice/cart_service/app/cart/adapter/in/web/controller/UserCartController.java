package microservice.cart_service.app.cart.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotNull;
import microservice.cart_service.app.cart.adapter.in.web.dto.input.CreateAfterwardsRequest;
import microservice.cart_service.app.cart.adapter.in.web.dto.input.DeleteAfterwardsRequest;
import microservice.cart_service.app.cart.core.application.command.CreateAfterwardsCommand;
import microservice.cart_service.app.cart.core.application.command.RemoveAfterwardsCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import microservice.cart_service.app.cart.adapter.in.web.dto.output.CartResponse;
import microservice.cart_service.app.cart.adapter.in.web.mapper.CartControllerMaper;
import microservice.cart_service.app.cart.core.domain.model.Cart;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.CustomerId;
import microservice.cart_service.app.cart.core.port.in.usecase.CartCommandUseCase;
import microservice.cart_service.app.cart.core.port.in.usecase.CartQueryUseCase;
import microservice.cart_service.app.shared.ResponseWrapper;

import jakarta.validation.constraints.NotBlank;
import microservice.cart_service.app.cart.adapter.in.web.dto.input.BuyFromCartRequest;
import microservice.cart_service.app.cart.adapter.in.web.dto.input.UpdateCartRequest;
import microservice.cart_service.app.cart.core.application.command.BuyCartCommand;
import microservice.cart_service.app.cart.core.application.command.UpdateCartCommand;
import microservice.cart_service.app.cart.core.application.queries.GetCartByCustomerIdQuery;

@RestController
@RequestMapping("/v2/api/carts/users")
public class UserCartController {
  private final CartCommandUseCase commandUseCase;
  private final CartControllerMaper maper;
  private final CartQueryUseCase cartQueryUseCase;

  @Autowired
  public UserCartController(CartCommandUseCase commandUseCase,
      CartQueryUseCase cartQueryUseCase,
      CartControllerMaper maper) {
    this.commandUseCase = commandUseCase;
    this.cartQueryUseCase = cartQueryUseCase;
    this.maper = maper;
  }

  @GetMapping("/{userId}")
  private ResponseWrapper<CartResponse> getUserCart(@Valid @PathVariable @NotBlank String userId) {
    var query = new GetCartByCustomerIdQuery(new CustomerId(userId));

    Cart cart = cartQueryUseCase.getCartByCustomerId(query);

    CartResponse cartResponse = maper.entityToResponse(cart);
    return ResponseWrapper.found(cartResponse, "Cart");
  }

  @PostMapping("/{userId}/products")
  private ResponseWrapper<Void> updateCartItems(
      @Valid @PathVariable @NotBlank String userId,
      @RequestBody UpdateCartRequest updateCartRequest) {

    UpdateCartCommand command = updateCartRequest.toCommand(userId);
    commandUseCase.updateCartItems(command);

    return ResponseWrapper.success("Product Items successfully updated in cart");
  }

  @PostMapping("/{userId}/buy")
  private ResponseWrapper<Void> buyProductsInCart(
      @Valid @PathVariable @NotBlank String userId,
      @RequestBody BuyFromCartRequest buyFromCartRequest) {

    BuyCartCommand command = buyFromCartRequest.toCommand(userId);
    commandUseCase.buyCart(command);

    return ResponseWrapper.success("Products successfully purchased from cart");
  }


  @Operation(summary = "Move product to afterwards", description = "Move a specific product emptyCart cart to afterwards list for a client.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Product successfully moved to afterwards"),
      @ApiResponse(responseCode = "400", description = "Failed to move product to afterwards")
  })
  @PostMapping("/move")
  public ResponseEntity<ResponseWrapper<Void>> moveItemsToAfterwards(
      @RequestBody @Valid @NotNull CreateAfterwardsRequest afterwardsRequest) {

    CreateAfterwardsCommand command = afterwardsRequest.toCommand();
    commandUseCase.moveItemToAfterwards(command);

    return ResponseEntity.ok(ResponseWrapper.ok("Products", "Moved to Afterwards"));
  }

  @Operation(summary = "Return product to cart", description = "Return a specific product emptyCart afterwards list to the cart for a client.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Product successfully returned to cart"),
      @ApiResponse(responseCode = "400", description = "Failed to return product to cart")
  })
  @PostMapping("/return")
  public ResponseEntity<ResponseWrapper<Void>> returnCartItemsFromAfterwards(
      @Valid @RequestBody DeleteAfterwardsRequest afterwardsRequest) {

    RemoveAfterwardsCommand command = afterwardsRequest.toCommand();
    commandUseCase.removeItemFromAfterwards(command);

    return ResponseEntity.ok(ResponseWrapper.ok("Products", "Restore"));
  }
}
