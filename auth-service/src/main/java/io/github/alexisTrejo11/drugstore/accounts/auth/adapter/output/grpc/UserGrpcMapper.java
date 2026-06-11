package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.output.grpc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.microservices.grpc.user.UserResponse;
import org.springframework.stereotype.Component;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.models.User;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.Email;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.PhoneNumber;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.UserId;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.UserRole;

@Component
public class UserGrpcMapper {
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

  public User toDomain(UserResponse grpcUser) {
    if (grpcUser == null) {
      return null;
    }

    try {
      Email email = new Email(grpcUser.getEmail());
      PhoneNumber phone = toPhone(grpcUser.getPhoneNumber());
      UserRole role = UserRole.valueOf(grpcUser.getRole().trim().toUpperCase());

      return User.builder()
          .id(new UserId(grpcUser.getUserId()))
          .email(email)
          .firstName(grpcUser.getFirstName())
          .lastName(grpcUser.getLastName())
          .phoneNumber(phone)
          .password("")
          .role(role)
          .status(mapStatus(grpcUser.getStatus()))
          .twoFactorEnabled(grpcUser.getTwoFactorEnabled())
          .createdAt(parseDateTime(grpcUser.getCreatedAt()))
          .build();
    } catch (Exception e) {
      throw new IllegalStateException("Failed to map UserResponse to domain User", e);
    }
  }

  private PhoneNumber toPhone(String raw) {
    if (raw == null || raw.isBlank()) {
      return new PhoneNumber("+0000000000");
    }
    String digits = raw.replaceAll("[^0-9+]", "");
    if (digits.isEmpty()) {
      return new PhoneNumber("+0000000000");
    }
    if (!digits.startsWith("+")) {
      digits = "+" + digits;
    }
    return new PhoneNumber(digits);
  }

  private User.UserStatus mapStatus(String status) {
    if (status == null || status.isBlank()) {
      return User.UserStatus.INACTIVE;
    }
    return switch (status.trim().toUpperCase()) {
      case "ACTIVE" -> User.UserStatus.ACTIVE;
      case "PENDING" -> User.UserStatus.PENDING_ACTIVATION;
      case "INACTIVE", "DELETED" -> User.UserStatus.INACTIVE;
      case "SUSPENDED" -> User.UserStatus.BANNED;
      default -> User.UserStatus.INACTIVE;
    };
  }

  private LocalDateTime parseDateTime(String dateTimeStr) {
    if (dateTimeStr == null || dateTimeStr.isEmpty()) {
      return null;
    }
    try {
      return LocalDateTime.parse(dateTimeStr, DATE_FORMATTER);
    } catch (Exception e) {
      return null;
    }
  }

  public String formatDateTime(LocalDateTime dateTime) {
    if (dateTime == null) {
      return "";
    }
    return dateTime.format(DATE_FORMATTER);
  }
}
