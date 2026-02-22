package io.github.alexisTrejo11.drugstore.notifications.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import io.github.alexisTrejo11.drugstore.notifications.domain.model.Notification;
import io.github.alexisTrejo11.drugstore.notifications.domain.repository.NotificationRepository;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationId;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationStatus;

/**
 * MongoDB implementation of NotificationRepository
 */
@Repository
public class NotificationMongoRepository implements NotificationRepository {

  private final SpringDataNotificationRepository mongoRepository;
  private final NotificationDocumentMapper mapper;

  @Autowired
  public NotificationMongoRepository(
      SpringDataNotificationRepository mongoRepository,
      NotificationDocumentMapper mapper) {
    this.mongoRepository = mongoRepository;
    this.mapper = mapper;
  }

  @Override
  public Notification save(Notification notification) {
    NotificationDocument document = mapper.toDocument(notification);
    NotificationDocument savedDocument = mongoRepository.save(document);
    return mapper.toDomain(savedDocument);
  }

  @Override
  public Optional<Notification> findById(NotificationId id) {
    return mongoRepository.findById(id.value())
        .map(mapper::toDomain);
  }

  @Override
  public List<Notification> findByUserId(String userId) {
    return mongoRepository.findByUserId(userId).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<Notification> findByStatus(NotificationStatus status) {
    return mongoRepository.findByStatus(status.name()).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<Notification> findPendingNotifications() {
    return mongoRepository.findPendingNotifications().stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<Notification> findFailedNotificationsForRetry(int maxRetries) {
    return mongoRepository.findFailedNotificationsForRetry(maxRetries).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public void deleteById(NotificationId id) {
    mongoRepository.deleteById(id.value());
  }

  @Override
  public boolean existsById(NotificationId id) {
    return mongoRepository.existsById(id.value());
  }

  @Override
  public Optional<Notification> findByEventId(String eventId) {
    return mongoRepository.findByEventId(eventId)
        .map(mapper::toDomain);
  }

  @Override
  public Optional<Notification> findByCorrelationId(String correlationId) {
    return mongoRepository.findByCorrelationId(correlationId)
        .map(mapper::toDomain);
  }

  @Override
  public Page<Notification> findByUserId(String userId, Pageable pageable) {
    return mongoRepository.findByUserId(userId, pageable)
        .map(mapper::toDomain);
  }

  @Override
  public Page<Notification> findByStatus(NotificationStatus status, Pageable pageable) {
    return mongoRepository.findByStatus(status.name(), pageable)
        .map(mapper::toDomain);
  }

  @Override
  public Page<Notification> findAllByOrderByCreatedAtDesc(Pageable pageable) {
    return mongoRepository.findAllByOrderByCreatedAtDesc(pageable)
        .map(mapper::toDomain);
  }

  @Override
  public List<Notification> findRetryableNotifications(LocalDateTime cutoffTime) {
    return mongoRepository.findRetryableNotifications(cutoffTime).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public long count() {
    return mongoRepository.count();
  }

  @Override
  public long countByStatus(NotificationStatus status) {
    return mongoRepository.countByStatus(status.name());
  }

  @Override
  public long countByCreatedAtBetween(LocalDateTime from, LocalDateTime to) {
    return mongoRepository.countByCreatedAtBetween(from, to);
  }
}
