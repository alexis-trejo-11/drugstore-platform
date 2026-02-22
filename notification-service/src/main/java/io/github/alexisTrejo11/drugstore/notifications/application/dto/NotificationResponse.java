package io.github.alexisTrejo11.drugstore.notifications.application.dto;

import java.time.LocalDateTime;
import java.util.Map;

import io.github.alexisTrejo11.drugstore.notifications.domain.model.Notification;

public record NotificationResponse(
    String id,
    String type,
    String correlationId,
    String eventId,
    String channel,
    String priority,
    String status,
    RecipientInfoDto recipient,
    String subject,
    String content,
    String templateId,
    Map<String, Object> metadata,
    Integer maxRetries,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime scheduledAt,
    LocalDateTime sentAt,
    Integer retryCount,
    String errorMessage) {

  public static NotificationResponse fromDomain(Notification notification) {
    if (notification == null) {
      return null;
    }

    return new NotificationResponse(
        notification.getId() != null ? notification.getId().value() : null,
        notification.getType() != null ? notification.getType().name() : null,
        notification.getCorrelationId(),
        notification.getEventId(),
        notification.getChannel() != null ? notification.getChannel().name() : null,
        notification.getPriority() != null ? notification.getPriority().name() : null,
        notification.getStatus() != null ? notification.getStatus().name() : null,
        RecipientInfoDto.fromDomain(notification.getRecipient()),
        notification.getSubject(),
        notification.getContent(),
        notification.getTemplateId(),
        notification.getMetadata(),
        notification.getMaxRetries(),
        notification.getCreatedAt(),
        notification.getUpdatedAt(),
        notification.getScheduledAt(),
        notification.getSentAt(),
        notification.getRetryCount(),
        notification.getErrorMessage());
  }

  public record RecipientInfoDto(
      String userId,
      String email,
      String phoneNumber,
      String firstName,
      String lastName,
      String language,
      String timezone) {

    public static RecipientInfoDto fromDomain(
        io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.RecipientInfo recipient) {
      if (recipient == null) {
        return null;
      }

      return new RecipientInfoDto(
          recipient.userId(),
          recipient.email(),
          recipient.phoneNumber(),
          recipient.firstName(),
          recipient.lastName(),
          recipient.language(),
          recipient.timezone());
    }
  }
}
