package microservice.product_service.app.domain.model.valueobjects;

import java.util.UUID;

import microservice.product_service.app.domain.exception.ProductValueObjectException;

/**
 * Stock Keeping Unit - Global unique identifier for a product in the catalog.
 * Format: PREFIX-XXXXXXXX (e.g., MED-12345678, UTL-87654321)
 *
 * The SKU serves as the primary business identifier across all services.
 */
public record SKU(String value) {

  public static final SKU NONE = new SKU("");
  private static final String SKU_PATTERN = "^[A-Z]{2,5}-[A-Z0-9]{6,12}$";

  public SKU {
    if (value == null) {
      value = "";
    }
  }

  public static SKU create(String value) {
    if (value == null || value.trim().isEmpty()) {
      throw new ProductValueObjectException("SKU", "SKU cannot be null or empty");
    }

    String normalized = value.trim().toUpperCase();

    if (!normalized.matches(SKU_PATTERN)) {
      throw new ProductValueObjectException("SKU",
          "Invalid SKU format. Expected: PREFIX-CODE (e.g., MED-12345678). Got: " + value);
    }

    return new SKU(normalized);
  }

  public static SKU generate(String prefix) {
    if (prefix == null || prefix.trim().isEmpty()) {
      prefix = "PRD";
    }
    String normalizedPrefix = prefix.trim().toUpperCase();
    if (normalizedPrefix.length() < 2 || normalizedPrefix.length() > 5) {
      throw new ProductValueObjectException("SKU", "SKU prefix must be 2-5 characters");
    }

    String uniquePart = UUID.randomUUID().toString()
        .replace("-", "")
        .substring(0, 8)
        .toUpperCase();

    return new SKU(normalizedPrefix + "-" + uniquePart);
  }

  public static SKU generateForMedication() {
    return generate("MED");
  }

  public static SKU generateForUtensil() {
    return generate("UTL");
  }

  public static SKU generateForCosmetic() {
    return generate("COS");
  }

  public static SKU generateForGeneral() {
    return generate("GEN");
  }

  public String getPrefix() {
    if (value.contains("-")) {
      return value.substring(0, value.indexOf("-"));
    }
    return "";
  }

  public String getCode() {
    if (value.contains("-")) {
      return value.substring(value.indexOf("-") + 1);
    }
    return value;
  }

  public boolean isEmpty() {
    return value == null || value.isEmpty();
  }

  public String getValue() {
    return value;
  }
}
