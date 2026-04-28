package io.github.alexisTrejo11.drugstore.carts.cart.adapter.input.web.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import libs_kernel.page.PageRequest;
import libs_kernel.page.PageResponse;
import io.github.alexisTrejo11.drugstore.carts.cart.adapter.input.web.annotation.CartAdminControllerTag;
import io.github.alexisTrejo11.drugstore.carts.cart.adapter.input.web.annotation.GetCartOperation;
import io.github.alexisTrejo11.drugstore.carts.cart.adapter.input.web.annotation.GetCustomerCartOperation;
import io.github.alexisTrejo11.drugstore.carts.cart.adapter.input.web.annotation.SearchCartOperation;
import io.github.alexisTrejo11.drugstore.carts.cart.adapter.input.web.dto.input.SearchCartsRequest;
import io.github.alexisTrejo11.drugstore.carts.cart.adapter.input.web.dto.output.CartResponse;
import io.github.alexisTrejo11.drugstore.carts.cart.adapter.input.web.mapper.CartResponseMapper;
import io.github.alexisTrejo11.drugstore.carts.cart.core.application.queries.GetCartByCustomerIdQuery;
import io.github.alexisTrejo11.drugstore.carts.cart.core.application.queries.GetCartByIdQuery;
import io.github.alexisTrejo11.drugstore.carts.cart.core.application.queries.SearchCartsQuery;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.Cart;
import io.github.alexisTrejo11.drugstore.carts.cart.core.port.in.usecase.CartQueryUseCase;
import io.github.alexisTrejo11.drugstore.carts.shared.ResponseWrapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/carts/admin")
@CartAdminControllerTag
@PreAuthorize("hasRole('ADMIN')")
public class CartManagerController {
	private final CartQueryUseCase cartQueryUseCase;
	private final CartResponseMapper mapper;

	@Autowired
	public CartManagerController(CartQueryUseCase cartQueryUseCase, CartResponseMapper mapper) {
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
			@Valid @ModelAttribute SearchCartsRequest params,
			@Valid @ModelAttribute PageRequest pageParams) {
		if (params == null) {
			params = SearchCartsRequest.empty();
		}
		if (pageParams == null) {
			pageParams = PageRequest.defaultPageRequest();
		}

		Pageable pageable = pageParams.toPageable();
		SearchCartsQuery query = params.toQuery(pageable);

		Page<Cart> cartPage = cartQueryUseCase.searchCarts(query);

		PageResponse<CartResponse> responsePage = mapper.fromDomainPage(cartPage);
		return ResponseWrapper.found(responsePage, "Search Carts");
	}
}
