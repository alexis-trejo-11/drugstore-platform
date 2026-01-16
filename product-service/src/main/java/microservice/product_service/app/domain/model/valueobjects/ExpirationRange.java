package microservice.product_service.app.domain.model.valueobjects;

import microservice.product_service.app.domain.exception.ProductValueObjectException;

/**
 * Represents the approximate expiration time range for a product.
 * This is catalog data (not stock-specific); actual expiration dates
 * are tracked in inventory-service per batch/lot.
 *
 * Examples: "6 months", "1 year", "24 months"
 */
public record ExpirationRange(int minMonths, int maxMonths) {

  public static final ExpirationRange NOT_SPECIFIED = new ExpirationRange(0, 0);

  public ExpirationRange {
    if (minMonths < 0 || maxMonths < 0) {
      throw new ProductValueObjectException("ExpirationRange", "Months cannot be negative");
    }
    if (maxMonths < minMonths) {
      throw new ProductValueObjectException("ExpirationRange", "Max months must be >= min months");
    }
  }

  public static ExpirationRange create(int minMonths, int maxMonths) {
    return new ExpirationRange(minMonths, maxMonths);
  }

  public static ExpirationRange ofMonths(int months) {
    return new ExpirationRange(months, months);
  }

  public static ExpirationRange ofYears(int years) {
    int months = years * 12;
    return new ExpirationRange(months, months);
  }

  public static ExpirationRange between(int minMonths, int maxMonths) {
    return new ExpirationRange(minMonths, maxMonths);
  }

  public boolean isSpecified() {
    return minMonths > 0 || maxMonths > 0;
  }

  public String toDisplayString() {
    if (!isSpecified()) {
      return "Not specified";
    }
    if (minMonths == maxMonths) {
      return formatMonths(minMonths);
    }
    return formatMonths(minMonths) + " - " + formatMonths(maxMonths);
  }

  private String formatMonths(int months) {
    if (months >= 12 && months % 12 == 0) {
      int years = months / 12;
      return years + (years == 1 ? " year" : " years");
    }
    return months + (months == 1 ? " month" : " months");
  }

  public int getMinMonths() {
    return minMonths;
  }

  public int getMaxMonths() {
    return maxMonths;
  }
}
