# Architecture

## Docker edge ingress (Nginx)

`docker-compose.yml` adds **nginx** (`inventory-nginx`): TLS `:443`, redirect `:80`, upstream `inventory_backend` → `inventory-service`. Published host port for the app is configured via `.env` (e.g. `INVENTORY_HOST_PORT`).

## Integration testing

- **Profile:** `test` with `application-test.yml` (H2 `MODE=PostgreSQL`, Redis/Kafka excluded, rate limiting off).
- **Security slice:** Requests include real JWTs; filters validate signatures against `jwt.secret` in test YAML (`IntegrationTestJwtSupport`).
- **Entry points:** `InventoryApiIntegrationTest` (REST + MockMvc), `InventoryItemServiceImplApplicationTests` (context load).

> Well-structured inventory service with batch tracking, reservations, and movements. 
> 
> **Critical Issues & Inconsistencies:**
> - Uses RabbitMQ (AMQP) while other services use Kafka - MAJOR INCONSISTENCY
> - Dockerfile uses openjdk:17-jdk-slim (Java 17) while build.gradle specifies Java 23 - VERSION MISMATCH
> - docker-compose.yml exists with nginx + observability — reconcile Dockerfile/port/Flyway notes with how you run locally
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
