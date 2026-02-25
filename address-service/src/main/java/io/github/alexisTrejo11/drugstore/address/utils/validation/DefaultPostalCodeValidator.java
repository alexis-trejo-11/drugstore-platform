package io.github.alexisTrejo11.drugstore.address.utils.validation;

/**
 * Default postal code validator for countries without specific validation
 * rules.
 * Accepts any non-empty postal code.
 */
public class DefaultPostalCodeValidator implements PostalCodeValidator {

  @Override
  public boolean isValid(String postalCode) {
    return postalCode != null && !postalCode.trim().isEmpty();
  }

  @Override
  public String getCountryCode() {
    return "DEFAULT";
  }

  @Override
  public String getFormatDescription() {
    return "Any valid postal code format";
  }
}
