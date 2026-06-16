package io.github.alexisTrejo11.drugstore.notifications.application.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.alexisTrejo11.drugstore.notifications.domain.exception.TemplateNotFoundException;
import io.github.alexisTrejo11.drugstore.notifications.domain.model.Notification;
import io.github.alexisTrejo11.drugstore.notifications.domain.repository.NotificationRepository;
import io.github.alexisTrejo11.drugstore.notifications.infrastructure.sending.email.EmailSender;
import io.github.alexisTrejo11.drugstore.notifications.infrastructure.sending.template.NotificationTemplateRenderer;

@Service
public class EmailNotificationService {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(EmailNotificationService.class);
  private final EmailSender emailSender;
  private final NotificationTemplateRenderer templateRenderer;
  private final NotificationRepository notificationRepository;
  private final NotificationTrackingService trackingService;

  @Autowired
  public EmailNotificationService(EmailSender emailSender,
      NotificationTemplateRenderer templateRenderer,
      NotificationRepository notificationRepository,
      NotificationTrackingService trackingService) {
    this.emailSender = emailSender;
    this.templateRenderer = templateRenderer;
    this.notificationRepository = notificationRepository;
    this.trackingService = trackingService;
  }

  /**
   * Send an email notification
   *
   * @param notification      Notification entity with all details
   * @param templateVariables Variables to replace in template
   * @return true if sent successfully
   */
  @Transactional
  public boolean sendEmail(Notification notification, Map<String, String> templateVariables) {
    try {
      trackingService.logInfo(
          notification.getId(),
          "EMAIL_PROCESSING",
          "Starting email preparation");

      String subject = resolveSubject(notification);
      String content = resolveContent(notification, templateVariables);

      trackingService.logInfo(notification.getId(), "EMAIL_SENDING",
          "Sending email to: " + notification.getRecipient().email());

      String messageId = emailSender.sendEmail(
          notification.getRecipient().email(),
          notification.getRecipient().getFullName(),
          subject,
          content,
          true);

      notification.markAsSent();
      notificationRepository.save(notification);

      Map<String, Object> details = new HashMap<>();
      details.put("provider_message_id", messageId);
      details.put("recipient", notification.getRecipient().email());
      trackingService.logInfo(notification.getId(), "EMAIL_SENT",
          "Email sent successfully", details);

      return true;

    } catch (MessagingException e) {
      log.error("Failed to send email for notification: {}", notification.getId(), e);
      trackingService.logError(notification.getId(), "EMAIL_SEND_FAILED",
          "Messaging error: " + e.getMessage(), e);

      notification.markAsFailed("Email send failed: " + e.getMessage());
      notificationRepository.save(notification);
      return false;

    } catch (Exception e) {
      log.error("Unexpected error sending email for notification: {}", notification.getId(), e);
      trackingService.logError(notification.getId(), "EMAIL_SEND_ERROR",
          "Unexpected error: " + e.getMessage(), e);

      notification.markAsFailed("Unexpected error: " + e.getMessage());
      notificationRepository.save(notification);
      return false;
    }
  }

  private String resolveSubject(Notification notification) {
    if (notification.getSubject() != null && !notification.getSubject().isBlank()) {
      return notification.getSubject();
    }
    return "Notification";
  }

  private String resolveContent(Notification notification, Map<String, String> templateVariables) {
    String templateId = notification.getTemplateId();
    String language = notification.getRecipient().getLanguageOrDefault();

    if (templateId != null && !templateId.isBlank()) {
      try {
        return templateRenderer.renderEmail(templateId, templateVariables, language);
      } catch (TemplateNotFoundException e) {
        log.warn("Email template not found: {}, using fallback content", templateId);
      }
    }

    if (notification.getContent() != null && !notification.getContent().isBlank()) {
      return notification.getContent();
    }

    return "<p>You have a new notification.</p>";
  }
}
