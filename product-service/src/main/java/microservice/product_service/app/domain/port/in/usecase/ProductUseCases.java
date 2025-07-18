package microservice.product_service.app.domain.port.in.usecase;

import microservice.product_service.app.domain.model.Product;
import microservice.product_service.app.domain.model.ProductId;
import microservice.product_service.app.domain.port.in.command.CreateProductCommand;
import microservice.product_service.app.domain.port.in.command.UpdateProductCommand;
import microservice.product_service.app.domain.port.in.query.SearchProductsQuery;

import java.util.List;

/**
 * A facade interface that bundles all product-related use cases.
 * Controllers or other driving adapters can inject this single interface
 * to access various product operations, reducing the number of direct
 * use case injections.
 */
public interface ProductUseCases {
    Product createProduct(CreateProductCommand command);
    Product getProduct(ProductId productId);
    Product updateProduct(UpdateProductCommand command);
    void deleteProduct(ProductId productId);
    List<Product> searchProducts(SearchProductsQuery query);
    //Product updateStock(ProductId productId, int quantity);

}
