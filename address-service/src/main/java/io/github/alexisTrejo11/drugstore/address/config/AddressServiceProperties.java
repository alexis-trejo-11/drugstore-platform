package io.github.alexisTrejo11.drugstore.address.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * Configuration properties for address service business rules.
 * These can be overridden in application.yml/bootstrap.yml
 */
@Component
@ConfigurationProperties(prefix = "address.service")
@Data
public class AddressServiceProperties {

  /**
   * Maximum number of addresses allowed per customer
   */
  private int customerMaxAddresses = 5;

  /**
   * Maximum number of addresses allowed per employee
   */
  private int employeeMaxAddresses = 1;

  /**
   * Get address limit based on user type
   */
  public int getAddressLimit(String userType) {
    return "CUSTOMER".equalsIgnoreCase(userType) ? customerMaxAddresses : employeeMaxAddresses;
  }
}
