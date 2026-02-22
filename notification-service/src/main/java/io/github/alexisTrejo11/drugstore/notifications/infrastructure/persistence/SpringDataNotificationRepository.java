package io.github.alexisTrejo11.drugstore.notifications.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for NotificationDocument
 */
@Repository
public interface SpringDataNotificationRepository extends MongoRepository<NotificationDocument, String> {

  /**
   * Find notification by event ID
   */
  Optional<NotificationDocument> findByEventId(String eventId);

  /**
   * Find notification by correlation ID
   */
  Optional<NotificationDocument> findByCorrelationId(String correlationId);

  /**
   * Find all notifications for a specific user
   */
  List<NotificationDocument> findByUserId(String userId);

  /**
   * Find all notifications for a specific user with pagination
   */
  Page<NotificationDocument> findByUserId(String userId, Pageable pageable);

  /**
   * Find notifications by status
   */
  List<NotificationDocument> findByStatus(String status);

  /**
   * Find notifications by status with pagination
   */
  Page<NotificationDocument> findByStatus(String status, Pageable pageable);

  /**
   * Find all notifications ordered by created date descending
   */
  Page<NotificationDocument> findAllByOrderByCreatedAtDesc(Pageable pageable);

  /**
   * Find all pending notifications
   */
  @Query("{ 'status': 'PENDING' }")
  List<NotificationDocument> findPendingNotifications();

  /**
   * Find failed notifications that can be retried
   */
  @Query("{ 'status': 'FAILED', 'retryCount': { $lt: ?0 } }")
  List<NotificationDocument> findFailedNotificationsForRetry(int maxRetries);

  /**
   * Find retryable notifications (failed and updated after cutoff time)
   */
  @Query("{ 'status': 'FAILED', 'updatedAt': { $gte: ?0 } }")
  List<NotificationDocument> findRetryableNotifications(LocalDateTime cutoffTime);

  /**
   * Count notifications by status
   */
  long countByStatus(String status);

  /**
   * Count notifications created between two dates
   */
  long countByCreatedAtBetween(LocalDateTime from, LocalDateTime to);
}
