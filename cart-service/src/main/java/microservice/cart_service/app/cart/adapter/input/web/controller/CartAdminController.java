package microservice.cart_service.app.cart.adapter.input.web.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import libs_kernel.page.PageRequest;
import libs_kernel.page.PageResponse;
import microservice.cart_service.app.cart.adapter.input.web.annotation.CartAdminControllerTag;
import microservice.cart_service.app.cart.adapter.input.web.annotation.GetCartOperation;
import microservice.cart_service.app.cart.adapter.input.web.annotation.GetCustomerCartOperation;
import microservice.cart_service.app.cart.adapter.input.web.annotation.SearchCartOperation;
import microservice.cart_service.app.cart.adapter.input.web.dto.input.SearchCartsRequest;
import microservice.cart_service.app.cart.adapter.input.web.dto.output.CartResponse;
import microservice.cart_service.app.cart.adapter.input.web.mapper.CartResponseMapper;
import microservice.cart_service.app.cart.core.application.queries.GetCartByCustomerIdQuery;
import microservice.cart_service.app.cart.core.application.queries.GetCartByIdQuery;
import microservice.cart_service.app.cart.core.application.queries.SearchCartsQuery;
import microservice.cart_service.app.cart.core.domain.model.Cart;
import microservice.cart_service.app.cart.core.port.in.usecase.CartQueryUseCase;
import microservice.cart_service.app.shared.ResponseWrapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/carts/admin")
@CartAdminControllerTag
public class CartAdminController {
	private final CartQueryUseCase cartQueryUseCase;
	private final CartResponseMapper mapper;

	@Autowired
	public CartAdminController(CartQueryUseCase cartQueryUseCase, CartResponseMapper mapper) {
		this.cartQueryUseCase = cartQueryUseCase;
		this.mapper = mapper;
	}

	@GetCustomerCartOperation
	@GetMapping("/customers/{customerId}")
	private ResponseWrapper<CartResponse> getCustomerCart(@PathVariable @Valid @NotBlank String customerId) {
		var query = GetCartByCustomerIdQuery.from(customerId);
		Cart cart = cartQueryUseCase.getCartByCustomerId(query);

		CartResponse cartResponse = mapper.fromDomain(cart);
		return ResponseWrapper.found(cartResponse, "Customer Cart");
	}

	@GetCartOperation
	@GetMapping("/{cartId}")
	private ResponseWrapper<CartResponse> getCart(
			@PathVariable @Valid @NotBlank String cartId,
			@RequestParam(required = false) boolean includeItems,
			@RequestParam(required = false) boolean includeAfterwards) {

		var query = GetCartByIdQuery.from(
				cartId,
				includeItems,
				includeAfterwards
		);
		Cart cart = cartQueryUseCase.getCartById(query);

		CartResponse cartResponse = mapper.fromDomain(cart);
		return ResponseWrapper.found(cartResponse, "Cart");
	}

	@SearchCartOperation
	@GetMapping("/search")
	private ResponseWrapper<PageResponse<CartResponse>> searchCarts(
			@Valid @NotNull @ModelAttribute SearchCartsRequest params,
			@Valid @NotNull @ModelAttribute PageRequest pageParams) {
		Pageable pageable = pageParams.toPageable();
		SearchCartsQuery query = params.toQuery(pageable);

		Page<Cart> cartPage = cartQueryUseCase.searchCarts(query);

		PageResponse<CartResponse> responsePage = mapper.fromDomainPage(cartPage);
		return ResponseWrapper.found(responsePage, "Search Carts");
	}
}
