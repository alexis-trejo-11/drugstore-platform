package io.github.alexisTrejo11.drugstore.products.core.application.usecase;

import io.github.alexisTrejo11.drugstore.products.core.port.input.query.GetProductByBarCodeQuery;
import io.github.alexisTrejo11.drugstore.products.core.port.input.query.GetProductByIDQuery;
import io.github.alexisTrejo11.drugstore.products.core.port.input.query.GetProductBySKUQuery;
import io.github.alexisTrejo11.drugstore.products.core.domain.exception.ProductNotFoundException;
import io.github.alexisTrejo11.drugstore.products.core.port.output.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.Product;

@Service
public class GetProductUseCases {

	private final ProductRepository productRepository;

	@Autowired
	public GetProductUseCases(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	public Product getProduct(GetProductByIDQuery query) {
		return productRepository.findByID(query.productId())
				.orElseThrow(() -> new ProductNotFoundException(query.productId()));
	}

	public Product getProduct(GetProductBySKUQuery query) {
		return productRepository.findBySKU(query.sku())
				.orElseThrow(() -> new ProductNotFoundException(query.sku()));
	}

	public Product getProduct(GetProductByBarCodeQuery query) {
		return productRepository.findByBarCode(query.barCode())
				.orElseThrow(() -> new ProductNotFoundException("Product with bar code " + query.barCode() + " not found"));
	}
}
