package io.github.alexisTrejo11.drugstore.address.utils.validation;

/**
 * Strategy interface for country-specific postal code validation
 */
public interface PostalCodeValidator {

  /**
   * Validates a postal code according to country-specific rules
   * 
   * @param postalCode the postal code to validate
   * @return true if valid, false otherwise
   */
  boolean isValid(String postalCode);

  /**
   * Gets the country code this validator handles
   * 
   * @return ISO 3166-1 alpha-2 country code
   */
  String getCountryCode();

  /**
   * Gets a human-readable description of the postal code format
   * 
   * @return format description
   */
  String getFormatDescription();
}
