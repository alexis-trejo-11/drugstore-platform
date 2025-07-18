package microservice.product_service.app.application;

import lombok.RequiredArgsConstructor;
import microservice.product_service.app.domain.model.Product;
import microservice.product_service.app.domain.model.ProductId;
import microservice.product_service.app.domain.port.in.command.CreateProductCommand;
import microservice.product_service.app.domain.port.in.command.UpdateProductCommand;
import microservice.product_service.app.domain.port.in.query.SearchProductsQuery;
import microservice.product_service.app.domain.port.in.usecase.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductUseCaseImpl implements ProductUseCases {
    private final CreateProductUseCase createProductUseCase;
    private final GetProductUseCase getProductUseCase;
    private final SearchProductsUseCase searchProductsUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    //private final UpdateStockUseCase updateStockUseCase;

    @Override
    public Product createProduct(CreateProductCommand command) {
        return createProductUseCase.createProduct(command);
    }

    @Override
    public Product getProduct(ProductId productId) {
        return getProductUseCase.getProduct(productId);
    }

    @Override
    public Product updateProduct(UpdateProductCommand command) {
        return updateProductUseCase.updateProduct(command);
    }

    @Override
    public void deleteProduct(ProductId productId) {
        deleteProductUseCase.deleteProduct(productId);
    }

    @Override
    public List<Product> searchProducts(SearchProductsQuery query) {
        return searchProductsUseCase.searchProducts(query);
    }

    //@Override
    //public Product updateStock(ProductId productId, int quantity) {
    //    return updateStockUseCase.updateStock(productId, quantity);
    //}
}
