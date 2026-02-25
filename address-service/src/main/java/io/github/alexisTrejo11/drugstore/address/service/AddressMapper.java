package io.github.alexisTrejo11.drugstore.address.service;

import org.springframework.stereotype.Component;

import io.github.alexisTrejo11.drugstore.address.entity.AddressEntity;
import io.github.alexisTrejo11.drugstore.address.utils.dto.AddressRequest;
import io.github.alexisTrejo11.drugstore.address.utils.dto.AddressSummary;

/**
 * Mapper for converting between Address entities and DTOs.
 * Centralizes all mapping logic in one place.
 */
@Component
public class AddressMapper {

  private static final int SUMMARY_STREET_MAX_LENGTH = 50;
  private static final String TRUNCATION_SUFFIX = "...";

  /**
   * Converts AddressEntity to AddressSummary DTO
   * Truncates street name if longer than 50 characters
   * 
   * @param entity the address entity
   * @return address summary DTO
   */
  public AddressSummary toSummary(AddressEntity entity) {
    String shortStreet = truncateStreet(entity.getStreet());

    return new AddressSummary(
        entity.getId() != null ? entity.getId().toString() : null,
        shortStreet,
        entity.getCity(),
        entity.getCountry(),
        entity.getIsDefault());
  }

  /**
   * Creates a new AddressEntity from request data
   * 
   * @param userId   the user ID
   * @param userType the user type (CUSTOMER or EMPLOYEE)
   * @param request  the address request
   * @return new address entity
   */
  public AddressEntity toEntity(String userId, AddressEntity.UserType userType, AddressRequest request) {
    return AddressEntity.builder()
        .userId(userId)
        .userType(userType)
        .street(request.street().trim())
        .city(request.city().trim())
        .state(request.state().trim())
        .country(request.country().toUpperCase())
        .postalCode(request.postalCode().trim())
        .additionalDetails(trimOrNull(request.additionalDetails()))
        .isDefault(request.isDefault() != null && request.isDefault())
        .active(true)
        .build();
  }

  /**
   * Updates an existing AddressEntity with request data
   * Does not update isDefault field (handled separately)
   * 
   * @param entity  the entity to update
   * @param request the address request with new data
   */
  public void updateEntity(AddressEntity entity, AddressRequest request) {
    entity.setStreet(request.street().trim());
    entity.setCity(request.city().trim());
    entity.setState(request.state().trim());
    entity.setCountry(request.country().toUpperCase());
    entity.setPostalCode(request.postalCode().trim());
    entity.setAdditionalDetails(trimOrNull(request.additionalDetails()));
    // Note: isDefault is managed separately in the service layer
  }

  /**
   * Truncates street name to max length for summary display
   * 
   * @param street the full street address
   * @return truncated street with "..." suffix if needed
   */
  private String truncateStreet(String street) {
    if (street == null) {
      return null;
    }

    if (street.length() > SUMMARY_STREET_MAX_LENGTH) {
      return street.substring(0, SUMMARY_STREET_MAX_LENGTH - TRUNCATION_SUFFIX.length())
          + TRUNCATION_SUFFIX;
    }

    return street;
  }

  /**
   * Trims a string or returns null if the string is null
   * 
   * @param value the string to trim
   * @return trimmed string or null
   */
  private String trimOrNull(String value) {
    return value != null ? value.trim() : null;
  }
}
