package io.github.alexisTrejo11.drugstore.products.core.port.input.query;

import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.SKU;


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
