package io.github.alexisTrejo11.drugstore.notifications.infrastructure.messaging.kafka.handler;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import io.github.alexisTrejo11.drugstore.notifications.application.service.NotificationOrchestrator;
import io.github.alexisTrejo11.drugstore.notifications.domain.event.TwoFactorCodeEvent;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationChannel;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationId;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationPriority;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationType;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.RecipientInfo;

public class TwoFactorCodeEventHandler {

  private final NotificationOrchestrator notificationOrchestrator;
  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TwoFactorCodeEventHandler.class);

  @Autowired
  public TwoFactorCodeEventHandler(NotificationOrchestrator notificationOrchestrator) {
    this.notificationOrchestrator = notificationOrchestrator;
  }

  /**
   * Handle two-factor code event
   */
  public void handle(TwoFactorCodeEvent event) {
    log.info("Processing TwoFactorCodeEvent - EventId: {}, UserId: {}, Channel: {}, Purpose: {}, CorrelationId: {}",
        event.eventId(), event.userId(), event.channel(), event.purpose(), event.correlationId());

    try {
      // Determine channels to use
      boolean sendEmail = "EMAIL".equalsIgnoreCase(event.channel()) ||
          "BOTH".equalsIgnoreCase(event.channel());
      boolean sendSms = "SMS".equalsIgnoreCase(event.channel()) ||
          "BOTH".equalsIgnoreCase(event.channel());

      // Build recipient info
      RecipientInfo recipientInfo = RecipientInfo.builder()
          .userId(event.userId())
          .email(event.email())
          .phoneNumber(event.phoneNumber())
          .firstName(event.firstName())
          .language(event.language() != null ? event.language() : "en")
          .build();

      // Build template variables
      Map<String, String> variables = new HashMap<>();
      variables.put("firstName", event.firstName() != null ? event.firstName() : "User");
      variables.put("code", event.code());
      variables.put("purpose", getPurposeDisplayName(event.purpose()));
      variables.put("expiresAt", event.expiresAt() != null ? event.expiresAt().format(FORMATTER) : "10 minutes");
      variables.put("ipAddress", event.ipAddress() != null ? event.ipAddress() : "Unknown");
      variables.put("deviceName", event.deviceName() != null ? event.deviceName() : "Unknown device");

      // Send via EMAIL
      if (sendEmail && event.email() != null) {
        NotificationId emailNotificationId = notificationOrchestrator.sendNotification(
            recipientInfo,
            NotificationType.TWO_FACTOR_CODE,
            NotificationChannel.EMAIL,
            NotificationPriority.URGENT, // 2FA is urgent
            "two-factor-code-email-template",
            "Your verification code",
            null,
            variables,
            event.eventId() + "-email",
            event.correlationId());

        log.info("2FA email notification created - NotificationId: {}, EventId: {}",
            emailNotificationId, event.eventId());
      }

      // Send via SMS
      if (sendSms && event.phoneNumber() != null) {
        NotificationId smsNotificationId = notificationOrchestrator.sendNotification(
            recipientInfo,
            NotificationType.TWO_FACTOR_CODE,
            NotificationChannel.SMS,
            NotificationPriority.URGENT,
            "two-factor-code-sms-template",
            null, // SMS doesn't need subject
            null,
            variables,
            event.eventId() + "-sms",
            event.correlationId());

        log.info("2FA SMS notification created - NotificationId: {}, EventId: {}",
            smsNotificationId, event.eventId());
      }

    } catch (Exception e) {
      log.error("Error processing TwoFactorCodeEvent - EventId: {}, UserId: {}",
          event.eventId(), event.userId(), e);
      throw new RuntimeException("Failed to process two-factor code event", e);
    }
  }

  /**
   * Get user-friendly purpose display name
   */
  private String getPurposeDisplayName(String purpose) {
    if (purpose == null)
      return "authentication";

    return switch (purpose.toLowerCase()) {
      case "login" -> "login";
      case "enable-2fa" -> "enabling two-factor authentication";
      case "sensitive-operation" -> "performing a sensitive operation";
      default -> purpose;
    };
  }
}