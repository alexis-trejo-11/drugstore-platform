package io.github.alexisTrejo11.drugstore.address.utils.validation;

import org.springframework.stereotype.Component;

/**
 * Canada postal code validator
 * Format: A1A 1A1 (letter-digit-letter space digit-letter-digit)
 */
@Component
public class CAPostalCodeValidator implements PostalCodeValidator {

  private static final String CA_POSTAL_CODE_REGEX = "^[A-Z]\\d[A-Z] \\d[A-Z]\\d$";

  @Override
  public boolean isValid(String postalCode) {
    return postalCode != null && postalCode.toUpperCase().matches(CA_POSTAL_CODE_REGEX);
  }

  @Override
  public String getCountryCode() {
    return "CA";
  }

  @Override
  public String getFormatDescription() {
    return "Letter-digit-letter space digit-letter-digit (e.g., K1A 0B1)";
  }
}
