package io.github.alexisTrejo11.drugstore.products.app.domain.model.valueobjects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.ProductName;
import org.junit.jupiter.api.Test;

import io.github.alexisTrejo11.drugstore.products.core.domain.exception.ProductValueObjectException;

public class ProductNameTest {

  @Test
  void create_valid() {
    ProductName n = ProductName.create("A cool product");
    assertThat(n.value()).isEqualTo("A cool product");
  }

  @Test
  void create_empty_throws() {
    assertThrows(ProductValueObjectException.class, () -> ProductName.create("  "));
  }

  @Test
  void create_tooLong_throws() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 110; i++)
      sb.append('x');
    assertThrows(ProductValueObjectException.class, () -> ProductName.create(sb.toString()));
  }
}
