package io.github.alexisTrejo11.drugstore.notifications.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import io.github.alexisTrejo11.drugstore.notifications.domain.model.NotificationLog;
import io.github.alexisTrejo11.drugstore.notifications.domain.repository.NotificationLogRepository;
import io.github.alexisTrejo11.drugstore.notifications.domain.valueobject.NotificationId;

/**
 * MongoDB implementation of NotificationLogRepository.
 */
@Repository
public class NotificationLogMongoRepository implements NotificationLogRepository {

  private final SpringDataNotificationLogRepository mongoRepository;
  private final NotificationLogDocumentMapper mapper;

  @Autowired
  public NotificationLogMongoRepository(
      SpringDataNotificationLogRepository mongoRepository,
      NotificationLogDocumentMapper mapper) {
    this.mongoRepository = mongoRepository;
    this.mapper = mapper;
  }

  @Override
  public NotificationLog save(NotificationLog log) {
    NotificationLogDocument document = mapper.toDocument(log);
    NotificationLogDocument saved = mongoRepository.save(document);
    return mapper.toDomain(saved);
  }

  @Override
  public Optional<NotificationLog> findById(String id) {
    return mongoRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public List<NotificationLog> findByNotificationId(NotificationId notificationId) {
    return mongoRepository.findByNotificationIdOrderByTimestampDesc(notificationId.value()).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<NotificationLog> findRecentLogs(int limit) {
    int pageSize = Math.max(limit, 1);
    return mongoRepository
        .findAllByOrderByTimestampDesc(PageRequest.of(0, pageSize, Sort.by(Sort.Direction.DESC, "timestamp")))
        .stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public void deleteById(String id) {
    mongoRepository.deleteById(id);
  }
}
