package io.github.alexisTrejo11.drugstore.users.user.core.domain.events;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Domain event triggered when a user is updated
 *
 * This event contains comprehensive metadata for tracking, auditing,
 * and processing user update events across the system.
 * Can be used for updates to email, phone number, last login, or other user
 * fields.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateEvent extends DomainEvent {
  private String correlationId; // For request tracking across services

  // User Data
  private String userId;
  private String email;
  private String firstName;
  private String lastName;
  private String phoneNumber;
  private String role;
  private String status;
  private Boolean twoFactorEnabled;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime lastLoginAt;

  // Additional Information
  private String source;
  private String ipAddress;
  private String userAgent;
  private String updateType; // e.g., "PROFILE_UPDATE", "LAST_LOGIN_UPDATE", "STATUS_UPDATE"

  public UserUpdateEvent(String userId, String email, String phoneNumber, LocalDateTime updatedAt) {
    super();
    this.userId = userId;
    this.email = email;
    this.phoneNumber = phoneNumber;
    this.updatedAt = updatedAt;
  }
}
