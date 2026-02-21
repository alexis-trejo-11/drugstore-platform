package microservice.notification.domain.model;

import microservice.notification.domain.valueobject.NotificationId;
import microservice.notification.domain.valueobject.NotificationStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Entity representing a log entry for notification tracking
 */
public class NotificationLog {

  private String id;
  private NotificationId notificationId;
  private NotificationStatus status;
  private String message;
  private Map<String, Object> details;
  private LocalDateTime timestamp;

  public NotificationLog() {
    this.details = new HashMap<>();
    this.timestamp = LocalDateTime.now();
  }

  public NotificationLog(NotificationId notificationId, NotificationStatus status, String message) {
    this();
    this.notificationId = notificationId;
    this.status = status;
    this.message = message;
  }

  // Getters
  public String getId() {
    return id;
  }

  public NotificationId getNotificationId() {
    return notificationId;
  }

  public NotificationStatus getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }

  public Map<String, Object> getDetails() {
    return details;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  // Setters
  public void setId(String id) {
    this.id = id;
  }

  public void setNotificationId(NotificationId notificationId) {
    this.notificationId = notificationId;
  }

  public void setStatus(NotificationStatus status) {
    this.status = status;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void setDetails(Map<String, Object> details) {
    this.details = details;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  // Business methods
  public void addDetail(String key, Object value) {
    if (this.details == null) {
      this.details = new HashMap<>();
    }
    this.details.put(key, value);
  }

  public static NotificationLog success(NotificationId notificationId, String message) {
    return new NotificationLog(notificationId, NotificationStatus.SENT, message);
  }

  public static NotificationLog failure(NotificationId notificationId, String errorMessage) {
    return new NotificationLog(notificationId, NotificationStatus.FAILED, errorMessage);
  }
}
