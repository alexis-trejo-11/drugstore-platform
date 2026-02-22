package io.github.alexisTrejo11.drugstore.notifications.application.service;

import io.github.alexisTrejo11.drugstore.notifications.domain.model.Notification;
import io.github.alexisTrejo11.drugstore.notifications.domain.model.NotificationTemplate;
import io.github.alexisTrejo11.drugstore.notifications.domain.repository.NotificationRepository;
import io.github.alexisTrejo11.drugstore.notifications.domain.repository.NotificationTemplateRepository;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationChannel;
import io.github.alexisTrejo11.drugstore.notifications.infrastructure.sending.email.EmailSender;
import io.github.alexisTrejo11.drugstore.notifications.infrastructure.sending.email.EmailTemplateEngine;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;

@Service
public class EmailNotificationService {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(EmailNotificationService.class);
  private final EmailSender emailSender;
  private final EmailTemplateEngine templateEngine;
  private final NotificationRepository notificationRepository;
  private final NotificationTemplateRepository templateRepository;
  private final NotificationTrackingService trackingService;

  @Autowired
  public EmailNotificationService(EmailSender emailSender,
      EmailTemplateEngine templateEngine,
      NotificationRepository notificationRepository,
      NotificationTemplateRepository templateRepository,
      NotificationTrackingService trackingService) {
    this.emailSender = emailSender;
    this.templateEngine = templateEngine;
    this.notificationRepository = notificationRepository;
    this.templateRepository = templateRepository;
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

      // Get or build content
      String subject;
      String content;

      if (notification.getId() != null) {
        // Load and process template
        NotificationTemplate template = loadTemplate(notification);
        subject = templateEngine.processTemplate(template.getSubject(), templateVariables);
        content = templateEngine.processTemplate(template.getBodyTemplate(), templateVariables);

        if (template.getIsHtml()) {
          content = templateEngine.wrapInHtmlTemplate(content, subject);
        }
      } else {
        // Use direct content
        subject = notification.getSubject();
        content = notification.getContent();
      }

      trackingService.logInfo(notification.getId(), "EMAIL_SENDING",
          "Sending email to: " + notification.getRecipient().email());

      // Send email
      String messageId = emailSender.sendEmail(
          notification.getRecipient().email(),
          notification.getRecipient().getFullName(),
          subject,
          content,
          true // Always send as HTML for better formatting
      );

      // Mark as sent
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

  /**
   * Load template from database
   */
  private NotificationTemplate loadTemplate(Notification notification) {
    return templateRepository
        .findByTypeAndChannelAndLanguage(
            notification.getType(),
            NotificationChannel.EMAIL,
            "en" // TODO: Get from notification or user preferences
        )
        .orElseGet(() -> {
          log.warn("Template not found for type: {} and channel: EMAIL, using fallback",
              notification.getType());
          return createFallbackTemplate(notification);
        });
  }

  /**
   * Create a fallback template if none found in database
   */
  private NotificationTemplate createFallbackTemplate(Notification notification) {
    return NotificationTemplate.builder()
        .id("fallback")
        .type(notification.getType())
        .channel(NotificationChannel.EMAIL)
        .language("en")
        .subject(notification.getSubject() != null ? notification.getSubject() : "Notification")
        .bodyTemplate(notification.getContent() != null ? notification.getContent() : "You have a new notification.")
        .isHtml(false)
        .active(true)
        .build();
  }
}