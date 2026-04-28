package io.github.alexisTrejo11.drugstore.notifications.application.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.alexisTrejo11.drugstore.notifications.domain.model.Notification;
import io.github.alexisTrejo11.drugstore.notifications.domain.repository.NotificationRepository;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationChannel;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationId;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationPriority;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationStatus;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationType;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.RecipientInfo;

/**
 * Orchestrator service that routes notifications to appropriate channels
 * 
 * This service:
 * 1. Creates notification records
 * 2. Validates notification data
 * 3. Routes to appropriate channel service (Email/SMS)
 * 4. Handles retries for failed notifications
 * 5. Prevents duplicate notifications
 */
@Service
public class NotificationOrchestrator {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(NotificationOrchestrator.class);
  private final NotificationRepository notificationRepository;
  private final EmailNotificationService emailService;
  private final SmsNotificationService smsService;
  private final NotificationTrackingService trackingService;

  @Autowired
  public NotificationOrchestrator(
      NotificationRepository notificationRepository,
      EmailNotificationService emailService,
      SmsNotificationService smsService,
      NotificationTrackingService trackingService) {
    this.notificationRepository = notificationRepository;
    this.emailService = emailService;
    this.smsService = smsService;
    this.trackingService = trackingService;
  }

  @Transactional
  public NotificationId sendNotification(
      RecipientInfo recipientInfo,
      NotificationType type,
      NotificationChannel channel,
      NotificationPriority priority,
      String templateId,
      String subject,
      String content,
      Map<String, String> variables,
      String eventId,
      String correlationId) {
    log.info("Orchestrating notification - Type: {}, Channel: {}, User: {}, CorrelationId: {}",
        type, channel, recipientInfo.userId(), correlationId);

    try {
      // Check for duplicate (idempotency)
      if (eventId != null) {
        Optional<Notification> existing = notificationRepository.findByEventId(eventId);
        if (existing.isPresent()) {
          log.info("Notification already exists for eventId: {}, skipping", eventId);

          trackingService.logInfo(existing.get().getId(), "DUPLICATE_DETECTED",
              "Notification already processed for event: " + eventId);

          return existing.get().getId();
        }
      }

      // Validate recipient info for channel
      recipientInfo.validate(channel);

      // Create notification entity
      NotificationId notificationId = NotificationId.generate();
      Notification notification = createNotification(
          notificationId,
          recipientInfo,
          type,
          channel,
          priority,
          templateId,
          subject,
          content,
          variables,
          eventId,
          correlationId);

      // Save notification
      notification = notificationRepository.save(notification);

      trackingService.logInfo(notification.getId(), "NOTIFICATION_CREATED",
          "Notification created and ready to send");

      // Route to appropriate channel service
      boolean sent = routeToChannel(notification, variables);

      if (sent) {
        log.info("Notification sent successfully - ID: {}, Type: {}, Channel: {}",
            notificationId.value(), type, channel);
      } else {
        log.warn("Notification failed to send - ID: {}, Type: {}, Channel: {}",
            notificationId.value(), type, channel);
      }

      return notificationId;

    } catch (Exception e) {
      log.error("Error orchestrating notification - Type: {}, Channel: {}, User: {}",
          type, channel, recipientInfo.userId(), e);
      throw new RuntimeException("Failed to orchestrate notification", e);
    }
  }

  /**
   * Route notification to appropriate channel service
   */
  private boolean routeToChannel(Notification notification, Map<String, String> variables) {
    NotificationChannel channel = notification.getChannel();

    trackingService.logInfo(notification.getId(), "ROUTING",
        "Routing notification to channel: " + channel);

    try {
      switch (channel) {
        case EMAIL -> {
          return emailService.sendEmail(notification, variables);
        }

        case SMS -> {
          return smsService.sendSms(notification, variables);
        }

        case PUSH -> {
          // TODO: Implement push notification service
          log.warn("Push notifications not yet implemented");
          trackingService.logWarning(notification.getId(), "CHANNEL_NOT_IMPLEMENTED",
              "Push notification channel not implemented", null);
          return false;
        }

        case IN_APP -> {
          // TODO: Implement in-app notification service
          log.warn("In-app notifications not yet implemented");
          trackingService.logWarning(notification.getId(), "CHANNEL_NOT_IMPLEMENTED",
              "In-app notification channel not implemented", null);
          return false;
        }

        default -> {
          log.error("Unknown notification channel: {}", channel);
          trackingService.logError(notification.getId(), "UNKNOWN_CHANNEL",
              "Unknown channel: " + channel, null);
          return false;
        }
      }
    } catch (Exception e) {
      log.error("Error routing notification to channel: {}", channel, e);
      trackingService.logError(notification.getId(), "ROUTING_ERROR",
          "Error routing to channel: " + e.getMessage(), e);
      return false;
    }
  }

  /**
   * Create notification entity
   */
  private Notification createNotification(
      NotificationId notificationId,
      RecipientInfo recipientInfo,
      NotificationType type,
      NotificationChannel channel,
      NotificationPriority priority,
      String templateId,
      String subject,
      String content,
      Map<String, String> variables,
      String eventId,
      String correlationId) {

    Map<String, Object> metadata = Map.of(
        "templateId", templateId,
        "eventId", eventId,
        "correlationId", correlationId,
        "variables", variables);

    return Notification.builder()
        .id(notificationId)
        .recipient(recipientInfo)
        .correlationId(correlationId)
        .eventId(eventId)
        .channel(channel)
        .type(type)
        .status(NotificationStatus.PENDING)
        .priority(priority)
        .subject(subject)
        .content(content)
        .retryCount(0)
        .maxRetries(3)
        .metadata(metadata)
        .templateId(templateId)
        .build();
  }

  /**
   * Retry failed notifications
   * 
   * This method can be called by a scheduled job to retry failed notifications
   */
  @Transactional
  public void retryFailedNotifications() {
    log.info("Starting retry process for failed notifications");

    // Find notifications that failed less than 24 hours ago
    LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
    var failedNotifications = notificationRepository.findRetryableNotifications(cutoffTime);

    log.info("Found {} notifications eligible for retry", failedNotifications.size());

    for (Notification notification : failedNotifications) {
      try {
        if (notification.canRetry()) {
          notification.incrementRetry();
          notification.setStatus(NotificationStatus.PENDING);
          notificationRepository.save(notification);

          trackingService.logInfo(notification.getId(), "RETRY_ATTEMPT",
              "Retrying notification (attempt " + notification.getRetryCount() + ")");

          // Route again
          routeToChannel(notification, notification.getMetadataAsStringMap());
        }
      } catch (Exception e) {
        log.error("Error retrying notification: {}", notification.getId(), e);
        trackingService.logError(notification.getId(), "RETRY_ERROR",
            "Error during retry: " + e.getMessage(), e);
      }
    }
  }

}
