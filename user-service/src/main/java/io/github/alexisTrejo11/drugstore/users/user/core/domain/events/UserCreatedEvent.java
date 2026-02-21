package io.github.alexisTrejo11.drugstore.users.user.core.domain.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Domain event triggered when a new user is created
 *
 * This event contains comprehensive metadata for tracking, auditing,
 * and processing user creation events across the system.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreatedEvent extends DomainEvent {
  private String correlationId; // For request tracking across services

  // User Data
  private String userId;
  private String email;
  private String firstName;
  private String lastName;
  private String phoneNumber;
  private String hashedPassword;
  private String role;
  private Boolean twoFactorEnabled;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  // Additional Information
  private String source;
  private String ipAddress;
  private String userAgent;


  public UserCreatedEvent(String userId, String email, String phoneNumber) {
    super();
    this.userId = userId;
    this.email = email;
    this.phoneNumber = phoneNumber;
  }
}
