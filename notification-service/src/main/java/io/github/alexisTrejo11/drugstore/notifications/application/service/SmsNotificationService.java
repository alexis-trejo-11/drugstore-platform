package io.github.alexisTrejo11.drugstore.notifications.application.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.alexisTrejo11.drugstore.notifications.domain.exception.TemplateNotFoundException;
import io.github.alexisTrejo11.drugstore.notifications.domain.model.Notification;
import io.github.alexisTrejo11.drugstore.notifications.domain.repository.NotificationRepository;
import io.github.alexisTrejo11.drugstore.notifications.infrastructure.sending.sms.SmsSender;
import io.github.alexisTrejo11.drugstore.notifications.infrastructure.sending.template.NotificationTemplateRenderer;

/**
 * Service for sending SMS notifications.
 *
 * Handles Thymeleaf text-template processing and SMS sending through Twilio.
 */
@Service
public class SmsNotificationService {
  private static final Logger log = LoggerFactory.getLogger(SmsNotificationService.class);

  private final NotificationRepository notificationRepository;
  private final NotificationTemplateRenderer templateRenderer;
  private final SmsSender smsSender;
  private final NotificationTrackingService trackingService;

  @Autowired
  public SmsNotificationService(NotificationRepository notificationRepository,
      NotificationTemplateRenderer templateRenderer,
      SmsSender smsSender,
      NotificationTrackingService trackingService) {
    this.notificationRepository = notificationRepository;
    this.templateRenderer = templateRenderer;
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

      String phoneNumber = notification.getRecipient().phoneNumber();
      if (!smsSender.isValidPhoneNumber(phoneNumber)) {
        trackingService.logError(notification.getId(), "SMS_VALIDATION",
            "Invalid phone number: " + phoneNumber, null);
        notification.markAsFailed("Invalid phone number format");
        notificationRepository.save(notification);
        return false;
      }

      String content = buildContent(notification, templateVariables);

      if (content.length() > 1600) {
        log.warn("SMS content exceeds 1600 characters for notification: {}", notificationId);
        content = content.substring(0, 1597) + "...";
      }

      trackingService.logInfo(notification.getId(), "SMS_SENDING",
          "Sending SMS to: " + phoneNumber);

      String messageSid = smsSender.sendSms(phoneNumber, content);

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

  private String buildContent(Notification notification, Map<String, String> templateVariables) {
    String templateId = notification.getTemplateId();
    String language = notification.getRecipient().getLanguageOrDefault();

    if (templateId != null && !templateId.isBlank()) {
      try {
        return templateRenderer.renderSms(templateId, templateVariables, language);
      } catch (TemplateNotFoundException e) {
        log.warn("SMS template not found: {}, using direct content", templateId);
      }
    }

    return notification.getContent() != null ? notification.getContent() : "You have a new notification.";
  }
}
