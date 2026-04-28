package io.github.alexisTrejo11.drugstore.users.user.adapter.output.persistence.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.enums.Gender;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "profiles")
public class ProfileModel {

  @Id
  @Column(name = "id", nullable = false, updatable = false, length = 36)
  private String id;

  @Column(name = "user_id", nullable = false, unique = true)
  private String userId;

  @Column(name = "first_name", length = 100)
  private String firstName;

  @Column(name = "last_name", length = 100)
  private String lastName;

  @Column(name = "date_of_birth")
  private LocalDate dateOfBirth;

  @Column(name = "bio", length = 500)
  private String bio;

  @Column(name = "profile_picture_url")
  private String profilePictureUrl;

  @Enumerated(EnumType.STRING)
  @Column(name = "gender")
  private Gender gender;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @OneToOne(mappedBy = "profile", fetch = FetchType.LAZY)
  private UserModel user;

  // Automatically set the creation timestamp
  @PrePersist
  protected void onCreate() {
    if (id == null) {
      id = UUID.randomUUID().toString();
    }
    if (createdAt == null) {
      createdAt = LocalDateTime.now();
    }
    if (updatedAt == null) {
      updatedAt = LocalDateTime.now();
    }
  }

  // Automatically update the modification timestamp
  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
