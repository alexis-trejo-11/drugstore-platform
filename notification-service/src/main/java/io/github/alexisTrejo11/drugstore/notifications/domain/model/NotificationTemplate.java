package io.github.alexisTrejo11.drugstore.notifications.domain.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationChannel;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationType;

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
  private boolean isHtml;

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

  public boolean getIsHtml() {
    return isHtml;
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

  public void setIsHtml(boolean isHtml) {
    this.isHtml = isHtml;
    this.updatedAt = LocalDateTime.now();
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

  // Builder pattern
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String id;
    private NotificationType type;
    private NotificationChannel channel;
    private String language;
    private String subject;
    private String bodyTemplate;
    private Map<String, String> variables = new HashMap<>();
    private boolean active = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isHtml;

    private Builder() {
    }

    public Builder id(String id) {
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

    public Builder language(String language) {
      this.language = language;
      return this;
    }

    public Builder subject(String subject) {
      this.subject = subject;
      return this;
    }

    public Builder bodyTemplate(String bodyTemplate) {
      this.bodyTemplate = bodyTemplate;
      return this;
    }

    public Builder variables(Map<String, String> variables) {
      this.variables = variables;
      return this;
    }

    public Builder addVariable(String key, String description) {
      this.variables.put(key, description);
      return this;
    }

    public Builder active(boolean active) {
      this.active = active;
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

    public Builder isHtml(boolean isHtml) {
      this.isHtml = isHtml;
      return this;
    }

    public NotificationTemplate build() {
      NotificationTemplate template = new NotificationTemplate();
      template.id = this.id;
      template.type = this.type;
      template.channel = this.channel;
      template.language = this.language;
      template.subject = this.subject;
      template.bodyTemplate = this.bodyTemplate;
      template.variables = this.variables;
      template.active = this.active;
      template.isHtml = this.isHtml;
      template.createdAt = this.createdAt != null ? this.createdAt : LocalDateTime.now();
      template.updatedAt = this.updatedAt != null ? this.updatedAt : LocalDateTime.now();
      return template;
    }
  }
}
