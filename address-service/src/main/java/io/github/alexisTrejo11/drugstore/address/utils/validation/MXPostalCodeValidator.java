package io.github.alexisTrejo11.drugstore.address.utils.validation;

import org.springframework.stereotype.Component;

/**
 * Mexico postal code validator
 * Format: 5 digits (e.g., 03100)
 */
@Component
public class MXPostalCodeValidator implements PostalCodeValidator {

  private static final String MX_POSTAL_CODE_REGEX = "^\\d{5}$";

  @Override
  public boolean isValid(String postalCode) {
    return postalCode != null && postalCode.matches(MX_POSTAL_CODE_REGEX);
  }

  @Override
  public String getCountryCode() {
    return "MX";
  }

  @Override
  public String getFormatDescription() {
    return "5 digits (e.g., 03100)";
  }
}
