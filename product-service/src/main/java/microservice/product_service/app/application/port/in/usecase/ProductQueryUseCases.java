package microservice.product_service.app.application.port.in.usecase;

import microservice.product_service.app.application.port.in.query.GetProductByBarCodeQuery;
import microservice.product_service.app.application.port.in.query.GetProductBySKUQuery;
import microservice.product_service.app.domain.model.valueobjects.ProductID;
import org.springframework.data.domain.Page;

import microservice.product_service.app.application.port.in.query.GetProductByIDQuery;
import microservice.product_service.app.application.port.in.query.SearchProductsQuery;
import microservice.product_service.app.domain.model.Product;

public interface ProductQueryUseCases {
  Product getProductByID(GetProductByIDQuery query);

  Product getProductBySKU(GetProductBySKUQuery query);

  Product getProductByBarcode(GetProductByBarCodeQuery query);

  Page<Product> searchProducts(SearchProductsQuery query);
}
