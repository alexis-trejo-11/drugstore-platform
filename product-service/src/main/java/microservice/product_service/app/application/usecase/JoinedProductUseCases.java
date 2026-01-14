package microservice.product_service.app.application.usecase;

import microservice.product_service.app.application.port.in.query.GetProductByIDQuery;
import microservice.product_service.app.application.port.in.usecase.*;
import microservice.product_service.app.domain.model.Product;
import microservice.product_service.app.domain.model.valueobjects.ProductID;
import microservice.product_service.app.application.port.in.command.CreateProductCommand;
import microservice.product_service.app.application.port.in.command.UpdateProductCommand;
import microservice.product_service.app.application.port.in.query.SearchProductsQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JoinedProductUseCases implements ProductCommandUseCases, ProductQueryUseCases {
	private final CreateProductUseCase createProductUseCase;
	private final GetProductUseCase getProductUseCase;
	private final SearchProductsUseCase searchProductsUseCase;
	private final UpdateProductUseCase updateProductUseCase;
	private final DeleteProductUseCase deleteProductUseCase;

	@Autowired
	public JoinedProductUseCases(CreateProductUseCase createProductUseCase,
	                             GetProductUseCase getProductUseCase,
	                             SearchProductsUseCase searchProductsUseCase,
	                             UpdateProductUseCase updateProductUseCase,
	                             DeleteProductUseCase deleteProductUseCase) {
		this.createProductUseCase = createProductUseCase;
		this.getProductUseCase = getProductUseCase;
		this.searchProductsUseCase = searchProductsUseCase;
		this.updateProductUseCase = updateProductUseCase;
		this.deleteProductUseCase = deleteProductUseCase;
	}

	@Override
	public Product createProduct(CreateProductCommand command) {
		return createProductUseCase.createProduct(command);
	}

	@Override
	public Product getProductByID(GetProductByIDQuery query) {
		return getProductUseCase.getProduct(query);
	}

	@Override
	public Product updateProduct(UpdateProductCommand command) {
		return updateProductUseCase.updateProduct(command);
	}

	@Override
	public void deleteProduct(ProductID productId) {
		deleteProductUseCase.deleteProduct(productId);
	}

	@Override
	public List<Product> searchProducts(SearchProductsQuery query) {
		return searchProductsUseCase.searchProducts(query);
	}
}
