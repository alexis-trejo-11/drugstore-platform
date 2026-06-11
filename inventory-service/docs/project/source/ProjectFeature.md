---
# ProjectFeature[]
features:
  - id: "inventory-management"
    title: "Inventory Management"
    description: "Core inventory tracking with total, available, and reserved quantity management. Supports reorder levels, maximum stock levels, and warehouse location tracking."
    icon: "inventory"
    category: "database"
    status: "stable"
    githubExampleUrl: "PLACEHOLDER"
    highlights:
      - "Track total, available, and reserved quantities"
      - "Reorder level and quantity thresholds"
      - "Warehouse location mapping"
      - "Inventory status management (ACTIVE, INACTIVE, OUT_OF_STOCK)"
      - "CQRS pattern with separate query/command models"
    techStack:
      - "Spring Data JPA"
      - "PostgreSQL"
      - "Flyway (disabled)"
      - "Lombok"
    metrics:
      - label: "Products Tracked"
        value: "PLACEHOLDER"
        trend: "stable"
        icon: "inventory"
      - label: "Avg Query Time"
        value: "PLACEHOLDER"
        trend: "stable"
        icon: "speed"

  - id: "batch-tracking"
    title: "Batch Tracking & Expiration Management"
    description: "Pharmaceutical-grade batch tracking with lot numbers, manufacturing/expiration dates, supplier info, and storage conditions. Supports batch status lifecycle (ACTIVE, EXPIRED, DAMAGED, QUARANTINED)."
    icon: "batch"
    category: "database"
    status: "stable"
    githubExampleUrl: "PLACEHOLDER"
    highlights:
      - "Unique batch numbers per inventory"
      - "Lot number tracking for compliance"
      - "Expiration date monitoring with alerting"
      - "Batch status lifecycle management"
      - "Storage conditions tracking"
      - "Supplier information linkage"
    techStack:
      - "JPA Entity Relationships"
      - "BatchStatus Enum"
      - "ISO DateTime formatting"
      - "Paginated queries"
    metrics:
      - label: "Active Batches"
        value: "PLACEHOLDER"
        trend: "up"
        icon: "batch"
      - label: "Expiring Soon (30d)"
        value: "PLACEHOLDER"
        trend: "stable"
        icon: "warning"

  - id: "stock-reservations"
    title: "Stock Reservation System"
    description: "Temporary stock reservation system for order processing. Supports reserve, confirm, release, and cancel operations with reason tracking."
    icon: "reservation"
    category: "api"
    status: "stable"
    githubExampleUrl: "PLACEHOLDER"
    highlights:
      - "Reserve stock for pending orders"
      - "Confirm reservations upon order confirmation"
      - "Release reservations with reason tracking"
      - "Cancel reservations entirely"
      - "Query active reservations per inventory"
      - "ReservationUseCase port interface"
    techStack:
      - "REST API"
      - "CQRS Commands/Queries"
      - "ResponseWrapper from shared kernel"
      - "ReservationId value object"
    metrics:
      - label: "Active Reservations"
        value: "PLACEHOLDER"
        trend: "stable"
        icon: "lock"
      - label: "Avg Reservation Time"
        value: "PLACEHOLDER"
        trend: "stable"
        icon: "timer"

  - id: "stock-movements"
    title: "Stock Movements (Adjustments & Transfers)"
    description: "Comprehensive stock movement tracking with adjustments (damage, loss, correction) and transfers between inventory locations. Full audit trail via InventoryMovement entities."
    icon: "movement"
    category: "api"
    status: "stable"
    githubExampleUrl: "PLACEHOLDER"
    highlights:
      - "Stock adjustments with reason codes"
      - "Inter-location inventory transfers"
      - "Date-range filtered movement history"
      - "Paginated movement queries"
      - "Audit trail via InventoryMovement entities"
      - "ISO DateTime format support"
    techStack:
      - "Spring MVC"
      - "StockMovementUseCase port"
      - "AdjustmentId value object"
      - "MovementResponse DTO"
    metrics:
      - label: "Daily Movements"
        value: "PLACEHOLDER"
        trend: "up"
        icon: "trending_up"
      - label: "Transfer Operations"
        value: "PLACEHOLDER"
        trend: "stable"
        icon: "swap_horiz"

  - id: "expiration-alerts"
    title: "Expiration Alerts & Batch Lifecycle"
    description: "Proactive expiration monitoring with configurable day thresholds. Batch lifecycle management including mark-as-expired, mark-as-damaged, and quarantine operations."
    icon: "notifications"
    category: "monitoring"
    status: "beta"
    githubExampleUrl: "PLACEHOLDER"
    highlights:
      - "Configurable expiration threshold (default 30 days)"
      - "Expiring batches query endpoint"
      - "Mark batch as expired"
      - "Mark batch as damaged"
      - "Quarantine batch for quality control"
      - "Performed-by user tracking"
    techStack:
      - "Spring Scheduled Tasks (PLACEHOLDER)"
      - "BatchUseCase port"
      - "MarkBatchAsExpiredCommand"
      - "UserId value object"
    metrics:
      - label: "Batches Near Expiry"
        value: "PLACEHOLDER"
        trend: "up"
        icon: "warning"
      - label: "Quarantined Batches"
        value: "PLACEHOLDER"
        trend: "stable"
        icon: "quarantine"

  - id: "rabbitmq-messaging"
    title: "RabbitMQ Event Publishing"
    description: "Asynchronous event publishing via RabbitMQ (AMQP). Publishes inventory events for other services. NOTE: This creates inconsistency as other services use Kafka."
    icon: "rabbitmq"
    category: "messaging"
    status: "beta"
    githubExampleUrl: "PLACEHOLDER"
    highlights:
      - "AMQP messaging with spring-boot-starter-amqp"
      - "Inventory event publishing"
      - "Decoupled service communication"
      - "**WARNING**: Inconsistent with Kafka-using services"
    techStack:
      - "Spring AMQP"
      - "RabbitTemplate"
      - "AMQP Messages"
    metrics:
      - label: "Events Published"
        value: "PLACEHOLDER"
        trend: "stable"
        icon: "send"
      - label: "Message Queue Depth"
        value: "PLACEHOLDER"
        trend: "stable"
        icon: "queue"

  - id: "caching-layer"
    title: "Redis Caching"
    description: "Spring Cache abstraction with Redis backend. 1-hour TTL for general cache with statistics enabled. Used for inventory queries and rate limiting."
    icon: "cache"
    category: "caching"
    status: "stable"
    githubExampleUrl: "PLACEHOLDER"
    highlights:
      - "Redis cache with 1-hour TTL"
      - "Lettuce connection pooling (max 8 active)"
      - "Cache statistics enabled"
      - "Null value caching disabled"
      - "Environment variable configuration"
    techStack:
      - "Spring Cache"
      - "Redis"
      - "Lettuce"
      - "Spring Data Redis"
    metrics:
      - label: "Cache Hit Rate"
        value: "PLACEHOLDER"
        trend: "up"
        icon: "speed"
      - label: "Cached Items"
        value: "PLACEHOLDER"
        trend: "stable"
        icon: "storage"

  - id: "rate-limiting"
    title: "Rate Limiting"
    description: "Global and endpoint-specific rate limiting using custom RateLimit filter. Protects against abuse with configurable request thresholds."
    icon: "speed"
    category: "security"
    status: "stable"
    githubExampleUrl: "PLACEHOLDER"
    highlights:
      - "Global rate limit: 1000 requests/hour"
      - "Default endpoint limit: 100 requests/minute"
      - "Auth endpoints: 10 requests/minute"
      - "Orders endpoint: 50 requests/30 seconds"
      - "Health endpoint: 5 requests/10 seconds"
      - "Redis-backed rate limiter"
    techStack:
      - "Spring Filter"
      - "Redis counters"
      - "libs-kernel RateLimit"
    metrics:
      - label: "Blocked Requests"
        value: "PLACEHOLDER"
        trend: "stable"
        icon: "block"
      - label: "Rate Limit Violations"
        value: "PLACEHOLDER"
        trend: "down"
        icon: "trending_down"

  - id: "actuator-monitoring"
    title: "Spring Boot Actuator & Monitoring"
    description: "Comprehensive monitoring with Actuator endpoints (health, info, metrics, env, prometheus). Exposes Redis, DB, and disk space health indicators."
    icon: "monitoring"
    category: "monitoring"
    status: "stable"
    githubExampleUrl: "PLACEHOLDER"
    highlights:
      - "Health checks for Redis, DB, disk space"
      - "Prometheus metrics endpoint"
      - "Environment info exposure"
      - "CORS enabled for monitoring tools"
      - "Spring Boot Admin client integration"
    techStack:
      - "Spring Boot Actuator"
      - "Prometheus"
      - "Spring Boot Admin"
      - "Micrometer"
    metrics:
      - label: "Health Check Response"
        value: "PLACEHOLDER"
        trend: "stable"
        icon: "health"
      - label: "Exposed Endpoints"
        value: "5"
        trend: "stable"
        icon: "api"

  - id: "flyway-migrations"
    title: "Flyway Database Migrations"
    description: "Database schema versioning with Flyway. Currently disabled in configuration. Supports PostgreSQL with public schema."
    icon: "database"
    category: "database"
    status: "experimental"
    githubExampleUrl: "PLACEHOLDER"
    highlights:
      - "Flyway Core 10.17.0"
      - "PostgreSQL-specific migrations"
      - "Clean disabled: false (dangerous for prod!)"
      - "**WARNING**: Currently disabled in application.yml"
      - "Migration locations: classpath:db/migration"
    techStack:
      - "Flyway Core"
      - "Flyway PostgreSQL"
      - "SQL Migrations"
    metrics:
      - label: "Migration Status"
        value: "DISABLED"
        trend: "stable"
        icon: "warning"
      - label: "Pending Migrations"
        value: "PLACEHOLDER"
        trend: "stable"
        icon: "pending"

  - id: "openapi-docs"
    title: "OpenAPI / Swagger Documentation"
    description: "Auto-generated API documentation with SpringDoc OpenAPI. Exposes Swagger UI and API docs endpoints for easy API exploration."
    icon: "docs"
    category: "api"
    status: "stable"
    githubExampleUrl: "PLACEHOLDER"
    highlights:
      - "Swagger UI at /swagger-ui.html"
      - "API docs at /api-docs"
      - "Alphabetical sorting for tags and operations"
      - "Packages to scan configuration"
      - "Cache disabled for real-time docs"
    techStack:
      - "SpringDoc OpenAPI 2.7.0"
      - "Swagger UI"
      - "OpenAPI 3.0"
    metrics:
      - label: "Documented Endpoints"
        value: "18"
        trend: "stable"
        icon: "description"
      - label: "API Version"
        value: "v2"
        trend: "stable"
        icon: "tag"

  - id: "integration-testing"
    title: "Integration tests (Spring profile test)"
    description: "End-to-end REST tests with H2, real JWT validation (JwtAuthenticationFilter), Redis/Kafka excluded, rate limiting off. InventoryApiIntegrationTest + IntegrationTestJwtSupport."
    icon: "science"
    category: "quality"
    status: "stable"
    githubExampleUrl: "PLACEHOLDER"
    highlights:
      - "Profile test via src/test/resources/application.yml + application-test.yml"
      - "H2 MODE=PostgreSQL; Hibernate ddl-auto create-drop; Flyway disabled"
      - "Bearer JWT built with same jwt.secret as test YAML (no mocked Security)"
      - "MockMvc + @Transactional rollback per test method"
      - "./gradlew test"
    techStack:
      - "JUnit 5"
      - "Spring Boot Test"
      - "MockMvc"
      - "JJWT (test tokens)"
      - "H2"
    metrics:
      - label: "Primary integration class"
        value: "InventoryApiIntegrationTest"
        trend: "stable"
        icon: "integration_instructions"

# Code Snippet Example for Batch Status Lifecycle
  - codeSnippet:
      language: "java"
      filename: "InventoryBatchEntity.java"
      code: |
        @Enumerated(EnumType.STRING)
        @Column(name = "status", nullable = false)
        private BatchStatus status;

        // BatchStatus enum values: ACTIVE, EXPIRED, DAMAGED, QUARANTINED

---
# Project Features

## Integration testing (`test` profile)

Automated API integration tests exercise the HTTP stack end-to-end with a dedicated Spring profile:

| Area | Detail |
|------|--------|
| **Profile** | `test` — activated via `src/test/resources/application.yml` (`spring.profiles.active=test`). |
| **Config** | `src/test/resources/application-test.yml`: H2 in-memory (`MODE=PostgreSQL`), Hibernate `ddl-auto: create-drop`, Flyway disabled for speed and portability. |
| **Authentication** | **Real JWT path**: tokens signed with the same `jwt.secret` as the test YAML are sent as `Authorization: Bearer …`; requests pass through `JwtAuthenticationFilter` and `JwtTokenValidator` (no mocked security filter chain). |
| **Isolation** | Redis and Kafka auto-configuration excluded; `app.rate-limit.global.enabled: false`; Spring cache type `simple`. |
| **Main suite** | `InventoryApiIntegrationTest`: `@SpringBootTest` + `@AutoConfigureMockMvc` + `@Transactional` — inventory create → GET by id/product, PATCH settings, low-stock pagination, and auth behaviour (401 without token, 403 insufficient role, invalid JWT). |
| **JWT helper** | `IntegrationTestJwtSupport` builds HS256 access tokens with claims `userId`, `role`, `type=access`. |
| **Smoke** | `InventoryItemServiceImplApplicationTests` loads the Spring context under `test`. |

Run: `./gradlew test` (from `inventory-service`).

---

> **CRITICAL OBSERVATIONS:**
> 1. **RabbitMQ vs Kafka Inconsistency**: inventory-service uses RabbitMQ (spring-boot-starter-amqp) while all other services (address, auth, cart) use Kafka. This PREVENTS direct messaging integration.
> 2. **Java Version Mismatch**: build.gradle specifies Java 23 but Dockerfile uses openjdk:17-jdk-slim. This will cause `UnsupportedClassVersionError` at runtime.
> 3. **Flyway Disabled**: Database migrations are configured but disabled (`flyway.enabled: false`). This means schema changes won't be tracked or auto-applied.
> 4. **docker-compose.yml**: Present for local stacks (service + Postgres + Redis + nginx + observability); align Dockerfile/Java/Flyway with how you run locally.
> 5. **No gRPC Endpoints**: Other services expose gRPC for inter-service communication, but inventory-service only has REST endpoints.
> 6. **PLACEHOLDER Metrics**: Actual metric values (cache hit rate, response times, etc.) need to be filled in with real monitoring data.
> 7. **Expiration Alerts Status**: Marked as "beta" because the scheduled job to auto-mark expiring batches may not be implemented yet (only manual endpoints exist).
