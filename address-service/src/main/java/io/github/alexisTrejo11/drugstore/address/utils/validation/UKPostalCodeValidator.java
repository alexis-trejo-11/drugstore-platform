package io.github.alexisTrejo11.drugstore.address.utils.validation;

import org.springframework.stereotype.Component;

/**
 * UK postal code validator
 * Format: Various UK postcode formats (e.g., SW1A 1AA, EC1A 1BB)
 */
@Component
public class UKPostalCodeValidator implements PostalCodeValidator {

  private static final String UK_POSTAL_CODE_REGEX = "^[A-Z]{1,2}\\d{1,2}[A-Z]? \\d[A-Z]{2}$";

  @Override
  public boolean isValid(String postalCode) {
    return postalCode != null && postalCode.toUpperCase().matches(UK_POSTAL_CODE_REGEX);
  }

  @Override
  public String getCountryCode() {
    return "UK";
  }

  @Override
  public String getFormatDescription() {
    return "UK postcode format (e.g., SW1A 1AA, EC1A 1BB)";
  }
}
