package io.github.alexisTrejo11.drugstore.employees.adapter.outbound.persistence.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Embeddable entity for Contact Information
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactInfoEmbeddable {

  private String email;
  private String phoneNumber;
  private String emergencyContact;
  private String emergencyPhone;
}
