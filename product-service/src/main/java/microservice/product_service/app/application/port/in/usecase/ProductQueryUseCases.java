package microservice.product_service.app.application.port.in.usecase;

import microservice.product_service.app.application.port.in.query.GetProductByIDQuery;
import microservice.product_service.app.application.port.in.query.SearchProductsQuery;
import microservice.product_service.app.domain.model.Product;

import java.util.List;

public interface ProductQueryUseCases {
  Product getProductByID(GetProductByIDQuery query);
  List<Product> searchProducts(SearchProductsQuery query);
}
