package io.github.alexisTrejo11.drugstore.notifications.application.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.alexisTrejo11.drugstore.notifications.application.NotificationMonitoringService;
import io.github.alexisTrejo11.drugstore.notifications.application.dto.NotificationMetrics;
import io.github.alexisTrejo11.drugstore.notifications.domain.exception.NotificationNotFoundException;
import io.github.alexisTrejo11.drugstore.notifications.domain.model.Notification;
import io.github.alexisTrejo11.drugstore.notifications.domain.repository.NotificationRepository;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationId;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationStatus;

/**
 * Implementation of NotificationMonitoringService
 * 
 * Provides monitoring and querying capabilities for notifications
 */
@Service
@Transactional(readOnly = true)
public class NotificationMonitoringServiceImpl implements NotificationMonitoringService {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory
      .getLogger(NotificationMonitoringServiceImpl.class);

  private final NotificationRepository notificationRepository;

  public NotificationMonitoringServiceImpl(NotificationRepository notificationRepository) {
    this.notificationRepository = notificationRepository;
  }

  @Override
  public Page<Notification> getNotificationsByUserId(String userId, Pageable pageable) {
    if (userId == null || userId.trim().isEmpty()) {
      throw new IllegalArgumentException("User ID cannot be null or empty");
    }

    log.debug("Fetching notifications for user: {}, page: {}", userId, pageable.getPageNumber());
    return notificationRepository.findByUserId(userId, pageable);
  }

  @Override
  public Page<Notification> getNotificationsByStatus(String status, Pageable pageable) {
    if (status == null || status.trim().isEmpty()) {
      throw new IllegalArgumentException("Status cannot be null or empty");
    }

    NotificationStatus notificationStatus;
    try {
      notificationStatus = NotificationStatus.valueOf(status.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid notification status: " + status, e);
    }

    log.debug("Fetching notifications with status: {}, page: {}", status, pageable.getPageNumber());
    return notificationRepository.findByStatus(notificationStatus, pageable);
  }

  @Override
  public Notification getNotificationById(String id) {
    if (id == null || id.trim().isEmpty()) {
      throw new IllegalArgumentException("Notification ID cannot be null or empty");
    }

    log.debug("Fetching notification by ID: {}", id);
    NotificationId notificationId = new NotificationId(id);

    return notificationRepository.findById(notificationId)
        .orElseThrow(() -> new NotificationNotFoundException(
            "Notification not found with ID: " + id));
  }

  @Override
  public Notification getNotificationByCorrelationId(String correlationId) {
    if (correlationId == null || correlationId.trim().isEmpty()) {
      throw new IllegalArgumentException("Correlation ID cannot be null or empty");
    }

    log.debug("Fetching notification by correlation ID: {}", correlationId);
    return notificationRepository.findByCorrelationId(correlationId)
        .orElseThrow(() -> new NotificationNotFoundException(
            "Notification not found with correlation ID: " + correlationId));
  }

  @Override
  public Notification getNotificationByEventId(String eventId) {
    if (eventId == null || eventId.trim().isEmpty()) {
      throw new IllegalArgumentException("Event ID cannot be null or empty");
    }

    log.debug("Fetching notification by event ID: {}", eventId);
    return notificationRepository.findByEventId(eventId)
        .orElseThrow(() -> new NotificationNotFoundException(
            "Notification not found with event ID: " + eventId));
  }

  @Override
  public Page<Notification> getRecentNotification(Pageable pageable) {
    log.debug("Fetching recent notifications, page: {}", pageable.getPageNumber());
    return notificationRepository.findAllByOrderByCreatedAtDesc(pageable);
  }

  @Override
  public NotificationMetrics getNotificationMetrics() {
    log.debug("Calculating notification metrics");

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime last24Hours = now.minusHours(24);

    long totalNotifications = notificationRepository.count();
    long pendingNotifications = notificationRepository.countByStatus(NotificationStatus.PENDING);
    long sentNotifications = notificationRepository.countByStatus(NotificationStatus.SENT);
    long failedNotifications = notificationRepository.countByStatus(NotificationStatus.FAILED);

    // Retryable notifications are those that are failed and can still be retried
    long retryableNotifications = notificationRepository
        .findRetryableNotifications(last24Hours)
        .size();

    return new NotificationMetrics(
        totalNotifications,
        pendingNotifications,
        sentNotifications,
        failedNotifications,
        retryableNotifications,
        last24Hours,
        now);
  }
}
