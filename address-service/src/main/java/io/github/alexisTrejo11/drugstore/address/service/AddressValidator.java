package io.github.alexisTrejo11.drugstore.address.service;

import org.springframework.stereotype.Component;

import io.github.alexisTrejo11.drugstore.address.utils.dto.AddressRequest;
import io.github.alexisTrejo11.drugstore.address.utils.exceptions.InvalidAddressException;
import io.github.alexisTrejo11.drugstore.address.utils.validation.PostalCodeValidator;
import io.github.alexisTrejo11.drugstore.address.utils.validation.PostalCodeValidatorFactory;
import lombok.RequiredArgsConstructor;

/**
 * Validator for address-related data.
 * Separates validation logic from business logic.
 */
@Component
@RequiredArgsConstructor
public class AddressValidator {

  private final PostalCodeValidatorFactory postalCodeValidatorFactory;

  /**
   * Validates an address request
   * 
   * @param request the address request to validate
   * @throws InvalidAddressException if validation fails
   */
  public void validate(AddressRequest request) {
    validateRequiredFields(request);
    validatePostalCode(request.country(), request.postalCode());
  }

  /**
   * Validates that all required fields are present and not empty
   */
  private void validateRequiredFields(AddressRequest request) {
    if (isNullOrBlank(request.street())) {
      throw new InvalidAddressException("Street is required");
    }
    if (isNullOrBlank(request.city())) {
      throw new InvalidAddressException("City is required");
    }
    if (isNullOrBlank(request.state())) {
      throw new InvalidAddressException("State is required");
    }
    if (isNullOrBlank(request.country())) {
      throw new InvalidAddressException("Country is required");
    }
    if (isNullOrBlank(request.postalCode())) {
      throw new InvalidAddressException("Postal code is required");
    }
  }

  /**
   * Validates postal code format based on country
   */
  private void validatePostalCode(String country, String postalCode) {
    PostalCodeValidator validator = postalCodeValidatorFactory.getValidator(country);

    if (!validator.isValid(postalCode)) {
      throw new InvalidAddressException(
          String.format("Invalid postal code format for country %s. Expected format: %s",
              country,
              validator.getFormatDescription()));
    }
  }

  /**
   * Checks if a string is null or blank (empty or whitespace only)
   */
  private boolean isNullOrBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
}
