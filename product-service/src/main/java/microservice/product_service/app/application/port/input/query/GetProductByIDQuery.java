package microservice.product_service.app.application.port.input.query;

import microservice.product_service.app.domain.model.valueobjects.ProductID;


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
