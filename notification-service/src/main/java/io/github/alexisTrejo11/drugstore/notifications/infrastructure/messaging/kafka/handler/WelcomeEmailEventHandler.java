package io.github.alexisTrejo11.drugstore.notifications.infrastructure.messaging.kafka.handler;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.alexisTrejo11.drugstore.notifications.application.service.NotificationOrchestrator;
import io.github.alexisTrejo11.drugstore.notifications.domain.event.WelcomeEmailEvent;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationChannel;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationId;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationPriority;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationType;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.RecipientInfo;

@Component
public class WelcomeEmailEventHandler {

  private final NotificationOrchestrator notificationOrchestrator;
  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(WelcomeEmailEventHandler.class);

  @Autowired
  public WelcomeEmailEventHandler(NotificationOrchestrator notificationOrchestrator) {
    this.notificationOrchestrator = notificationOrchestrator;
  }

  /**
   * Handle welcome email event
   */
  public void handle(WelcomeEmailEvent event) {
    log.info("Processing WelcomeEmailEvent - EventId: {}, UserId: {}, Email: {}, CorrelationId: {}",
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
      variables.put("firstName", event.firstName() != null ? event.firstName() : "");
      variables.put("lastName", event.lastName() != null ? event.lastName() : "");
      variables.put("fullName", String.format("%s %s",
          event.firstName() != null ? event.firstName() : "",
          event.lastName() != null ? event.lastName() : "").trim());
      variables.put("email", event.email());
      variables.put("accountType", getAccountTypeDisplayName(event.accountType()));
      variables.put("dashboardUrl", event.dashboardUrl() != null ? event.dashboardUrl() : "#");
      variables.put("profileUrl", event.profileUrl() != null ? event.profileUrl() : "#");
      variables.put("supportUrl", event.supportUrl() != null ? event.supportUrl() : "#");
      variables.put("registeredAt", event.registeredAt() != null ? event.registeredAt().format(FORMATTER) : "");
      variables.put("currentYear", String.valueOf(java.time.Year.now().getValue()));

      // Send notification
      NotificationId notificationId = notificationOrchestrator.sendNotification(
          recipientInfo,
          NotificationType.WELCOME,
          NotificationChannel.EMAIL,
          NotificationPriority.NORMAL, // Welcome emails are normal priority
          "welcome-email-template",
          "Welcome to Drugstore!",
          null,
          variables,
          event.eventId(),
          event.correlationId());

      log.info("Welcome email notification created - NotificationId: {}, EventId: {}",
          notificationId, event.eventId());

    } catch (Exception e) {
      log.error("Error processing WelcomeEmailEvent - EventId: {}, UserId: {}",
          event.eventId(), event.userId(), e);
      throw new RuntimeException("Failed to process welcome email event", e);
    }
  }

  /**
   * Get user-friendly account type display name
   */
  private String getAccountTypeDisplayName(String accountType) {
    if (accountType == null)
      return "Customer";

    return switch (accountType.toLowerCase()) {
      case "customer" -> "Customer";
      case "employee" -> "Employee";
      case "admin" -> "Administrator";
      default -> accountType;
    };
  }
}