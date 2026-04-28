package io.github.alexisTrejo11.drugstore.notifications.infrastructure.messaging.kafka.handler;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.alexisTrejo11.drugstore.notifications.application.service.NotificationOrchestrator;
import io.github.alexisTrejo11.drugstore.notifications.domain.event.EmailVerificationEvent;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationChannel;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationId;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationPriority;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationType;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.RecipientInfo;

@Component
public class EmailVerificationEventHandler {

  private static final Logger log = LoggerFactory.getLogger(EmailVerificationEventHandler.class);
  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  private final NotificationOrchestrator notificationOrchestrator;

  @Autowired
  public EmailVerificationEventHandler(NotificationOrchestrator notificationOrchestrator) {
    this.notificationOrchestrator = notificationOrchestrator;
  }

  /**
   * Handle email verification event
   */
  public void handle(EmailVerificationEvent event) {
    log.info("Processing EmailVerificationEvent - EventId: {}, UserId: {}, Email: {}, CorrelationId: {}",
        event.eventId(), event.userId(), event.email(), event.correlationId());

    try {
      // Build recipient info
      RecipientInfo recipientInfo = RecipientInfo.builder()
          .userId(event.userId())
          .email(event.email())
          .firstName(event.firstName())
          .lastName(event.lastName())
          .language(event.language() != null ? event.language() : "en")
          .build();

      // Build template variables
      Map<String, String> variables = new HashMap<>();
      variables.put("firstName", event.firstName() != null ? event.firstName() : "User");
      variables.put("lastName", event.lastName() != null ? event.lastName() : "");
      variables.put("email", event.email());
      variables.put("verificationToken", event.verificationToken());
      variables.put("verificationCode", event.verificationCode() != null ? event.verificationCode() : "");
      variables.put("verificationUrl", buildVerificationUrl(event.callbackUrl(), event.verificationToken()));
      variables.put("expiresAt", event.expiresAt() != null ? event.expiresAt().format(FORMATTER) : "24 hours");
      variables.put("isResend", String.valueOf(event.isResend() != null && event.isResend()));

      // Send notification
      NotificationId notificationId = notificationOrchestrator.sendNotification(
          recipientInfo,
          NotificationType.EMAIL_VERIFICATION,
          NotificationChannel.EMAIL,
          NotificationPriority.HIGH, // Verification is high priority
          "email-verification-template", // Template ID
          "Verify your email address", // Subject
          null, // Content (will use template)
          variables,
          event.eventId(),
          event.correlationId());

      log.info("Email verification notification created - NotificationId: {}, EventId: {}",
          notificationId, event.eventId());

    } catch (Exception e) {
      log.error("Error processing EmailVerificationEvent - EventId: {}, UserId: {}",
          event.eventId(), event.userId(), e);
      throw new RuntimeException("Failed to process email verification event", e);
    }
  }

  /**
   * Build verification URL
   */
  private String buildVerificationUrl(String callbackUrl, String token) {
    if (callbackUrl == null || callbackUrl.isEmpty()) {
      return "https://placeholder.com/verify-email?token=" + token;
    }
    return callbackUrl + "?token=" + token;
  }
}
