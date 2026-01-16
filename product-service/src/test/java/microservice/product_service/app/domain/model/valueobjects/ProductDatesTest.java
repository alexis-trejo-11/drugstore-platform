package microservice.product_service.app.domain.model.valueobjects;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import microservice.product_service.app.domain.exception.ProductValueObjectException;

public class ProductDatesTest {

  @Test
  void create_valid() {
    LocalDateTime m = LocalDateTime.now().minusDays(10);
    LocalDateTime e = LocalDateTime.now().plusDays(10);
    ProductDates d = ProductDates.create(m, e);
    assertThat(d.manufactureDate()).isEqualTo(m);
    assertThat(d.expirationDate()).isEqualTo(e);
  }

  @Test
  void create_expirationBeforeManufacture_throws() {
    LocalDateTime m = LocalDateTime.now();
    LocalDateTime e = m.minusDays(1);
    assertThrows(ProductValueObjectException.class, () -> ProductDates.create(m, e));
  }
}
