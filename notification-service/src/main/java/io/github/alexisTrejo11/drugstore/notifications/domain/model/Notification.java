package io.github.alexisTrejo11.drugstore.notifications.domain.model;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationChannel;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationId;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationPriority;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationStatus;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationType;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.RecipientInfo;

/**
 * Aggregate root representing a Notification
 */
public class Notification {

  private NotificationId id;
  private NotificationType type;
  private String correlationId;
  private String eventId;
  private NotificationChannel channel;
  private NotificationPriority priority;
  private NotificationStatus status;
  private RecipientInfo recipient;
  private String subject;
  private String content;
  private String templateId;
  private Map<String, Object> metadata;
  private Integer maxRetries;
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
    this.metadata = Map.of();
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
    this.retryCount = 0;
    this.maxRetries = 3;
    this.recipient = RecipientInfo.NONE;
    this.correlationId = null;
    this.eventId = null;
    this.templateId = null;
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

  public String getCorrelationId() {
    return correlationId;
  }

  public String getEventId() {
    return eventId;
  }

  public String getTemplateId() {
    return templateId;
  }

  public Map<String, Object> getMetadata() {
    return metadata;
  }

  public Map<String, String> getMetadataAsStringMap() {
    if (metadata == null) {
      return Map.of();
    }
    return metadata.entrySet().stream()
        .filter(entry -> entry.getValue() instanceof String)
        .collect(Collectors.toMap(Map.Entry::getKey, entry -> (String) entry.getValue()));
  }

  public Integer getMaxRetries() {
    return maxRetries;
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

  public void setTemplateId(String templateId) {
    this.templateId = templateId;
  }

  public void setMetadata(Map<String, Object> metadata) {
    this.metadata = metadata;
  }

  public void setMaxRetries(Integer maxRetries) {
    this.maxRetries = maxRetries;
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

  public void setCorrelationId(String correlationId) {
    this.correlationId = correlationId;
  }

  public void setEventId(String eventId) {
    this.eventId = eventId;
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

  public void incrementRetry() {
    incrementRetryCount();
  }

  public boolean canRetry(int maxRetries) {
    return this.status != null && this.retryCount < maxRetries && !this.status.isFinal();
  }

  public boolean canRetry() {
    return this.maxRetries == null ? canRetry(3) : canRetry(this.maxRetries);
  }

  public void addMetadata(String key, String value) {
    if (this.metadata == null) {
      this.metadata = Map.of(key, value);
    } else {
      this.metadata.put(key, value);
    }
    this.updatedAt = LocalDateTime.now();
  }

  // Builder pattern
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private NotificationId id;
    private NotificationType type;
    private NotificationChannel channel;
    private NotificationPriority priority = NotificationPriority.NORMAL;
    private NotificationStatus status = NotificationStatus.PENDING;
    private RecipientInfo recipient = RecipientInfo.NONE;
    private String subject;
    private String content;
    private String templateId;
    private Map<String, Object> metadata;
    private Integer maxRetries = 3;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime scheduledAt;
    private LocalDateTime sentAt;
    private Integer retryCount = 0;
    private String errorMessage;
    private String correlationId;
    private String eventId;

    private Builder() {
    }

    public Builder id(NotificationId id) {
      this.id = id;
      return this;
    }

    public Builder type(NotificationType type) {
      this.type = type;
      return this;
    }

    public Builder channel(NotificationChannel channel) {
      this.channel = channel;
      return this;
    }

    public Builder priority(NotificationPriority priority) {
      this.priority = priority;
      return this;
    }

    public Builder status(NotificationStatus status) {
      this.status = status;
      return this;
    }

    public Builder recipient(RecipientInfo recipient) {
      this.recipient = recipient;
      return this;
    }

    public Builder subject(String subject) {
      this.subject = subject;
      return this;
    }

    public Builder content(String content) {
      this.content = content;
      return this;
    }

    public Builder templateId(String templateId) {
      this.templateId = templateId;
      return this;
    }

    public Builder metadata(Map<String, Object> metadata) {
      this.metadata = metadata;
      return this;
    }

    public Builder maxRetries(Integer maxRetries) {
      this.maxRetries = maxRetries;
      return this;
    }

    public Builder createdAt(LocalDateTime createdAt) {
      this.createdAt = createdAt;
      return this;
    }

    public Builder updatedAt(LocalDateTime updatedAt) {
      this.updatedAt = updatedAt;
      return this;
    }

    public Builder scheduledAt(LocalDateTime scheduledAt) {
      this.scheduledAt = scheduledAt;
      return this;
    }

    public Builder sentAt(LocalDateTime sentAt) {
      this.sentAt = sentAt;
      return this;
    }

    public Builder retryCount(Integer retryCount) {
      this.retryCount = retryCount;
      return this;
    }

    public Builder errorMessage(String errorMessage) {
      this.errorMessage = errorMessage;
      return this;
    }

    public Builder correlationId(String correlationId) {
      this.correlationId = correlationId;
      return this;
    }

    public Builder eventId(String eventId) {
      this.eventId = eventId;
      return this;
    }

    public Notification build() {
      Notification notification = new Notification();
      notification.id = this.id != null ? this.id : NotificationId.generate();
      notification.type = this.type;
      notification.channel = this.channel;
      notification.priority = this.priority;
      notification.status = this.status;
      notification.recipient = this.recipient;
      notification.subject = this.subject;
      notification.content = this.content;
      notification.templateId = this.templateId;
      notification.metadata = this.metadata != null ? this.metadata : Map.of();
      notification.maxRetries = this.maxRetries;
      notification.createdAt = this.createdAt != null ? this.createdAt : LocalDateTime.now();
      notification.updatedAt = this.updatedAt != null ? this.updatedAt : LocalDateTime.now();
      notification.scheduledAt = this.scheduledAt;
      notification.sentAt = this.sentAt;
      notification.retryCount = this.retryCount;
      notification.errorMessage = this.errorMessage;
      notification.correlationId = this.correlationId;
      notification.eventId = this.eventId;
      return notification;
    }
  }
}
