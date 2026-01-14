package microservice.product_service.app.application.port.in.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import microservice.product_service.app.application.port.in.usecase.ProductCommandUseCases;
import microservice.product_service.app.domain.model.Product;
import microservice.product_service.app.domain.model.valueobjects.ProductID;
import microservice.product_service.app.infrastructure.in.web.dto.ProductResponse;


public record GetProductByIDQuery(ProductID productId)  {

  public GetProductByIDQuery {
    if  (productId == null) {
      throw new IllegalArgumentException("ProductID cannot be null");
    }
  }

  public static GetProductByIDQuery of (String productID) {
    ProductID id = ProductID.from(productID);
    return new GetProductByIDQuery(id);

  }
}
