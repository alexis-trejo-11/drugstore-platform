package io.github.alexisTrejo11.drugstore.notifications.application.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.github.alexisTrejo11.drugstore.notifications.domain.model.NotificationLog;
import io.github.alexisTrejo11.drugstore.notifications.domain.repository.NotificationLogRepository;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationId;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationStatus;

/**
 * Service for tracking and logging notification events
 */
@Service
public class NotificationTrackingService {

  private static final Logger log = LoggerFactory.getLogger(NotificationTrackingService.class);

  private final NotificationLogRepository logRepository;

  public NotificationTrackingService(NotificationLogRepository logRepository) {
    this.logRepository = logRepository;
  }

  /**
   * Log an informational event
   */
  public void logInfo(NotificationId notificationId, String eventType, String message) {
    logInfo(notificationId, eventType, message, null);
  }

  /**
   * Log an informational event with details
   */
  public void logInfo(NotificationId notificationId, String eventType,
      String message, Map<String, Object> details) {
    try {
      NotificationLog logEntry = new NotificationLog(
          notificationId,
          NotificationStatus.PROCESSING,
          String.format("[%s] %s", eventType, message));

      if (details != null) {
        logEntry.setDetails(details);
      }

      logRepository.save(logEntry);
      log.info("Notification {} - {}: {}", notificationId.value(), eventType, message);
    } catch (Exception e) {
      log.error("Failed to save notification log", e);
    }
  }

  /**
   * Log an error event
   */
  public void logError(NotificationId notificationId, String eventType,
      String message, Exception exception) {
    try {
      NotificationLog logEntry = new NotificationLog(
          notificationId,
          NotificationStatus.FAILED,
          String.format("[%s] %s", eventType, message));

      if (exception != null) {
        logEntry.addDetail("error_class", exception.getClass().getName());
        logEntry.addDetail("error_message", exception.getMessage());
      }

      logRepository.save(logEntry);
      log.error("Notification {} - {}: {}", notificationId.value(), eventType, message, exception);
    } catch (Exception e) {
      log.error("Failed to save error log", e);
    }
  }

  /**
   * Log a warning event
   */
  public void logWarning(NotificationId notificationId, String eventType,
      String message, Exception exception) {
    try {
      NotificationLog logEntry = new NotificationLog(
          notificationId,
          NotificationStatus.PENDING,
          String.format("[%s] %s", eventType, message));

      if (exception != null) {
        logEntry.addDetail("error_class", exception.getClass().getName());
        logEntry.addDetail("error_message", exception.getMessage());
      }

      logRepository.save(logEntry);
      log.warn("Notification {} - {}: {}", notificationId.value(), eventType, message);
    } catch (Exception e) {
      log.error("Failed to save warning log", e);
    }
  }

  /**
   * Log success event
   */
  public void logSuccess(NotificationId notificationId, String message) {
    logSuccess(notificationId, message, null);
  }

  /**
   * Log success event with details
   */
  public void logSuccess(NotificationId notificationId, String message,
      Map<String, Object> details) {
    try {
      NotificationLog logEntry = new NotificationLog(
          notificationId,
          NotificationStatus.SENT,
          message);

      if (details != null) {
        logEntry.setDetails(details);
      }

      logRepository.save(logEntry);
      log.info("Notification {} succeeded: {}", notificationId.value(), message);
    } catch (Exception e) {
      log.error("Failed to save success log", e);
    }
  }
}
