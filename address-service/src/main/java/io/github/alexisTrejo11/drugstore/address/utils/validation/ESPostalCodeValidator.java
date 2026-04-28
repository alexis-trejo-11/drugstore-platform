package io.github.alexisTrejo11.drugstore.address.utils.validation;

import org.springframework.stereotype.Component;

/**
 * Spain postal code validator
 * Format: 5 digits (e.g., 28001)
 */
@Component
public class ESPostalCodeValidator implements PostalCodeValidator {

  private static final String ES_POSTAL_CODE_REGEX = "^\\d{5}$";

  @Override
  public boolean isValid(String postalCode) {
    return postalCode != null && postalCode.matches(ES_POSTAL_CODE_REGEX);
  }

  @Override
  public String getCountryCode() {
    return "ES";
  }

  @Override
  public String getFormatDescription() {
    return "5 digits (e.g., 28001)";
  }
}
