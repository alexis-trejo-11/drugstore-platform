package microservice.product_service.app.domain.port.in.usecase;

import microservice.product_service.app.domain.model.Product;
import microservice.product_service.app.domain.port.in.query.SearchProductsQuery;

import java.util.List;

public interface SearchProductsUseCase {
    List<Product> searchProducts(SearchProductsQuery query);
}
