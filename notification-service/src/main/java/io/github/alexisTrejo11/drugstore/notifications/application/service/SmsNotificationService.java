package io.github.alexisTrejo11.drugstore.notifications.application.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.alexisTrejo11.drugstore.notifications.domain.model.Notification;
import io.github.alexisTrejo11.drugstore.notifications.domain.repository.NotificationRepository;
import io.github.alexisTrejo11.drugstore.notifications.domain.repository.NotificationTemplateRepository;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationChannel;
import io.github.alexisTrejo11.drugstore.notifications.infrastructure.sending.email.EmailTemplateEngine;
import io.github.alexisTrejo11.drugstore.notifications.infrastructure.sending.sms.SmsSender;

/**
 * Service for sending SMS notifications
 * 
 * Handles template processing and SMS sending through Twilio
 */
@Service
public class SmsNotificationService {
  private static final Logger log = LoggerFactory.getLogger(SmsNotificationService.class);

  private final EmailTemplateEngine templateEngine;
  private final NotificationRepository notificationRepository;
  private final NotificationTemplateRepository templateRepository;
  private final SmsSender smsSender;
  private final NotificationTrackingService trackingService;

  @Autowired
  public SmsNotificationService(EmailTemplateEngine templateEngine,
      NotificationRepository notificationRepository,
      NotificationTemplateRepository templateRepository,
      SmsSender smsSender,
      NotificationTrackingService trackingService) {
    this.templateEngine = templateEngine;
    this.notificationRepository = notificationRepository;
    this.templateRepository = templateRepository;
    this.smsSender = smsSender;
    this.trackingService = trackingService;
  }

  /**
   * Send an SMS notification
   * 
   * @param notification      Notification entity with all details
   * @param templateVariables Variables to replace in template
   * @return true if sent successfully
   */
  @Transactional
  public boolean sendSms(Notification notification, Map<String, String> templateVariables) {
    String notificationId = notification.getId().value();

    try {
      trackingService.logInfo(notification.getId(), "SMS_PROCESSING",
          "Starting SMS preparation");

      // Validate phone number
      String phoneNumber = notification.getRecipient().phoneNumber();
      if (!smsSender.isValidPhoneNumber(phoneNumber)) {
        trackingService.logError(notification.getId(), "SMS_VALIDATION",
            "Invalid phone number: " + phoneNumber, null);
        notification.markAsFailed("Invalid phone number format");
        notificationRepository.save(notification);
        return false;
      }

      // Get or build content
      String content = buildContent(notification, templateVariables);

      // Ensure content is not too long (Twilio limit: 1600 chars)
      if (content.length() > 1600) {
        log.warn("SMS content exceeds 1600 characters for notification: {}", notificationId);
        content = content.substring(0, 1597) + "...";
      }

      trackingService.logInfo(notification.getId(), "SMS_SENDING",
          "Sending SMS to: " + phoneNumber);

      // Send SMS
      String messageSid = smsSender.sendSms(phoneNumber, content);

      // Mark as sent
      notification.markAsSent();
      notification.addMetadata("provider_message_id", messageSid);
      notification.addMetadata("provider", "twilio");
      notification.addMetadata("recipient", phoneNumber);
      notificationRepository.save(notification);

      Map<String, Object> details = new HashMap<>();
      details.put("provider_message_id", messageSid);
      details.put("recipient", phoneNumber);
      trackingService.logInfo(notification.getId(), "SMS_SENT",
          "SMS sent successfully", details);

      return true;

    } catch (IllegalArgumentException e) {
      log.error("Invalid arguments for SMS notification: {}", notificationId, e);
      trackingService.logError(notification.getId(), "SMS_VALIDATION_ERROR",
          "Validation error: " + e.getMessage(), e);

      notification.markAsFailed("Validation error: " + e.getMessage());
      notificationRepository.save(notification);
      return false;

    } catch (Exception e) {
      log.error("Failed to send SMS for notification: {}", notificationId, e);
      trackingService.logError(notification.getId(), "SMS_SEND_FAILED",
          "Send error: " + e.getMessage(), e);

      notification.markAsFailed("SMS send failed: " + e.getMessage());
      notificationRepository.save(notification);
      return false;
    }
  }

  /**
   * Build content from template or direct content
   */
  private String buildContent(Notification notification, Map<String, String> templateVariables) {
    // Try to load template from database
    return templateRepository
        .findByTypeAndChannelAndLanguage(
            notification.getType(),
            NotificationChannel.SMS,
            notification.getRecipient().getLanguageOrDefault())
        .map(template -> templateEngine.processTemplate(
            template.getBodyTemplate(),
            templateVariables != null ? templateVariables : new HashMap<>()))
        .orElseGet(() -> {
          log.warn("SMS template not found for type: {}, using direct content",
              notification.getType());
          return notification.getContent() != null ? notification.getContent() : "You have a new notification.";
        });
  }
}
