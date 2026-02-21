package microservice.notification.domain.model;

import microservice.notification.domain.valueobject.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Aggregate root representing a Notification
 */
public class Notification {

  private NotificationId id;
  private NotificationType type;
  private NotificationChannel channel;
  private NotificationPriority priority;
  private NotificationStatus status;
  private RecipientInfo recipient;
  private String subject;
  private String content;
  private Map<String, Object> metadata;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime scheduledAt;
  private LocalDateTime sentAt;
  private Integer retryCount;
  private String errorMessage;

  public Notification() {
    this.id = NotificationId.generate();
    this.status = NotificationStatus.PENDING;
    this.priority = NotificationPriority.NORMAL;
    this.metadata = new HashMap<>();
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
    this.retryCount = 0;
  }

  public Notification(NotificationType type, NotificationChannel channel, RecipientInfo recipient) {
    this();
    this.type = type;
    this.channel = channel;
    this.recipient = recipient;
  }

  // Getters
  public NotificationId getId() {
    return id;
  }

  public NotificationType getType() {
    return type;
  }

  public NotificationChannel getChannel() {
    return channel;
  }

  public NotificationPriority getPriority() {
    return priority;
  }

  public NotificationStatus getStatus() {
    return status;
  }

  public RecipientInfo getRecipient() {
    return recipient;
  }

  public String getSubject() {
    return subject;
  }

  public String getContent() {
    return content;
  }

  public Map<String, Object> getMetadata() {
    return metadata;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public LocalDateTime getScheduledAt() {
    return scheduledAt;
  }

  public LocalDateTime getSentAt() {
    return sentAt;
  }

  public Integer getRetryCount() {
    return retryCount;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  // Setters
  public void setId(NotificationId id) {
    this.id = id;
  }

  public void setType(NotificationType type) {
    this.type = type;
  }

  public void setChannel(NotificationChannel channel) {
    this.channel = channel;
  }

  public void setPriority(NotificationPriority priority) {
    this.priority = priority;
  }

  public void setStatus(NotificationStatus status) {
    this.status = status;
    this.updatedAt = LocalDateTime.now();
  }

  public void setRecipient(RecipientInfo recipient) {
    this.recipient = recipient;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public void setMetadata(Map<String, Object> metadata) {
    this.metadata = metadata;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public void setScheduledAt(LocalDateTime scheduledAt) {
    this.scheduledAt = scheduledAt;
  }

  public void setSentAt(LocalDateTime sentAt) {
    this.sentAt = sentAt;
  }

  public void setRetryCount(Integer retryCount) {
    this.retryCount = retryCount;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  // Business methods
  public void markAsProcessing() {
    this.status = NotificationStatus.PROCESSING;
    this.updatedAt = LocalDateTime.now();
  }

  public void markAsSent() {
    this.status = NotificationStatus.SENT;
    this.sentAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  public void markAsDelivered() {
    this.status = NotificationStatus.DELIVERED;
    this.updatedAt = LocalDateTime.now();
  }

  public void markAsFailed(String errorMessage) {
    this.status = NotificationStatus.FAILED;
    this.errorMessage = errorMessage;
    this.updatedAt = LocalDateTime.now();
  }

  public void incrementRetryCount() {
    this.retryCount++;
    this.updatedAt = LocalDateTime.now();
  }

  public void addMetadata(String key, Object value) {
    if (this.metadata == null) {
      this.metadata = new HashMap<>();
    }
    this.metadata.put(key, value);
  }

  public boolean canRetry(int maxRetries) {
    return this.retryCount < maxRetries && !this.status.isFinal();
  }
}
