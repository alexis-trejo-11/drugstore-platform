package io.github.alexisTrejo11.drugstore.notifications.domain.exception;

/**
 * Thrown when a classpath notification template cannot be resolved.
 */
public class TemplateNotFoundException extends NotificationDomainException {

  public TemplateNotFoundException(String templateId, String channel) {
    super("Template not found: " + templateId + " (channel: " + channel + ")");
  }
}
