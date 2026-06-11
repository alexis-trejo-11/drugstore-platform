---
# ArchitectureLayer[]
layers:
  - name: "Controllers (Inbound Adapters)"
    description: "REST API layer for inventory management, reservations, and stock movements"
    color: "#4CAF50"
    expanded: true
    components:
      - "InventoryController - Core inventory operations (get by id/product, low-stock, create)"
      - "InventoryReservationController - Stock reservation management (reserve, confirm, release, cancel)"
      - "InventoryStockMovementController - Stock movements (adjust, transfer, get movements)"
      - "InventoryBatchController - Batch management (PLACEHOLDER: check if exists)"
    responsibilities:
      - "REST API request handling"
      - "Request mapping to commands/queries"
      - "Response wrapping with ResponseWrapper from libs-kernel"
      - "Jakarta Validation on DTOs"
      - "Integration-tested via MockMvc + JWT (profile test)"
    technologies:
      - "Spring Web MVC"
      - "libs-kernel (shared ResponseWrapper, mappers)"
      - "SpringDoc OpenAPI 2.7.0"

  - name: "Application Layer (Use Cases)"
    description: "Inventory, Stock, and Alert use cases coordinating domain logic"
    color: "#2196F3"
    expanded: true
    components:
      - "InventoryService - Core inventory operations (get, create, low-stock checks)"
      - "StockMovementUseCase - Stock adjustments and transfers"
      - "ReservationUseCase - Stock reservation management"
      - "InventoryAlertUseCase - Low-stock alert management"
      - "Command/Query Objects: CreateInventoryCommand, AdjustInventoryCommand, TransferInventoryCommand, ReserveStockCommand, ConfirmReservationCommand, ReleaseReservationCommand"
    responsibilities:
      - "Command/Query coordination"
      - "Transaction management"
      - "Business rule enforcement"
    technologies:
      - "Spring Service"
      - "CQS Pattern"
      - "Domain-Driven Design"

  - name: "Domain Layer"
    description: "Core business logic with Inventory, InventoryMovement, StockReservation, InventoryAlert entities"
    color: "#FF9800"
    expanded: false
    components:
      - "Inventory - Core entity with total/available/reserved quantities, reorder levels"
      - "InventoryBatch - Batch tracking with lot numbers, expiration dates, supplier info"
      - "InventoryMovement - Audit trail for stock adjustments and transfers"
      - "StockReservation - Temporary stock reservations for order processing"
      - "InventoryAlert - Low-stock alerts with severity levels"
      - "Value Objects: InventoryId, BatchId, MovementId, ReservationId, AlertId"
      - "Enums: InventoryStatus, BatchStatus, MovementType, ReservationStatus, AlertSeverity, AlertType, AlertStatus"
    responsibilities:
      - "Business rule enforcement (reorder levels, quantity validation)"
      - "Batch expiration tracking"
      - "Reservation conflict detection"
      - "Alert generation"
    technologies:
      - "DDD Domain Model"
      - "Value Objects Pattern"
      - "Enum Types"

  - name: "Output Adapters (Infrastructure)"
    description: "Adapters implementing outbound ports: persistence, messaging, caching"
    color: "#9C27B0"
    expanded: false
    components:
      - "JpaInventoryRepository - Spring Data JPA for InventoryEntity"
      - "JpaInventoryBatchRepository - Batch management"
      - "JpaInventoryMovementRepository - Stock movement tracking"
      - "JpaStockReservationRepository - Reservation persistence"
      - "JpaInventoryAlertRepository - Alert management"
      - "InventoryJpaEntityMapper - Domain to JPA entity mapping"
      - "RabbitMQ Publisher - Inventory events to message queue (PLACEHOLDER: verify implementation)"
    responsibilities:
      - "JPA persistence with PostgreSQL"
      - "Flyway database migrations"
      - "Redis caching for inventory lookups"
      - "RabbitMQ messaging (AMQP)"
    technologies:
      - "Spring Data JPA"
      - "PostgreSQL 15"
      - "Flyway Migrations 10.17.0"
      - "Spring Data Redis"
      - "RabbitMQ/AMQP"

  - name: "Configuration Layer"
    description: "Cross-cutting concerns: caching, rate limiting, messaging, OpenAPI"
    color: "#F44336"
    expanded: false
    components:
      - "CacheConfig - Spring Cache with Redis"
      - "RateLimitAspect - AOP aspect for rate limiting (from libs-kernel)"
      - "RabbitMQ Config - AMQP configuration (PLACEHOLDER: verify)"
      - "OpenApiConfig - Swagger/OpenAPI documentation (2.7.0)"
      - "GlobalExceptionHandler - Centralized exception handling"
      - "JacksonConfig - JSON serialization config"
      - "MDCFilter - MDC logging filter"
    responsibilities:
      - "Cache configuration"
      - "Rate limiting enforcement"
      - "RabbitMQ connection management"
      - "API documentation generation"
      - "Global exception handling"
    technologies:
      - "Spring Cache"
      - "Spring AOP"
      - "Spring AMQP (RabbitMQ)"
      - "SpringDoc OpenAPI 2.7.0"
      - "Logstash Logback Encoder 7.4"

# DesignPattern[]
designPatterns:
  - title: "CQS Pattern"
    emoji: "📋"
    description: "Separate command (write) and query (read) operations with dedicated use cases and request objects"
    category: "Architectural"
    badge: "CQS"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/inventory-service/src/main/java/io/github/alexisTrejo11/drugstore/inventories/inventory/adapter/inbound/api/rest/controller/InventoryController.java"

  - title: "Value Object Pattern"
    emoji: "🧱"
    description: "InventoryId, BatchId, MovementId, ReservationId, AlertId as strongly-typed value objects"
    category: "Domain"
    badge: "DDD"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/inventory-service/src/main/java/io/github/alexisTrejo11/drugstore/inventories/inventory/core/inventory/domain/entity/valueobject"

  - title: "Repository Pattern"
    emoji: "🗄️"
    description: "JPA repositories for Inventory, Batch, Movement, Reservation, Alert entities"
    category: "Structural"
    badge: "Persistence"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/inventory-service/src/main/java/io/github/alexisTrejo11/drugstore/inventories/inventory/adapter/outbound/persistence/repository/jpa"

  - title: "Entity Pattern with Auditing"
    emoji: "📦"
    description: "JPA entities with createdAt, updatedAt fields using @PrePersist/@PreUpdate"
    category: "Persistence"
    badge: "JPA"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/inventory-service/src/main/java/io/github/alexisTrejo11/drugstore/inventories/inventory/adapter/outbound/persistence/model/InventoryEntity.java"

  - title: "Reservation Pattern"
    emoji: "🔒"
    description: "StockReservation entity prevents overselling by reserving stock during order processing"
    category: "Business Logic"
    badge: "Inventory"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/inventory-service/src/main/java/io/github/alexisTrejo11/drugstore/inventories/inventory/core/stock/domain/entity/StockReservation.java"

  - title: "Movement Tracking Pattern"
    emoji: "📈"
    description: "InventoryMovementEntity tracks all stock adjustments and transfers for audit trail"
    category: "Audit"
    badge: "Tracking"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/inventory-service/src/main/java/io/github/alexisTrejo11/drugstore/inventories/inventory/adapter/outbound/persistence/model/InventoryMovementEntity.java"

# StrategyItem[] - Scalability
scalabilityStrategies:
  - title: "Redis Caching"
    description: "Spring Cache with Redis for frequently accessed inventory data"
  - title: "Pagination Support"
    description: "Endpoints support Pageable for efficient large dataset handling"
  - title: "Batch Processing"
    description: "Inventory batches allow granular stock tracking and expiration management"

# StrategyItem[] - Security
securityStrategies:
  - title: "PLACEHOLDER: JWT Authentication"
    description: "PLACEHOLDER: Security config not visible in scanned files - assumed JWT filter from libs-kernel"
  - title: "Rate Limiting"
    description: "GlobalRateLimitFilter and RateLimitAspect from libs-kernel for API protection"
  - title: "Input Validation"
    description: "Jakarta Validation on all request DTOs"

# CacheStrategy[]
cacheStrategies:
  - name: "Redis Inventory Cache"
    description: "Spring Cache with Redis for inventory lookups"
    ttl: "PLACEHOLDER: Check CacheConfig for TTL"
    coverage: "Inventory queries (PLACEHOLDER: @Cacheable annotations needed)"
  - name: "Redis Rate Limit Cache"
    description: "Rate limit counters in Redis with configurable profiles"
    ttl: "60s (default)"
    coverage: "All endpoints with rate limiting"

# ArchitectureFeature[]
architectureFeatures:
  - title: "Batch Tracking with Expiration"
    emoji: "📦"
    description: "InventoryBatchEntity tracks lot numbers, manufacturing/expiration dates, supplier info"
  - title: "Stock Reservation System"
    emoji: "🔒"
    description: "Prevents overselling by reserving stock during order processing with automatic release"
  - title: "Movement Audit Trail"
    emoji: "📈"
    description: "All stock adjustments and transfers tracked via InventoryMovementEntity"
  - title: "Low-Stock Alerts"
    emoji: "⚠️"
    description: "InventoryAlertEntity generates alerts when stock falls below reorder level"
  - title: "RabbitMQ Messaging"
    emoji: "🐇"
    description: "Publishes inventory events to RabbitMQ (inconsistency: other services use Kafka)"

# ArchitectureDiagramModel
architectureDiagram:
  legendItems:
    - type: "client"
      label: "Client"
      color: "#4CAF50"
      icon: "user"
    - type: "gateway"
      label: "Nginx"
      color: "#009688"
      icon: "nginx"
    - type: "service"
      label: "Inventory Service"
      color: "#2196F3"
      icon: "spring"
    - type: "database"
      label: "PostgreSQL"
      color: "#9C27B0"
      icon: "database"
    - type: "queue"
      label: "RabbitMQ"
      color: "#FF9800"
      icon: "rabbitmq"
    - type: "cache"
      label: "Redis"
      color: "#F44336"
      icon: "redis"
    - type: "monitoring"
      label: "Actuator"
      color: "#607D8B"
      icon: "health"

  nodes:
    - id: "client"
      label: "Frontend/Order Service"
      type: "client"
      x: 100
      y: 100
      connections: ["nginx"]
      status: "healthy"
      traffic: 130

    - id: "nginx"
      label: "Nginx (TLS + LB)"
      type: "gateway"
      x: 280
      y: 100
      connections: ["inventory-service"]
      status: "healthy"
      traffic: 120

    - id: "inventory-service"
      label: "Inventory Service"
      type: "service"
      x: 480
      y: 100
      connections: ["postgres", "rabbitmq", "redis", "actuator"]
      status: "healthy"
      traffic: 100

    - id: "postgres"
      label: "PostgreSQL 15"
      type: "database"
      x: 250
      y: 250
      connections: []
      status: "healthy"
      traffic: 70

    - id: "rabbitmq"
      label: "RabbitMQ"
      type: "queue"
      x: 550
      y: 250
      connections: []
      status: "healthy"
      traffic: 40

    - id: "redis"
      label: "Redis"
      type: "cache"
      x: 400
      y: 250
      connections: []
      status: "healthy"
      traffic: 30

    - id: "actuator"
      label: "Actuator"
      type: "monitoring"
      x: 400
      y: 400
      connections: []
      status: "healthy"
      traffic: 5

  connections:
    - id: "conn1"
      from: "client"
      to: "nginx"
      label: "HTTPS :443"
      protocol: "HTTPS"
      isActive: true
    - id: "conn1b"
      from: "nginx"
      to: "inventory-service"
      label: "HTTP :8080 (internal)"
      protocol: "HTTP"
      isActive: true
    - id: "conn2"
      from: "inventory-service"
      to: "postgres"
      label: "JDBC"
      protocol: "TCP"
      isActive: true
    - id: "conn3"
      from: "inventory-service"
      to: "rabbitmq"
      label: "AMQP"
      protocol: "AMQP"
      isActive: true
    - id: "conn4"
      from: "inventory-service"
      to: "redis"
      label: "Cache/Rate Limit"
      protocol: "RESP"
      isActive: true
    - id: "conn5"
      from: "inventory-service"
      to: "actuator"
      label: "Health Checks"
      protocol: "HTTP"
      isActive: true

# DataFlowModel
dataFlow:
  requestFlow:
    - number: 1
      title: "Client Request"
      description: "Order Service sends request to reserve stock at /api/v2/inventories/{id}/stock/reservations"
      icon: "send"
    - number: 2
      title: "Rate Limit Check"
      description: "GlobalRateLimitFilter checks Redis for rate limit compliance"
      icon: "speed"
    - number: 3
      title: "Controller Handling"
      description: "InventoryReservationController processes reservation request"
      icon: "controller"
    - number: 4
      title: "Use Case Execution"
      description: "ReservationUseCase reserves stock, checks availability"
      icon: "gear"
    - number: 5
      title: "Data Persistence"
      description: "StockReservation saved to PostgreSQL via JpaStockReservationRepository"
      icon: "database"
    - number: 6
      title: "Event Publishing"
      description: "Inventory event published to RabbitMQ (PLACEHOLDER: verify)"
      icon: "rabbitmq"
    - number: 7
      title: "Response"
      description: "ReservationId returned to client"
      icon: "check"

  eventFlow:
    - number: 1
      title: "Inventory Created"
      description: "When new inventory created, event published to RabbitMQ"
      icon: "event"
    - number: 2
      title: "Stock Adjusted"
      description: "When stock adjusted, InventoryMovement tracked, event published"
      icon: "event"
    - number: 3
      title: "Low-Stock Alert"
      description: "When stock falls below reorder level, InventoryAlert created"
      icon: "alert"

# TechDecisionsModel
techDecisions:
  - title: "RabbitMQ vs Kafka"
    problem: "Need asynchronous messaging for inventory events to other services"
    solution: "Used RabbitMQ (AMQP) instead of Kafka used by other services - INCONSISTENCY"
    outcome: "Works for messaging but creates inconsistency across platform (mix of Kafka and RabbitMQ)"
    icon: "messaging"
    alternatives:
      - "Kafka (consistent with other services)"
      - "REST calls (tight coupling)"
      - "No messaging (other services won't know about inventory changes)"

  - title: "Reservation Pattern for Oversell Prevention"
    problem: "Need to prevent overselling when multiple orders compete for same stock"
    solution: "StockReservation entity reserves stock during order processing with confirm/release lifecycle"
    outcome: "Prevents overselling, allows temporary holds, automatic release on timeout"
    icon: "reservation"
    alternatives:
      - "No reservations (risk of overselling)"
      - "Pessimistic locking (performance impact)"
      - "Optimistic locking (complex conflict resolution)"

  - title: "Batch Tracking for Expiration Management"
    problem: "Pharmacy inventory requires tracking lot numbers and expiration dates for compliance"
    solution: "InventoryBatchEntity with lot number, manufacturing/expiration dates, supplier info"
    outcome: "Granular stock tracking, expiration alerts, supplier traceability"
    icon: "batch"
    alternatives:
      - "No batch tracking (less compliance)"
      - "Separate batch table only (more complex queries)"
      - "Embed batch data in inventory (less flexible)"

  - title: "Movement Audit Trail"
    problem: "Need to track all inventory adjustments and transfers for audit and compliance"
    solution: "InventoryMovementEntity tracks all stock movements with type, quantity, reason, timestamp"
    outcome: "Complete audit trail, supports reconciliation, compliance reporting"
    icon: "audit"
    alternatives:
      - "No movement tracking (no audit trail)"
      - "Log files only (harder to query)"
      - "Update inventory directly (no history)"
---
# Architecture

## Integration testing

- **Profile:** `test` with `application-test.yml` (H2 `MODE=PostgreSQL`, Redis/Kafka excluded, rate limiting off).
- **Security slice:** Requests include real JWTs; filters validate signatures against `jwt.secret` in test YAML (`IntegrationTestJwtSupport`).
- **Entry points:** `InventoryApiIntegrationTest` (REST + MockMvc), `InventoryItemServiceImplApplicationTests` (context load).

> Well-structured inventory service with batch tracking, reservations, and movements. 
> 
> **Critical Issues & Inconsistencies:**
> - Uses RabbitMQ (AMQP) while other services use Kafka - MAJOR INCONSISTENCY
> - Dockerfile uses openjdk:17-jdk-slim (Java 17) while build.gradle specifies Java 23 - VERSION MISMATCH
> - docker-compose.yml exists with nginx + observability — reconcile Dockerfile/port/Flyway with local runtime
> - Broader **unit** test coverage beyond integration/API smoke paths is still an improvement area
> - PLACEHOLDER: Security config not scanned (JWT filter assumed)
> - PLACEHOLDER: RabbitMQ configuration needs verification
> 
> **Missing:**
> - Kubernetes manifests
> - CI/CD pipeline
> - Micrometer metrics for inventory operations
> - Circuit Breaker for external calls
> - @Cacheable annotations on repository methods
