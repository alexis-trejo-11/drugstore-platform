package io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject;

import java.io.Serializable;
import java.util.Objects;

/**
 * Value object representing a physical address
 */
public class Address implements Serializable {

  private final String street;
  private final String city;
  private final String state;
  private final String postalCode;
  private final String country;

  private Address(String street, String city, String state, String postalCode, String country) {
    this.street = street;
    this.city = city;
    this.state = state;
    this.postalCode = postalCode;
    this.country = country;
  }

  public static Address of(String street, String city, String state, String postalCode, String country) {
    if (street == null || street.trim().isEmpty()) {
      throw new IllegalArgumentException("Street cannot be null or empty");
    }
    if (city == null || city.trim().isEmpty()) {
      throw new IllegalArgumentException("City cannot be null or empty");
    }
    if (state == null || state.trim().isEmpty()) {
      throw new IllegalArgumentException("State cannot be null or empty");
    }
    if (postalCode == null || postalCode.trim().isEmpty()) {
      throw new IllegalArgumentException("Postal code cannot be null or empty");
    }
    if (country == null || country.trim().isEmpty()) {
      throw new IllegalArgumentException("Country cannot be null or empty");
    }

    return new Address(street, city, state, postalCode, country);
  }

  public String getStreet() {
    return street;
  }

  public String getCity() {
    return city;
  }

  public String getState() {
    return state;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public String getCountry() {
    return country;
  }

  public String getFullAddress() {
    return String.format("%s, %s, %s %s, %s", street, city, state, postalCode, country);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Address address = (Address) o;
    return Objects.equals(street, address.street) &&
        Objects.equals(city, address.city) &&
        Objects.equals(state, address.state) &&
        Objects.equals(postalCode, address.postalCode) &&
        Objects.equals(country, address.country);
  }

  @Override
  public int hashCode() {
    return Objects.hash(street, city, state, postalCode, country);
  }

  @Override
  public String toString() {
    return getFullAddress();
  }
}
