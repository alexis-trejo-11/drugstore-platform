---
codeExamples:
  - id: "batch-tracking-entity"
    title: "Inventory Batch Tracking with Expiration"
    description: "JPA entity implementing batch tracking with lot numbers, expiration dates, and status management for pharmaceutical inventory compliance"
    category: "database"
    duration: "PLACEHOLDER"
    views: 0
    tags:
      - "JPA"
      - "Entity"
      - "Batch Tracking"
      - "Pharmaceutical"
    files:
      - name: "InventoryBatchEntity.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/inventories/inventory/adapter/outbound/persistence/model/InventoryBatchEntity.java"
        language: "java"
        content: |
          @Entity
          @Table(name = "inventory_batches")
          @Getter
          @Setter
          @Builder
          @NoArgsConstructor
          @AllArgsConstructor
          public class InventoryBatchEntity {
              @Id
              @Column(name = "id", length = 36)
              private String id;

              @Column(name = "inventory_id", nullable = false, length = 36)
              private String inventoryId;

              @Column(name = "batch_number", nullable = false, unique = true)
              private String batchNumber;

              @Column(name = "lot_number")
              private String lotNumber;

              @Column(name = "quantity", nullable = false)
              private Integer quantity;

              @Column(name = "available_quantity", nullable = false)
              private Integer availableQuantity;

              @Column(name = "expiration_date", nullable = false)
              private LocalDateTime expirationDate;

              @Enumerated(EnumType.STRING)
              @Column(name = "status", nullable = false)
              private BatchStatus status;

              @Column(name = "storage_conditions")
              private String storageConditions;

              @Column(name = "received_date")
              private LocalDateTime receivedDate;

              @PrePersist
              protected void onCreate() {
                  if (createdAt == null) {
                      createdAt = LocalDateTime.now();
                  }
                  if (updatedAt == null) {
                      updatedAt = LocalDateTime.now();
                  }
              }
          }
        highlighted: true
        explanation: "Entity with batch_number unique constraint, expiration_date tracking, and BatchStatus enum for lifecycle management (ACTIVE, EXPIRED, DAMAGED, QUARANTINED)"

  - id: "inventory-reservation"
    title: "Stock Reservation System"
    description: "REST controller implementing stock reservation pattern for order processing with confirm, release, and cancel operations"
    category: "api"
    duration: "PLACEHOLDER"
    views: 0
    tags:
      - "REST API"
      - "Reservation Pattern"
      - "Order Processing"
      - "CQRS"
    files:
      - name: "InventoryReservationController.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/inventories/inventory/adapter/inbound/api/rest/controller/InventoryReservationController.java"
        language: "java"
        content: |
          @RestController
          @RequestMapping("/api/v2/inventories")
          @RequiredArgsConstructor
          public class InventoryReservationController {
              private final ReservationUseCase reservationUseCase;
              private final ResponseMapper<ReservationResponse, StockReservation> responseMapper;

              @PostMapping("/{inventoryId}/stock/reservations")
              public ResponseEntity<ResponseWrapper<ReservationId>> reserveStock(
                      @PathVariable String inventoryId,
                      @Valid @RequestBody ReserveStockRequest request) {
                  ReservationId reservationId = reservationUseCase.reserveStock(request.toCommand(inventoryId));
                  return ResponseEntity.status(HttpStatus.CREATED)
                      .body(ResponseWrapper.created(reservationId, "Stock Reservation"));
              }

              @PatchMapping("/stock/reservations/{reservationId}/confirm")
              public ResponseWrapper<Void> confirmReservation(@PathVariable String reservationId) {
                  var command = new ConfirmReservationCommand(ReservationId.of(reservationId), UserId.of("system"));
                  reservationUseCase.confirmReservation(command);
                  return ResponseWrapper.updated(null, "Reservation confirmed");
              }

              @PatchMapping("/reservations/{reservationId}/stock/release")
              public ResponseWrapper<Void> releaseReservation(@PathVariable String reservationId,
                                                              @RequestParam(required = false) String reason) {
                  String reasonValue = (reason != null) ? reason : "Released by system";
                  var command = new ReleaseReservationCommand(ReservationId.of(reservationId), reasonValue);
                  reservationUseCase.releaseReservation(command);
                  return ResponseWrapper.updated(null, "Reservation released");
              }

              @DeleteMapping("reservations/{reservationId}")
              public ResponseWrapper<Void> cancelReservation(@PathVariable String reservationId) {
                  reservationUseCase.cancelReservation(ReservationId.of(reservationId));
                  return ResponseWrapper.deleted(null, "Reservation");
              }
          }
        highlighted: true
        explanation: "REST API with reserve, confirm, release, and cancel operations. Uses ReservationUseCase port and ResponseWrapper from shared kernel."

  - id: "stock-movements"
    title: "Inventory Stock Movements (Adjust & Transfer)"
    description: "Controller handling inventory adjustments and transfers between locations with audit trail via InventoryMovement entities"
    category: "api"
    duration: "PLACEHOLDER"
    views: 0
    tags:
      - "REST API"
      - "Stock Movement"
      - "Audit Trail"
      - "Transfer"
    files:
      - name: "InventoryStockMovementController.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/inventories/inventory/adapter/inbound/api/rest/controller/InventoryStockMovementController.java"
        language: "java"
        content: |
          @RestController
          @RequestMapping("/api/v2/inventories")
          @RequiredArgsConstructor
          public class InventoryStockMovementController {
              private final StockMovementUseCase stockMovementUseCase;
              private final ResponseMapper<MovementResponse, InventoryMovement> responseMapper;

              @PostMapping("/{inventoryId}/stocks-movements/adjust")
              public ResponseEntity<ResponseWrapper<AdjustmentId>> adjustInventory(
                      @PathVariable String inventoryId,
                      @Valid @RequestBody AdjustInventoryRequest request) {
                  AdjustmentId adjustmentId = stockMovementUseCase.adjustInventory(request.toCommand(inventoryId));
                  return ResponseEntity.status(HttpStatus.CREATED)
                      .body(ResponseWrapper.created(adjustmentId, "Stock Adjustment"));
              }

              @PostMapping("/{sourceInventoryId}/stocks/movements/transfer")
              public ResponseWrapper<Void> transferInventory(
                      @PathVariable String sourceInventoryId,
                      @Valid @RequestBody TransferInventoryRequest request) {
                  stockMovementUseCase.transferInventory(request.toCommand(sourceInventoryId));
                  return ResponseWrapper.success("Inventory transferred successfully");
              }

              @GetMapping("/{inventoryId}/stocks/movements")
              public ResponseWrapper<PageResponse<MovementResponse>> getInventoryMovements(
                      @PathVariable String inventoryId,
                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
                  var query = GetInventoryMovementsQuery.of(inventoryId, startDate, endDate);
                  Page<InventoryMovement> movements = stockMovementUseCase.getInventoryMovements(query);
                  PageResponse<MovementResponse> movementResponses = responseMapper.toResponsePage(movements);
                  return ResponseWrapper.found(movementResponses, "Inventory Movements");
              }
          }
        highlighted: true
        explanation: "Supports stock adjustments (with reason tracking) and transfers between inventory locations. Movements query supports date range filtering with ISO DateTime format."

  - id: "rabbitmq-integration"
    title: "RabbitMQ Messaging Integration"
    description: "Inventory service uses RabbitMQ (AMQP) for async messaging, unlike other services that use Kafka - creates platform inconsistency"
    category: "messaging"
    duration: "PLACEHOLDER"
    views: 0
    tags:
      - "RabbitMQ"
      - "AMQP"
      - "Messaging"
      - "Inconsistency"
    files:
      - name: "build.gradle (RabbitMQ dependency)"
        path: "build.gradle"
        language: "groovy"
        content: |
          dependencies {
              // RabbitMQ
              implementation 'org.springframework.boot:spring-boot-starter-amqp'
          }
        highlighted: true
        explanation: "**CRITICAL**: Uses spring-boot-starter-amqp for RabbitMQ while other services (address, auth, cart) use spring-kafka. This inconsistency prevents seamless inter-service communication."

  - id: "redis-caching-config"
    title: "Redis Cache Configuration"
    description: "Spring Boot cache abstraction with Redis backend, 1-hour TTL for general cache, lettuce connection pool"
    category: "caching"
    duration: "PLACEHOLDER"
    views: 0
    tags:
      - "Redis"
      - "Cache"
      - "Spring Boot"
      - "Lettuce"
    files:
      - name: "application.yml (Redis config)"
        path: "src/main/resources/application.yml"
        language: "yaml"
        content: |
          spring:
            data:
              redis:
                host: ${SPRING_REDIS_HOST:localhost}
                port: ${SPRING_REDIS_PORT:6379}
                password: ${SPRING_REDIS_PASSWORD:redispass}
                timeout: 2000ms
                lettuce:
                  pool:
                    max-active: 8
                    max-idle: 8
                    min-idle: 0
                    max-wait: 1000ms

            cache:
              type: redis
              redis:
                time-to-live: 3600000  # 1 hour
                cache-null-values: false
                enable-statistics: true
        highlighted: true
        explanation: "Redis configured with environment variable support, lettuce pool sizing, and 1-hour TTL. Cache statistics enabled for monitoring."
---
# CodeShowCase

> **OBSERVATIONS:**
> 1. **Batch Tracking**: `InventoryBatchEntity` provides pharmaceutical-grade batch tracking with lot numbers, expiration dates, and status (ACTIVE, EXPIRED, DAMAGED, QUARANTINED). This is critical for drugstore compliance.
> 2. **Reservation Pattern**: The reservation system allows temporary stock reservation during order processing with confirm/release lifecycle - essential for e-commerce scenarios.
> 3. **Stock Movements**: Adjustments track reasons (damage, loss, correction) while transfers move stock between inventory locations. Both create audit trails via `InventoryMovement` entities.
> 4. **RabbitMQ Inconsistency**: Using RabbitMQ while other services use Kafka is a **major architectural inconsistency**. Consider migrating to Kafka for platform uniformity.
> 5. **Missing gRPC**: Unlike address-service, auth-service, and cart-service, inventory-service doesn't expose gRPC endpoints for inter-service communication.
> 6. **CQRS Pattern**: Controllers use command/query separation (e.g., `GetInventoryMovementsQuery`, `AdjustInventoryRequest.toCommand()`).
> 7. **Integration tests**: `InventoryApiIntegrationTest` drives REST APIs through MockMvc under profile `test` (H2, real JWT validation via `IntegrationTestJwtSupport`). See `docs/project/generated/ProjectFeature.md`.
