package io.github.alexisTrejo11.drugstore.products.core.port.input.usecase;

import io.github.alexisTrejo11.drugstore.products.core.port.input.query.GetProductByBarCodeQuery;
import io.github.alexisTrejo11.drugstore.products.core.port.input.query.GetProductBySKUQuery;
import org.springframework.data.domain.Page;

import io.github.alexisTrejo11.drugstore.products.core.port.input.query.GetProductByIDQuery;
import io.github.alexisTrejo11.drugstore.products.core.port.input.query.SearchProductsQuery;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.Product;

public interface ProductQueryUseCases {
  Product getProductByID(GetProductByIDQuery query);

  Product getProductBySKU(GetProductBySKUQuery query);

  Product getProductByBarcode(GetProductByBarCodeQuery query);

  Page<Product> searchProducts(SearchProductsQuery query);
}
