package microservice.product_service.app.core.domain.model.valueobjects;

import java.util.Objects;

import microservice.product_service.app.core.domain.exception.ProductValueObjectException;
import microservice.product_service.app.core.domain.model.enums.ProductCategory;
import microservice.product_service.app.core.domain.model.enums.ProductSubcategory;
import microservice.product_service.app.core.domain.model.enums.ProductType;

/**
 * Hierarchical product classification: Type -> Category -> Subcategory
 *
 * Examples:
 * - MEDICATION -> DERMATOLOGY -> ANTI_ACNE
 * - UTENSIL -> MEDICAL_DEVICES -> MONITORING_DEVICES
 * - COSMETIC -> PERSONAL_CARE -> SKIN_CARE
 */
public record ProductClassification(
    ProductType type,
    ProductCategory category,
    ProductSubcategory subcategory) {

  public static final ProductClassification NOT_CLASSIFIED = new ProductClassification(
      ProductType.OTHER,
      ProductCategory.NOT_CATEGORIZED,
      ProductSubcategory.NOT_SPECIFIED);

  public ProductClassification {

  }

  public static ProductClassification create(ProductType type, ProductCategory category,
      ProductSubcategory subcategory) {
    Objects.requireNonNull(type, "ProductType cannot be null");
    Objects.requireNonNull(category, "ProductCategory cannot be null");
    Objects.requireNonNull(subcategory, "ProductSubcategory cannot be null");

    // Validate subcategory belongs to category
    if (subcategory != ProductSubcategory.NOT_SPECIFIED && !subcategory.belongsTo(category)) {
      throw new ProductValueObjectException("ProductClassification",
          String.format("Subcategory '%s' does not belong to category '%s'",
              subcategory.getDisplayName(), category.getDisplayName()));
    }
    return new ProductClassification(type, category, subcategory);
  }

  public static ProductClassification of(ProductType type, ProductCategory category) {
    return new ProductClassification(type, category, ProductSubcategory.NOT_SPECIFIED);
  }

  public static ProductClassification medication(ProductCategory category, ProductSubcategory subcategory) {
    return create(ProductType.MEDICATION, category, subcategory);
  }

  public static ProductClassification utensil(ProductCategory category, ProductSubcategory subcategory) {
    return create(ProductType.UTENSIL, category, subcategory);
  }

  public static ProductClassification cosmetic(ProductCategory category, ProductSubcategory subcategory) {
    return create(ProductType.COSMETIC, category, subcategory);
  }

  public String getFullClassificationPath() {
    String subName = (subcategory == null || subcategory == ProductSubcategory.NOT_SPECIFIED)
        ? ProductSubcategory.NOT_SPECIFIED.getDisplayName()
        : subcategory.getDisplayName();
    return String.format("%s > %s > %s",
        type.getDisplayName(),
        category.getDisplayName(),
        subName);
  }

  public String getShortPath() {
    return String.format("%s / %s",
        type.getDisplayName(),
        category.getDisplayName());
  }

  public boolean isMedication() {
    return type == ProductType.MEDICATION;
  }

  public boolean isUtensil() {
    return type == ProductType.UTENSIL;
  }

  public boolean isCosmetic() {
    return type == ProductType.COSMETIC;
  }

  public boolean hasSubcategory() {
    return subcategory != null && subcategory != ProductSubcategory.NOT_SPECIFIED;
  }

  public ProductType getType() {
    return type;
  }

  public ProductCategory getCategory() {
    return category;
  }

  public ProductSubcategory getSubcategory() {
    return subcategory;
  }

  public String getSkuPrefix() {
    return type.getSkuPrefix();
  }
}
