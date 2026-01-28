package microservice.cart_service.app.cart.adapter.input.web.controller;

import jakarta.validation.constraints.NotNull;


import microservice.cart_service.app.cart.adapter.input.web.annotation.*;
import microservice.cart_service.app.cart.adapter.input.web.dto.input.CreateAfterwardsRequest;
import microservice.cart_service.app.cart.adapter.input.web.dto.input.DeleteAfterwardsRequest;
import microservice.cart_service.app.cart.core.application.command.CreateAfterwardsCommand;
import microservice.cart_service.app.cart.core.application.command.RemoveAfterwardsCommand;
import microservice.cart_service.app.cart.adapter.input.web.dto.output.CartResponse;
import microservice.cart_service.app.cart.adapter.input.web.mapper.CartResponseMapper;
import microservice.cart_service.app.cart.core.domain.model.Cart;
import microservice.cart_service.app.cart.core.port.in.usecase.CartCommandUseCase;
import microservice.cart_service.app.cart.core.port.in.usecase.CartQueryUseCase;
import microservice.cart_service.app.shared.ResponseWrapper;

import microservice.cart_service.app.cart.adapter.input.web.dto.input.BuyFromCartRequest;
import microservice.cart_service.app.cart.adapter.input.web.dto.input.UpdateCartRequest;
import microservice.cart_service.app.cart.core.application.command.BuyCartCommand;
import microservice.cart_service.app.cart.core.application.command.UpdateCartCommand;
import microservice.cart_service.app.cart.core.application.queries.GetCartByCustomerIdQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;


@RestController
@RequestMapping("/v2/api/carts/users")
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

	//TODO: Retrieve userId from auth context instead of path variable
	@GetMapping("/{userId}")
	@GetMyCartCartOperation
	private ResponseWrapper<CartResponse> getMyCart(@Valid @PathVariable @NotBlank String userId) {
		var query = GetCartByCustomerIdQuery.from(userId);

		Cart cart = cartQueryUseCase.getCartByCustomerId(query);

		CartResponse cartResponse = mapper.fromDomain(cart);
		return ResponseWrapper.found(cartResponse, "Cart");
	}


	@PutMapping("/{userId}")
	@UpdateMyCartItemsOperation
	private ResponseWrapper<Void> UpdateMyCartItems(
			@Valid @PathVariable @NotBlank String userId,
			@RequestBody @NotNull UpdateCartRequest updateCartRequest) {

		UpdateCartCommand command = updateCartRequest.toCommand(userId);
		commandUseCase.updateCartItems(command);

		return ResponseWrapper.success("Product Items successfully updated in cart");
	}

	@PostMapping("/{userId}/items/buy")
	@BuyCartItemsOperation
	private ResponseWrapper<Void> buyProductsInCart(
			@Valid @PathVariable @NotBlank String userId,
			@Valid @RequestBody @NotNull BuyFromCartRequest request) {

		BuyCartCommand command = request.toCommand(userId);
		commandUseCase.buyCart(command);

		return ResponseWrapper.success("Products successfully purchased from cart");
	}



	@PostMapping("items/move-to-afterwards")
	@MoveCartItemsToAfterwards
	public ResponseEntity<ResponseWrapper<Void>> moveItemsToAfterwards(
			@RequestBody @Valid @NotNull CreateAfterwardsRequest afterwardsRequest) {

		CreateAfterwardsCommand command = afterwardsRequest.toCommand();
		commandUseCase.moveItemToAfterwards(command);

		return ResponseEntity.ok(ResponseWrapper.ok("Products", "Moved to Afterwards"));
	}

	@RestoreItemsFromAfterwardsOperation
	@PostMapping("/restore-from-afterwards")
	public ResponseWrapper<Void> restoreItemsFromAfterwards(
			@Valid @RequestBody DeleteAfterwardsRequest afterwardsRequest) {

		RemoveAfterwardsCommand command = afterwardsRequest.toCommand();
		commandUseCase.removeItemFromAfterwards(command);

		return ResponseWrapper.ok("Products", "Returned to Cart");
	}
}
