package microservice.product_service.app.application.port.input.query;


public record GetProductByBarCodeQuery(String barCode)  {
  public GetProductByBarCodeQuery {
    if  (barCode == null) {
      throw new IllegalArgumentException("Barcode cannot be null");
    }
  }
}
