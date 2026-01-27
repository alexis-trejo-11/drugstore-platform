package microservice.cart_service.app.product.core.port.in;

import microservice.cart_service.app.cart.core.domain.model.valueobjects.ProductId;
import microservice.cart_service.app.product.core.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductUseCases {
	Page<Product> getProducts(Pageable pageable);

	List<ProductId> getUnavailableProductsIn(List<ProductId> productIds);
	List<ProductId> getOutOfStockProductsIn(List<ProductId> productIds);

	Product getProductById(String productId);

	void createProduct(Product product);

	void updateProduct(Product product);

	void deleteProduct(String productId);
}
