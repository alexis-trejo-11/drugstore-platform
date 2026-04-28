package io.github.alexisTrejo11.drugstore.carts.product.adapter.input.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import libs_kernel.page.PageRequest;
import libs_kernel.page.PageResponse;
import io.github.alexisTrejo11.drugstore.carts.product.adapter.input.dto.ProductResponse;
import io.github.alexisTrejo11.drugstore.carts.product.core.domain.Product;
import io.github.alexisTrejo11.drugstore.carts.product.core.port.in.ProductUseCases;
import io.github.alexisTrejo11.drugstore.carts.shared.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v2/carts/products")
public class ProductController {
	private final ProductUseCases productUseCases;

	@Autowired
	public ProductController(ProductUseCases productUseCases) {
		this.productUseCases = productUseCases;
	}

	@GetMapping
	public ResponseWrapper<PageResponse<ProductResponse>> getProducts(@ModelAttribute @Valid PageRequest pageRequest) {
		if (pageRequest == null) {
			pageRequest = PageRequest.defaultPageRequest();
		}

		Page<Product> productPage = productUseCases.getProducts(pageRequest.toPageable());

		Page<ProductResponse> responsePage = productPage.map(ProductResponse::from);
		return ResponseWrapper.found(PageResponse.from(responsePage), "Products");
	}

	@GetMapping("/{productId}")
	public ResponseWrapper<ProductResponse> getProductById(@PathVariable @Valid @NotBlank String productId) {
		Product product = productUseCases.getProductById(productId);
		return ResponseWrapper.found(ProductResponse.from(product), "Product");
	}
}
