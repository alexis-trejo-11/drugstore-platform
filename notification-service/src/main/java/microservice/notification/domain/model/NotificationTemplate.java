package microservice.notification.domain.model;

import microservice.notification.domain.valueobject.NotificationChannel;
import microservice.notification.domain.valueobject.NotificationType;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Entity representing a notification template
 */
public class NotificationTemplate {

  private String id;
  private NotificationType type;
  private NotificationChannel channel;
  private String language;
  private String subject;
  private String bodyTemplate;
  private Map<String, String> variables;
  private boolean active;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public NotificationTemplate() {
    this.variables = new HashMap<>();
    this.active = true;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  public NotificationTemplate(NotificationType type, NotificationChannel channel, String language) {
    this();
    this.type = type;
    this.channel = channel;
    this.language = language;
  }

  // Getters
  public String getId() {
    return id;
  }

  public NotificationType getType() {
    return type;
  }

  public NotificationChannel getChannel() {
    return channel;
  }

  public String getLanguage() {
    return language;
  }

  public String getSubject() {
    return subject;
  }

  public String getBodyTemplate() {
    return bodyTemplate;
  }

  public Map<String, String> getVariables() {
    return variables;
  }

  public boolean isActive() {
    return active;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  // Setters
  public void setId(String id) {
    this.id = id;
  }

  public void setType(NotificationType type) {
    this.type = type;
  }

  public void setChannel(NotificationChannel channel) {
    this.channel = channel;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public void setSubject(String subject) {
    this.subject = subject;
    this.updatedAt = LocalDateTime.now();
  }

  public void setBodyTemplate(String bodyTemplate) {
    this.bodyTemplate = bodyTemplate;
    this.updatedAt = LocalDateTime.now();
  }

  public void setVariables(Map<String, String> variables) {
    this.variables = variables;
  }

  public void setActive(boolean active) {
    this.active = active;
    this.updatedAt = LocalDateTime.now();
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  // Business methods
  public void addVariable(String key, String description) {
    if (this.variables == null) {
      this.variables = new HashMap<>();
    }
    this.variables.put(key, description);
    this.updatedAt = LocalDateTime.now();
  }

  public void deactivate() {
    this.active = false;
    this.updatedAt = LocalDateTime.now();
  }

  public void activate() {
    this.active = true;
    this.updatedAt = LocalDateTime.now();
  }
}
