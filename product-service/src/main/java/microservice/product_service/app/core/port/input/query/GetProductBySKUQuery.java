package microservice.product_service.app.core.port.input.query;

import microservice.product_service.app.core.domain.model.valueobjects.SKU;


public record GetProductBySKUQuery(SKU sku)  {
  public GetProductBySKUQuery {
    if  (sku == null) {
      throw new IllegalArgumentException("SKU cannot be null");
    }
  }

  public static GetProductBySKUQuery from(String skuStr) {
    return new GetProductBySKUQuery(new SKU(skuStr));
  }
}
