package io.github.alexisTrejo11.drugstore.carts.product.core.domain.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductEvent {
  private String eventType; // e.g. "PRODUCT_CREATED", "PRODUCT_UPDATED", "PRODUCT_DELETED"
  private ProductPayload payload;

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class ProductPayload {
    private String id;
    private String name;
    private BigDecimal price; // maps to unitPrice in cart
    private String description;
    private boolean available;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // discountPerUnit is not sent by product‑service; we will default to zero.
  }
}
