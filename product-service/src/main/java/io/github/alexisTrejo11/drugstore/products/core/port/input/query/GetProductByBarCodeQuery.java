package io.github.alexisTrejo11.drugstore.products.core.port.input.query;


public record GetProductByBarCodeQuery(String barCode)  {
  public GetProductByBarCodeQuery {
    if  (barCode == null) {
      throw new IllegalArgumentException("Barcode cannot be null");
    }
  }
}
