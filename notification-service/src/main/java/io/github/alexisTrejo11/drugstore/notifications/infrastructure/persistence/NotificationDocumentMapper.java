package io.github.alexisTrejo11.drugstore.notifications.infrastructure.persistence;

import io.github.alexisTrejo11.drugstore.notifications.domain.model.Notification;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.*;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Notification domain model and
 * NotificationDocument
 */
@Component
public class NotificationDocumentMapper {

  public NotificationDocument toDocument(Notification notification) {
    if (notification == null) {
      return null;
    }

    NotificationDocument document = new NotificationDocument();
    document.setId(notification.getId() != null ? notification.getId().value() : null);
    document.setType(notification.getType() != null ? notification.getType().name() : null);
    document.setCorrelationId(notification.getCorrelationId());
    document.setEventId(notification.getEventId());
    document.setChannel(notification.getChannel() != null ? notification.getChannel().name() : null);
    document.setPriority(notification.getPriority() != null ? notification.getPriority().name() : null);
    document.setStatus(notification.getStatus() != null ? notification.getStatus().name() : null);
    document.setUserId(notification.getRecipient() != null ? notification.getRecipient().userId() : null);
    document.setRecipient(toRecipientDocument(notification.getRecipient()));
    document.setSubject(notification.getSubject());
    document.setContent(notification.getContent());
    document.setTemplateId(notification.getTemplateId());
    document.setMetadata(notification.getMetadata());
    document.setMaxRetries(notification.getMaxRetries());
    document.setCreatedAt(notification.getCreatedAt());
    document.setUpdatedAt(notification.getUpdatedAt());
    document.setScheduledAt(notification.getScheduledAt());
    document.setSentAt(notification.getSentAt());
    document.setRetryCount(notification.getRetryCount());
    document.setErrorMessage(notification.getErrorMessage());

    return document;
  }

  public Notification toDomain(NotificationDocument document) {
    if (document == null) {
      return null;
    }

    Notification notification = new Notification();
    notification.setId(document.getId() != null ? NotificationId.from(document.getId()) : null);
    notification.setType(document.getType() != null ? NotificationType.valueOf(document.getType()) : null);
    notification.setCorrelationId(document.getCorrelationId());
    notification.setEventId(document.getEventId());
    notification.setChannel(document.getChannel() != null ? NotificationChannel.valueOf(document.getChannel()) : null);
    notification
        .setPriority(document.getPriority() != null ? NotificationPriority.valueOf(document.getPriority()) : null);
    notification.setStatus(document.getStatus() != null ? NotificationStatus.valueOf(document.getStatus()) : null);
    notification.setRecipient(toRecipientInfo(document.getRecipient()));
    notification.setSubject(document.getSubject());
    notification.setContent(document.getContent());
    notification.setTemplateId(document.getTemplateId());
    notification.setMetadata(document.getMetadata());
    notification.setMaxRetries(document.getMaxRetries());
    notification.setCreatedAt(document.getCreatedAt());
    notification.setUpdatedAt(document.getUpdatedAt());
    notification.setScheduledAt(document.getScheduledAt());
    notification.setSentAt(document.getSentAt());
    notification.setRetryCount(document.getRetryCount());
    notification.setErrorMessage(document.getErrorMessage());

    return notification;
  }

  private NotificationDocument.RecipientDocument toRecipientDocument(RecipientInfo recipientInfo) {
    if (recipientInfo == null) {
      return null;
    }

    return new NotificationDocument.RecipientDocument(
        recipientInfo.userId(),
        recipientInfo.email(),
        recipientInfo.phoneNumber(),
        recipientInfo.firstName(),
        recipientInfo.lastName(),
        recipientInfo.language(),
        recipientInfo.timezone());
  }

  private RecipientInfo toRecipientInfo(NotificationDocument.RecipientDocument recipientDocument) {
    if (recipientDocument == null) {
      return null;
    }

    return new RecipientInfo(
        recipientDocument.getUserId(),
        recipientDocument.getEmail(),
        recipientDocument.getPhoneNumber(),
        recipientDocument.getFirstName(),
        recipientDocument.getLastName(),
        recipientDocument.getLanguage(),
        recipientDocument.getTimezone());
  }
}
