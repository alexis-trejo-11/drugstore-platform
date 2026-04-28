package io.github.alexisTrejo11.drugstore.notifications.infrastructure.presentation.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.alexisTrejo11.drugstore.notifications.application.NotificationMonitoringService;
import io.github.alexisTrejo11.drugstore.notifications.application.dto.NotificationMetrics;
import io.github.alexisTrejo11.drugstore.notifications.application.dto.NotificationResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import libs_kernel.response.ResponseWrapper;

@RestController
@RequestMapping("/api/v1/notifications/monitoring")
public class NotificationMonitoringController {
  private final NotificationMonitoringService monitoringService;

  @Autowired
  public NotificationMonitoringController(NotificationMonitoringService monitoringService) {
    this.monitoringService = monitoringService;
  }

  /**
   * Get a specific notification by ID
   */
  @GetMapping("/{notificationId}")
  public ResponseWrapper<NotificationResponse> getNotification(
      @PathVariable @Valid @NotBlank String notificationId) {
    var notification = monitoringService.getNotificationById(notificationId);
    var response = NotificationResponse.fromDomain(notification);
    return ResponseWrapper.found(response, "Notification");
  }

  /**
   * Get recent notifications with pagination
   */
  @GetMapping("/recent")
  public ResponseWrapper<Page<NotificationResponse>> getRecentNotifications(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "DESC") String direction) {
    Sort.Direction sortDirection = Sort.Direction.fromString(direction);
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

    Page<NotificationResponse> notifications = monitoringService
        .getRecentNotification(pageable)
        .map(NotificationResponse::fromDomain);

    return ResponseWrapper.found(notifications, "Recent Notifications");
  }

  /**
   * Get notifications for a specific user
   */
  @GetMapping("/user/{userId}")
  public ResponseWrapper<Page<NotificationResponse>> getUserNotifications(
      @PathVariable @Valid @NotBlank String userId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "DESC") String direction) {
    Sort.Direction sortDirection = Sort.Direction.fromString(direction);
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

    Page<NotificationResponse> notifications = monitoringService
        .getNotificationsByUserId(userId, pageable)
        .map(NotificationResponse::fromDomain);

    return ResponseWrapper.found(notifications, "Notifications by User");
  }

  /**
   * Get notifications by status
   */
  @GetMapping("/status/{status}")
  public ResponseWrapper<Page<NotificationResponse>> getNotificationsByStatus(
      @PathVariable @Valid @NotBlank String status,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "DESC") String direction) {
    Sort.Direction sortDirection = Sort.Direction.fromString(direction);
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

    Page<NotificationResponse> notifications = monitoringService
        .getNotificationsByStatus(status, pageable)
        .map(NotificationResponse::fromDomain);

    return ResponseWrapper.found(notifications, "Notifications by Status");
  }

  /**
   * Get notification by correlation ID
   */
  @GetMapping("/correlation/{correlationId}")
  public ResponseWrapper<NotificationResponse> getNotificationByCorrelationId(
      @PathVariable @Valid @NotBlank String correlationId) {
    var notification = monitoringService.getNotificationByCorrelationId(correlationId);
    var response = NotificationResponse.fromDomain(notification);
    return ResponseWrapper.found(response, "Notification");
  }

  /**
   * Get notification by event ID
   */
  @GetMapping("/event/{eventId}")
  public ResponseWrapper<NotificationResponse> getNotificationByEventId(
      @PathVariable @Valid @NotBlank String eventId) {
    var notification = monitoringService.getNotificationByEventId(eventId);
    var response = NotificationResponse.fromDomain(notification);
    return ResponseWrapper.found(response, "Notification");
  }

  /**
   * Get notification service metrics
   */
  @GetMapping("/metrics")
  public ResponseWrapper<NotificationMetrics> getNotificationMetrics() {
    NotificationMetrics metrics = monitoringService.getNotificationMetrics();
    return ResponseWrapper.found(metrics, "Notification Metrics");
  }
}
