package io.github.alexisTrejo11.drugstore.notifications.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB document entity for Notification
 */
@Document(collection = "notifications")
public class NotificationDocument {

  @Id
  private String id;

  @Indexed
  private String type;

  @Indexed
  private String correlationId;

  @Indexed
  private String eventId;

  private String channel;

  private String priority;

  @Indexed
  private String status;

  @Indexed
  private String userId;

  private RecipientDocument recipient;

  private String subject;

  private String content;

  private String templateId;

  private Map<String, Object> metadata;

  private Integer maxRetries;

  @Indexed
  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  @Indexed
  private LocalDateTime scheduledAt;

  private LocalDateTime sentAt;

  private Integer retryCount;

  private String errorMessage;

  // Nested document for recipient info
  public static class RecipientDocument {
    private String userId;
    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String language;
    private String timezone;

    public RecipientDocument() {
    }

    public RecipientDocument(String userId, String email, String phoneNumber,
        String firstName, String lastName, String language, String timezone) {
      this.userId = userId;
      this.email = email;
      this.phoneNumber = phoneNumber;
      this.firstName = firstName;
      this.lastName = lastName;
      this.language = language;
      this.timezone = timezone;
    }

    // Getters and setters
    public String getUserId() {
      return userId;
    }

    public void setUserId(String userId) {
      this.userId = userId;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public String getPhoneNumber() {
      return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
      this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
      return firstName;
    }

    public void setFirstName(String firstName) {
      this.firstName = firstName;
    }

    public String getLastName() {
      return lastName;
    }

    public void setLastName(String lastName) {
      this.lastName = lastName;
    }

    public String getLanguage() {
      return language;
    }

    public void setLanguage(String language) {
      this.language = language;
    }

    public String getTimezone() {
      return timezone;
    }

    public void setTimezone(String timezone) {
      this.timezone = timezone;
    }
  }

  // Constructors
  public NotificationDocument() {
  }

  // Getters and setters
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public String getPriority() {
    return priority;
  }

  public void setPriority(String priority) {
    this.priority = priority;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public RecipientDocument getRecipient() {
    return recipient;
  }

  public void setRecipient(RecipientDocument recipient) {
    this.recipient = recipient;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getCorrelationId() {
    return correlationId;
  }

  public void setCorrelationId(String correlationId) {
    this.correlationId = correlationId;
  }

  public String getEventId() {
    return eventId;
  }

  public void setEventId(String eventId) {
    this.eventId = eventId;
  }

  public String getTemplateId() {
    return templateId;
  }

  public void setTemplateId(String templateId) {
    this.templateId = templateId;
  }

  public Map<String, Object> getMetadata() {
    return metadata;
  }

  public void setMetadata(Map<String, Object> metadata) {
    this.metadata = metadata;
  }

  public Integer getMaxRetries() {
    return maxRetries;
  }

  public void setMaxRetries(Integer maxRetries) {
    this.maxRetries = maxRetries;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public LocalDateTime getScheduledAt() {
    return scheduledAt;
  }

  public void setScheduledAt(LocalDateTime scheduledAt) {
    this.scheduledAt = scheduledAt;
  }

  public LocalDateTime getSentAt() {
    return sentAt;
  }

  public void setSentAt(LocalDateTime sentAt) {
    this.sentAt = sentAt;
  }

  public Integer getRetryCount() {
    return retryCount;
  }

  public void setRetryCount(Integer retryCount) {
    this.retryCount = retryCount;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }
}
