package microservice.product_service.app.domain.model.valueobjects;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import microservice.product_service.app.core.domain.model.valueobjects.ProductPrice;
import org.junit.jupiter.api.Test;

import microservice.product_service.app.core.domain.exception.ProductValueObjectException;

public class ProductPriceTest {

  @Test
  void create_valid() {
    ProductPrice p = ProductPrice.create(new BigDecimal("19.99"));
    assertThat(p.value()).isEqualByComparingTo(new BigDecimal("19.99"));
  }

  @Test
  void create_null_throws() {
    assertThrows(ProductValueObjectException.class, () -> ProductPrice.create(null));
  }

  @Test
  void create_negative_throws() {
    assertThrows(ProductValueObjectException.class, () -> ProductPrice.create(new BigDecimal("-1.00")));
  }

  @Test
  void create_tooManyDecimals_throws() {
    assertThrows(ProductValueObjectException.class, () -> ProductPrice.create(new BigDecimal("1.123")));
  }
}
