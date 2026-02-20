package io.github.alexisTrejo11.drugstore.products.core.port.input.query;

import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.ProductID;


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
