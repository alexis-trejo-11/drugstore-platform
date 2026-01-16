package microservice.product_service.app.domain.model.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Top-level product classification (General Type).
 * Hierarchy: ProductType -> ProductCategory -> ProductSubcategory
 *
 * Examples:
 * - MEDICATION -> DERMATOLOGY -> "Anti-acne cream"
 * - UTENSIL -> MEDICAL_DEVICES -> "Blood pressure monitor"
 * - COSMETIC -> PERSONAL_CARE -> "Moisturizer"
 */
public enum ProductType {
  MEDICATION("Medication", "MED"),
  UTENSIL("Utensil", "UTL"),
  COSMETIC("Cosmetic", "COS"),
  SUPPLEMENT("Supplement", "SUP"),
  HYGIENE("Hygiene", "HYG"),
  EQUIPMENT("Equipment", "EQP"),
  OTHER("Other", "OTH");

  private final String displayName;
  private final String skuPrefix;

  ProductType(String displayName, String skuPrefix) {
    this.displayName = displayName;
    this.skuPrefix = skuPrefix;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getSkuPrefix() {
    return skuPrefix;
  }

  public static List<String> getAllDisplayNames() {
    return Arrays.stream(ProductType.values())
        .map(ProductType::getDisplayName)
        .toList();
  }

  public static ProductType fromDisplayName(String displayName) {
    return Arrays.stream(ProductType.values())
        .filter(t -> t.displayName.equalsIgnoreCase(displayName))
        .findFirst()
        .orElse(OTHER);
  }

  public static ProductType fromSkuPrefix(String prefix) {
    return Arrays.stream(ProductType.values())
        .filter(t -> t.skuPrefix.equalsIgnoreCase(prefix))
        .findFirst()
        .orElse(OTHER);
  }
}
