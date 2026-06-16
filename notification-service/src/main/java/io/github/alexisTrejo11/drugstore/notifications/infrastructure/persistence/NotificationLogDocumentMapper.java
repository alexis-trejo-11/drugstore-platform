package io.github.alexisTrejo11.drugstore.notifications.infrastructure.persistence;

import org.springframework.stereotype.Component;

import io.github.alexisTrejo11.drugstore.notifications.domain.model.NotificationLog;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationId;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationStatus;

/**
 * Mapper between NotificationLog domain model and NotificationLogDocument.
 */
@Component
public class NotificationLogDocumentMapper {

  public NotificationLogDocument toDocument(NotificationLog log) {
    if (log == null) {
      return null;
    }

    NotificationLogDocument document = new NotificationLogDocument();
    document.setId(log.getId());
    document.setNotificationId(log.getNotificationId() != null ? log.getNotificationId().value() : null);
    document.setStatus(log.getStatus() != null ? log.getStatus().name() : null);
    document.setMessage(log.getMessage());
    document.setDetails(log.getDetails());
    document.setTimestamp(log.getTimestamp());
    return document;
  }

  public NotificationLog toDomain(NotificationLogDocument document) {
    if (document == null) {
      return null;
    }

    NotificationLog log = new NotificationLog();
    log.setId(document.getId());
    log.setNotificationId(
        document.getNotificationId() != null ? NotificationId.from(document.getNotificationId()) : null);
    log.setStatus(document.getStatus() != null ? NotificationStatus.valueOf(document.getStatus()) : null);
    log.setMessage(document.getMessage());
    log.setDetails(document.getDetails());
    log.setTimestamp(document.getTimestamp());
    return log;
  }
}
