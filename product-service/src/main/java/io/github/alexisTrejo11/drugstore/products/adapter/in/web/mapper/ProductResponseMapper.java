package io.github.alexisTrejo11.drugstore.products.adapter.in.web.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import libs_kernel.page.PageResponse;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.Product;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.ExpirationRange;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.ProductClassification;
import io.github.alexisTrejo11.drugstore.products.adapter.in.web.dto.ProductResponse;

/**
 * Mapper responsible for transforming Product domain entities into
 * ProductResponse DTOs.
 * Handles null safety and complex object transformations for the presentation
 * layer.
 */
@Component
public class ProductResponseMapper {

  /**
   * Converts a Product domain entity to its corresponding API response
   * representation.
   *
   * @param product the domain entity to convert, may be null
   * @return the API response DTO, or null if input is null
   */
  public ProductResponse toResponse(Product product) {
    if (product == null) {
      return null;
    }

    return ProductResponse.builder()
        .status(product.getStatus())
        .barcode(product.getBarcode())
        .requiresPrescription(product.isRequiresPrescription())
        .id(extractIdValue(product))
        .sku(extractSkuValue(product))
        .images(extractImageUrls(product))
        .name(extractNameValue(product))
        .description(extractDescriptionValue(product))
        .activeIngredient(extractActiveIngredientValue(product))
        .manufacturer(extractManufacturerValue(product))
        .price(extractPriceValue(product))
        .dosage(extractDosageValue(product))
        .administration(extractAdministrationValue(product))
        .createdAt(extractCreatedTimestamp(product))
        .updatedAt(extractUpdatedTimestamp(product))
        .classification(mapProductClassification(product))
        .expirationRange(mapExpirationRange(product))
        .build();
  }

  /**
   * Converts a paginated list of Product entities to a paginated API response.
   *
   * @param page the paginated product entities from the data layer
   * @return paginated API response containing product DTOs, or null if input is
   *         null
   */
  public PageResponse<ProductResponse> toPageResponse(Page<Product> page) {
    if (page == null) {
      return null;
    }

    return PageResponse.from(page.map(this::toResponse));
  }

  private String extractIdValue(Product product) {
    return product.getId() != null ? product.getId().value().toString() : null;
  }

  private String extractSkuValue(Product product) {
    return product.getSku() != null ? product.getSku().value() : null;
  }

  private List<String> extractImageUrls(Product product) {
    return product.getImages() != null ? product.getImages().urls() : null;
  }

  private String extractNameValue(Product product) {
    return product.getName() != null ? product.getName().value() : null;
  }

  private String extractDescriptionValue(Product product) {
    return product.getDescription() != null ? product.getDescription().value() : null;
  }

  private String extractActiveIngredientValue(Product product) {
    return product.getActiveIngredient() != null ? product.getActiveIngredient().value() : null;
  }

  private String extractManufacturerValue(Product product) {
    return product.getManufacturer() != null ? product.getManufacturer().value() : null;
  }

  private BigDecimal extractPriceValue(Product product) {
    return product.getPrice() != null ? product.getPrice().value() : null;
  }

  private String extractDosageValue(Product product) {
    return product.getDosage() != null ? product.getDosage().value() : null;
  }

  private String extractAdministrationValue(Product product) {
    return product.getAdministration() != null ? product.getAdministration().value() : null;
  }

  private java.time.LocalDateTime extractCreatedTimestamp(Product product) {
    return product.getTimeStamps() != null ? product.getTimeStamps().getCreatedAt() : null;
  }

  private java.time.LocalDateTime extractUpdatedTimestamp(Product product) {
    return product.getTimeStamps() != null ? product.getTimeStamps().getUpdatedAt() : null;
  }

  private ProductResponse.ProductClassificationResponse mapProductClassification(Product product) {
    ProductClassification classification = product.getClassification();
    if (classification == null) {
      return null;
    }
    return mapToClassificationResponse(classification);
  }

  private ProductResponse.ExpirationRangeResponse mapExpirationRange(Product product) {
    ExpirationRange expirationRange = product.getExpirationRange();
    if (expirationRange == null) {
      return null;
    }
    return mapToExpirationRangeResponse(expirationRange);
  }

  /**
   * Maps a ProductClassification domain object to its API response
   * representation.
   */
  private ProductResponse.ProductClassificationResponse mapToClassificationResponse(
      ProductClassification classification) {

    String typeName = classification.type() != null ? classification.type().name() : null;
    String categoryName = classification.category() != null ? classification.category().name() : null;
    String subcategoryName = classification.subcategory() != null ? classification.subcategory().name() : null;

    return new ProductResponse.ProductClassificationResponse(typeName, categoryName, subcategoryName);
  }

  /**
   * Maps an ExpirationRange domain object to its API response representation.
   */
  private ProductResponse.ExpirationRangeResponse mapToExpirationRangeResponse(
      ExpirationRange expirationRange) {

    return new ProductResponse.ExpirationRangeResponse(
        expirationRange.minMonths(),
        expirationRange.maxMonths());
  }
}
