package microservice.product_service.app.domain.model.valueobjects;

import static org.assertj.core.api.Assertions.assertThat;

import microservice.product_service.app.core.domain.model.valueobjects.ProductTimeStamps;
import org.junit.jupiter.api.Test;

public class ProductTimeStampsTest {

  @Test
  void now_and_delete_and_restore() throws InterruptedException {
    ProductTimeStamps ts = ProductTimeStamps.now();
    assertThat(ts.getCreatedAt()).isNotNull();
    assertThat(ts.getUpdatedAt()).isNotNull();

    ts.markAsDeleted();
    assertThat(ts.getDeletedAt()).isNotNull();

    ts.restore();
    assertThat(ts.getDeletedAt()).isNull();
  }
}
