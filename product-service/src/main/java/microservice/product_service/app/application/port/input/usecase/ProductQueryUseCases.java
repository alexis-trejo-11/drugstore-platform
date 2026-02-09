package microservice.product_service.app.application.port.input.usecase;

import microservice.product_service.app.application.port.input.query.GetProductByBarCodeQuery;
import microservice.product_service.app.application.port.input.query.GetProductBySKUQuery;
import org.springframework.data.domain.Page;

import microservice.product_service.app.application.port.input.query.GetProductByIDQuery;
import microservice.product_service.app.application.port.input.query.SearchProductsQuery;
import microservice.product_service.app.domain.model.Product;

public interface ProductQueryUseCases {
  Product getProductByID(GetProductByIDQuery query);

  Product getProductBySKU(GetProductBySKUQuery query);

  Product getProductByBarcode(GetProductByBarCodeQuery query);

  Page<Product> searchProducts(SearchProductsQuery query);
}
