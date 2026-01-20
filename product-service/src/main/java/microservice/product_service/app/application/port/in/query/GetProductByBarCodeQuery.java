package microservice.product_service.app.application.port.in.query;

import microservice.product_service.app.domain.model.valueobjects.SKU;


public record GetProductByBarCodeQuery(String barCode)  {
  public GetProductByBarCodeQuery {
    if  (barCode == null) {
      throw new IllegalArgumentException("Barcode cannot be null");
    }
  }
}
