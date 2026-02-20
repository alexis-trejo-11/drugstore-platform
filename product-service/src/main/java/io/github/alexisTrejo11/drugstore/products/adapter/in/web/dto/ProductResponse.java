package io.github.alexisTrejo11.drugstore.products.adapter.in.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.enums.ProductStatus;

@Builder
public record ProductResponse(
    String id,
    String sku,
    String barcode,
    Boolean requiresPrescription,
    ProductStatus status,
    String name,
    String description,
    String activeIngredient,
    String manufacturer,
    BigDecimal price,
    String dosage,
    String administration,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    ProductClassificationResponse classification,
    ExpirationRangeResponse expirationRange,
    List<String> images) {

  public record ProductClassificationResponse(
      String type,
      String category,
      String subcategory) {
  }

  public record ExpirationRangeResponse(
      Integer minMonths,
      Integer maxMonths) {
  }

}
