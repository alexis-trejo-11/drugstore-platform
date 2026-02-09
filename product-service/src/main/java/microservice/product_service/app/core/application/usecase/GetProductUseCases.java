package microservice.product_service.app.core.application.usecase;

import microservice.product_service.app.core.port.input.query.GetProductByBarCodeQuery;
import microservice.product_service.app.core.port.input.query.GetProductByIDQuery;
import microservice.product_service.app.core.port.input.query.GetProductBySKUQuery;
import microservice.product_service.app.core.domain.exception.ProductNotFoundException;
import microservice.product_service.app.core.port.output.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import microservice.product_service.app.core.domain.model.Product;

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
