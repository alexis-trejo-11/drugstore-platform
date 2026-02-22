package io.github.alexisTrejo11.drugstore.notifications.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.github.alexisTrejo11.drugstore.notifications.domain.model.Notification;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationId;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationStatus;

/**
 * Repository interface for Notification aggregate
 */
public interface NotificationRepository {

  Notification save(Notification notification);

  Optional<Notification> findById(NotificationId id);

  Optional<Notification> findByEventId(String eventId);

  Optional<Notification> findByCorrelationId(String correlationId);

  List<Notification> findByUserId(String userId);

  Page<Notification> findByUserId(String userId, Pageable pageable);

  List<Notification> findByStatus(NotificationStatus status);

  Page<Notification> findByStatus(NotificationStatus status, Pageable pageable);

  Page<Notification> findAllByOrderByCreatedAtDesc(Pageable pageable);

  List<Notification> findPendingNotifications();

  List<Notification> findFailedNotificationsForRetry(int maxRetries);

  List<Notification> findRetryableNotifications(LocalDateTime cutoffTime);

  void deleteById(NotificationId id);

  boolean existsById(NotificationId id);

  long count();

  long countByStatus(NotificationStatus status);

  long countByCreatedAtBetween(LocalDateTime from, LocalDateTime to);
}
