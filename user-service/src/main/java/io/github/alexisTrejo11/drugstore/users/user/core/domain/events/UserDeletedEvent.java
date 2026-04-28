package io.github.alexisTrejo11.drugstore.users.user.core.domain.events;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Domain event triggered when a user is deleted
 *
 * This event contains comprehensive metadata for tracking, auditing,
 * and processing user deletion events across the system.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDeletedEvent extends DomainEvent {
  private String correlationId; // For request tracking across services

  // User Data
  private String userId;
  private String email;
  private String phoneNumber;
  private String role;
  private String deletionReason;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime deletedAt;

  // Additional Information
  private String source;
  private String ipAddress;
  private String userAgent;
  private String deletedBy; // ID of the user/admin who performed the deletion

  public UserDeletedEvent(String userId, String email, LocalDateTime deletedAt) {
    super();
    this.userId = userId;
    this.email = email;
    this.deletedAt = deletedAt;
  }
}
