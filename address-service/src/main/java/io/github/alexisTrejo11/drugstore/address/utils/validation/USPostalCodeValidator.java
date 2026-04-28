package io.github.alexisTrejo11.drugstore.address.utils.validation;

import org.springframework.stereotype.Component;

/**
 * US postal code validator (ZIP codes)
 * Formats: 12345 or 12345-6789
 */
@Component
public class USPostalCodeValidator implements PostalCodeValidator {

  private static final String US_ZIP_REGEX = "^\\d{5}(-\\d{4})?$";

  @Override
  public boolean isValid(String postalCode) {
    return postalCode != null && postalCode.matches(US_ZIP_REGEX);
  }

  @Override
  public String getCountryCode() {
    return "US";
  }

  @Override
  public String getFormatDescription() {
    return "5 digits (e.g., 12345) or 5 digits + 4 digits (e.g., 12345-6789)";
  }
}
