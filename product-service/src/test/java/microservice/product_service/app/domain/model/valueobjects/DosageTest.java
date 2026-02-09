package microservice.product_service.app.domain.model.valueobjects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import microservice.product_service.app.core.domain.model.valueobjects.Dosage;
import org.junit.jupiter.api.Test;

import microservice.product_service.app.core.domain.exception.ProductValueObjectException;

public class DosageTest {

  @Test
  void create_valid() {
    Dosage d = Dosage.create("1 tablet");
    assertThat(d.getValue()).isEqualTo("1 tablet");
    assertThat(d.isSpecified()).isTrue();
  }

  @Test
  void create_invalidFormat_throws() {
    assertThrows(ProductValueObjectException.class, () -> Dosage.create("tablet"));
  }

  @Test
  void create_empty_returnsNone() {
    Dosage d = Dosage.create("  ");
    assertThat(d.isEmpty()).isTrue();
  }
}
