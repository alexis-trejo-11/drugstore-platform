package microservice.product_service.app.domain.model.valueobjects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import microservice.product_service.app.core.domain.exception.ProductValueObjectException;
import microservice.product_service.app.core.domain.model.valueobjects.Administration;
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
