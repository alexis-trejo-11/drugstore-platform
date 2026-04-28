package io.github.alexisTrejo11.drugstore.address.utils.validation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * Factory for managing postal code validators by country
 */
@Component
public class PostalCodeValidatorFactory {

  private final Map<String, PostalCodeValidator> validators;

  public PostalCodeValidatorFactory(List<PostalCodeValidator> validatorList) {
    this.validators = new HashMap<>();
    validatorList.forEach(validator -> validators.put(validator.getCountryCode().toUpperCase(), validator));
  }

  /**
   * Gets validator for a specific country
   * 
   * @param countryCode ISO 3166-1 alpha-2 country code
   * @return validator or default validator if country not supported
   */
  public PostalCodeValidator getValidator(String countryCode) {
    return validators.getOrDefault(countryCode.toUpperCase(), new DefaultPostalCodeValidator());
  }

  /**
   * Checks if a country has specific validation rules
   * 
   * @param countryCode ISO 3166-1 alpha-2 country code
   * @return true if specific validator exists
   */
  public boolean hasValidator(String countryCode) {
    return validators.containsKey(countryCode.toUpperCase());
  }
}
