package io.github.alexisTrejo11.drugstore.products.app.domain.model.valueobjects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.alexisTrejo11.drugstore.products.core.domain.exception.ProductValueObjectException;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.Administration;
import org.junit.jupiter.api.Test;

public class AdministrationTest {

  @Test
  void fromCommonType_iv() {
    Administration a = Administration.fromCommonType("iv");
    assertThat(a).isEqualTo(Administration.INTRAVENOUS);
    assertThat(a.getDisplayName()).isEqualTo("Intravenous");
  }

  @Test
  void create_invalidShort_throws() {
    assertThrows(ProductValueObjectException.class,
        () -> Administration.create("x"));
  }

  @Test
  void create_none_returnsNone() {
    Administration a = Administration.create("  ");
    assertThat(a.isEmpty()).isTrue();
  }
}
