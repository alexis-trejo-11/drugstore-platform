package io.github.alexisTrejo11.drugstore.employees.adapter.outbound.persistence.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Embeddable entity for Address
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressEmbeddable {

  private String street;
  private String city;
  private String state;
  private String postalCode;
  private String country;
}
