package io.github.alexisTrejo11.drugstore.notifications.infrastructure.persistence;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for NotificationLogDocument.
 */
@Repository
public interface SpringDataNotificationLogRepository extends MongoRepository<NotificationLogDocument, String> {

  List<NotificationLogDocument> findByNotificationIdOrderByTimestampDesc(String notificationId);

  List<NotificationLogDocument> findAllByOrderByTimestampDesc(Pageable pageable);
}
