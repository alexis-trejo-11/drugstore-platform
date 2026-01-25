package microservice.cart_service.app.cart.core.application.dto;

import java.math.BigDecimal;

public record ProductSummaryResponse(
    String productId,
    String name,
    BigDecimal price) {

  public ProductSummaryResponse {
    if (price == null) {
      price = BigDecimal.ZERO;
    }
  }
}
