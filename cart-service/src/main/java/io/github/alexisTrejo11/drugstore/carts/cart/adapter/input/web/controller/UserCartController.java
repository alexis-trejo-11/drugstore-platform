package io.github.alexisTrejo11.drugstore.carts.cart.adapter.input.web.controller;

import io.github.alexisTrejo11.drugstore.carts.cart.adapter.input.web.annotation.*;
import jakarta.validation.constraints.NotNull;

import io.github.alexisTrejo11.drugstore.carts.cart.adapter.input.web.annotation.*;
import io.github.alexisTrejo11.drugstore.carts.cart.adapter.input.web.dto.input.CreateAfterwardsRequest;
import io.github.alexisTrejo11.drugstore.carts.cart.adapter.input.web.dto.input.DeleteAfterwardsRequest;
import io.github.alexisTrejo11.drugstore.carts.cart.core.application.command.CreateAfterwardsCommand;
import io.github.alexisTrejo11.drugstore.carts.cart.core.application.command.RemoveAfterwardsCommand;
import io.github.alexisTrejo11.drugstore.carts.cart.adapter.input.web.dto.output.CartResponse;
import io.github.alexisTrejo11.drugstore.carts.cart.adapter.input.web.mapper.CartResponseMapper;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.Cart;
import io.github.alexisTrejo11.drugstore.carts.cart.core.port.in.usecase.CartCommandUseCase;
import io.github.alexisTrejo11.drugstore.carts.cart.core.port.in.usecase.CartQueryUseCase;
import io.github.alexisTrejo11.drugstore.carts.shared.ResponseWrapper;

import io.github.alexisTrejo11.drugstore.carts.cart.adapter.input.web.dto.input.BuyFromCartRequest;
import io.github.alexisTrejo11.drugstore.carts.cart.adapter.input.web.dto.input.UpdateCartRequest;
import io.github.alexisTrejo11.drugstore.carts.cart.core.application.command.BuyCartCommand;
import io.github.alexisTrejo11.drugstore.carts.cart.core.application.command.UpdateCartCommand;
import io.github.alexisTrejo11.drugstore.carts.cart.core.application.queries.GetCartByCustomerIdQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2/carts/users")
@CartUserControllerTag
public class UserCartController {
  private final CartCommandUseCase commandUseCase;
  private final CartResponseMapper mapper;
  private final CartQueryUseCase cartQueryUseCase;

  @Autowired
  public UserCartController(
      CartCommandUseCase commandUseCase,
      CartQueryUseCase cartQueryUseCase,
      CartResponseMapper mapper) {
    this.commandUseCase = commandUseCase;
    this.cartQueryUseCase = cartQueryUseCase;
    this.mapper = mapper;
  }

  @GetMapping("/my-cart")
  public ResponseWrapper<CartResponse> getMyCart(@RequestAttribute("userId") String userId) {
    var query = GetCartByCustomerIdQuery.from(userId);
    Cart cart = cartQueryUseCase.getCartByCustomerId(query);
    CartResponse cartResponse = mapper.fromDomain(cart);

    return ResponseWrapper.found(cartResponse, "Cart");
  }

  @PutMapping("/items/{userId}")
  @UpdateMyCartItemsOperation
  private ResponseWrapper<Void> updateMyCartItems(
      @RequestAttribute("userId") String userId,
      @Valid @RequestBody @NotNull UpdateCartRequest updateCartRequest) {
    UpdateCartCommand command = updateCartRequest.toCommand(userId);
    commandUseCase.updateCartItems(command);

    return ResponseWrapper.success("Product Items successfully updated in cart");
  }

  @PostMapping("/{userId}/items/buy")
  @BuyCartItemsOperation
  private ResponseWrapper<Void> buyProductsInCart(
      @RequestAttribute("userId") String userId,
      @Valid @RequestBody @NotNull BuyFromCartRequest request) {
    BuyCartCommand command = request.toCommand(userId);
    commandUseCase.buyCart(command);

    return ResponseWrapper.success("Products successfully purchased from cart");
  }

  @PostMapping("/items/move-to-afterwards")
  @MoveCartItemsToAfterwards
  public ResponseEntity<ResponseWrapper<Void>> moveItemsToAfterwards(
      @RequestAttribute("userId") String userId,
      @RequestBody @Valid @NotNull CreateAfterwardsRequest afterwardsRequest) {
    CreateAfterwardsCommand command = afterwardsRequest.toCommand(userId);
    commandUseCase.moveItemToAfterwards(command);

    return ResponseEntity.ok(ResponseWrapper.ok("Products", "Moved to Afterwards"));
  }

  @RestoreItemsFromAfterwardsOperation
  @PutMapping("/items/restore-from-afterwards")
  public ResponseWrapper<Void> restoreItemsFromAfterwards(
      @RequestAttribute("userId") String userId,
      @RequestBody @Valid @NotNull DeleteAfterwardsRequest afterwardsRequest) {
    RemoveAfterwardsCommand command = afterwardsRequest.toCommand(userId);
    commandUseCase.removeItemFromAfterwards(command);

    return ResponseWrapper.ok("Products", "Returned to Cart");
  }
}
