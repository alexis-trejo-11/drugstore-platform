package microservice.product_service.app.application.port.in.usecase;

import org.springframework.data.domain.Page;

import microservice.product_service.app.application.port.in.query.GetProductByIDQuery;
import microservice.product_service.app.application.port.in.query.SearchProductsQuery;
import microservice.product_service.app.domain.model.Product;

public interface ProductQueryUseCases {
  Product getProductByID(GetProductByIDQuery query);

  Page<Product> searchProducts(SearchProductsQuery query);
}
