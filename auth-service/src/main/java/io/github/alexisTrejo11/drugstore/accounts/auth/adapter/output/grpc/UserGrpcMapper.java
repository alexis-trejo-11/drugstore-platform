package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.output.grpc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.microservices.grpc.user.UserResponse;
import org.springframework.stereotype.Component;

import io.github.alexisTrejo11.drugstore.accounts.auth.User;

@Component
public class UserGrpcMapper {
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

  public User toDomain(UserResponse grpcUser) {
    if (grpcUser == null) {
      return null;
    }

    /*
     * return User.builder()
     * .id(grpcUser.getUserId())
     * .email(grpcUser.getEmail())
     * .firstName(grpcUser.getFirstName())
     * .lastName(grpcUser.getLastName())
     * .phoneNumber(grpcUser.getPhoneNumber())
     * .role(grpcUser.getRole())
     * .twoFactorEnabled(grpcUser.getTwoFactorEnabled())
     * .emailVerified(grpcUser.getEmailVerified())
     * .status(grpcUser.getStatus())
     * .createdAt(parseDateTime(grpcUser.getCreatedAt()))
     * .updatedAt(parseDateTime(grpcUser.getUpdatedAt()))
     * .build();
     * 
     */

    return null;
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
