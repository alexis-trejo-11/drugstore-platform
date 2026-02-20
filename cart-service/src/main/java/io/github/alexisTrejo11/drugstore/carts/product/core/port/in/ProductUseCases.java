package io.github.alexisTrejo11.drugstore.carts.product.core.port.in;

import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.ProductId;
import io.github.alexisTrejo11.drugstore.carts.product.core.application.ProductInsertCommand;
import io.github.alexisTrejo11.drugstore.carts.product.core.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductUseCases {
	Page<Product> getProducts(Pageable pageable);

	List<ProductId> getUnavailableProductsIn(List<ProductId> productIds);
	List<ProductId> getOutOfStockProductsIn(List<ProductId> productIds);

	Product getProductById(String productId);

	void createProduct(ProductInsertCommand command);

	void updateProduct(ProductInsertCommand command);

	void deleteProduct(ProductId productId);

}
