---
projectId: "drugstore-platform"
featured: true
name: "Drugstore Platform"
language: "Java"
category: "microservice-platform"
framework: "Spring Boot"
version: "1.0"
repositoryUrl: "https://github.com/alexisTrejo11/drugstore-platform"
liveDemoUrl: null
description: |-
  Merged metadata descriptions from all Drugstore Platform services.
  
  # address-service
  Microservice for managing user addresses in a drugstore e-commerce platform. Supports multi-country postal code validation, role-based access control (CUSTOMER/EMPLOYEE), default address management, and rate limiting via Redis.
  
  # auth-service
  Authentication and authorization microservice for the drugstore platform. Handles user registration (Customer/Employee/Admin), JWT token management (access + refresh), password reset flows, two-factor authentication via TOTP, OAuth2 social login, and publishes user events to Kafka for inter-service communication.
  
  # cart-service
  Shopping cart microservice for the drugstore platform implementing Domain-Driven Design with Cart aggregate root. Supports cart items management, afterwards items (save-for-later), gRPC endpoints for inter-service communication, Kafka event consumption for product updates, Redis caching, and comprehensive validation.
  
  # employee-service
  Employee management microservice for the drugstore platform. Manages employee data including personal info, workday schedules (JSONB), certifications, compensation, employment status (ACTIVE, INACTIVE, ON_LEAVE, SUSPENDED, TERMINATED), and role-based access (PHARMACIST, PHARMACY_TECHNICIAN, STORE_MANAGER, etc.).
  
  # inventory-service
  Inventory management microservice for the drugstore platform. Manages product inventory with batch tracking (lot numbers, expiration dates), stock reservations for order processing, inventory movements (adjustments, transfers), low-stock alerts, and RabbitMQ messaging for inventory events. Includes Spring Boot integration tests (profile test, H2, real JWT).
  
  # order-service
  A microservice responsible for complete order lifecycle management in a drugstore platform, implementing DDD and hexagonal architecture with support for multiple delivery methods including store pickup and home delivery.
  
  # payment-service
  A microservice responsible for payment processing and sales management in a drugstore platform, implementing DDD with Stripe integration for payment gateway operations including refunds, webhooks, and sales generation from completed payments.
  
  # product-service
  Microservice that owns product catalog CRUD/search, enforces domain validations, and publishes product lifecycle events.
  
  # store-service
  Hexagonal Spring Boot microservice for drugstore store catalog: CRUD-style commands, search and pagination, Redis caching, JWT security, Flyway/PostgreSQL, optional Kafka hook (stub), Docker Compose with Nginx, Prometheus, Loki, and Grafana.
  
  # user-service
  User and profile microservice: JWT REST API (/api/v2/users, manager lifecycle, profile), Redis, PostgreSQL, Kafka consumers for user.* topics, protobuf/gRPC stubs and UserGrpcServer implementation (server bootstrap not wired). Observability via Actuator, Prometheus registry, Loki logback appender.
techStack:
  # address-service
  - "Java 23"
  - "Spring Boot 3.3.2"
  - "Spring Data JPA"
  - "PostgreSQL 15"
  - "Redis 7"
  - "Spring Boot Actuator"
  - "Micrometer + Prometheus"
  - "OpenTelemetry Tracing Bridge"
  - "Loki4j + Loki + Grafana"
  - "Flyway Migrations"
  - "Spring Security + JWT"
  - "Springdoc OpenAPI 2.6.0"
  - "Lombok"
  - "Docker"
  # auth-service
  - "Java 23"
  - "Spring Boot 3.3.2"
  - "JUnit 5 / Testcontainers (integration tests)"
  - "Spring Security"
  - "Spring Data JPA"
  - "Spring Data Redis"
  - "Spring Kafka"
  - "gRPC (for user-service communication)"
  - "JWT (JJWT 0.11.5)"
  - "Redis 7"
  - "PostgreSQL (via user-service)"
  - "Flyway Migrations"
  - "Lombok"
  - "Protobuf/gRPC"
  - "OAuth2 Client"
  # cart-service
  - "Java 23"
  - "Spring Boot 3.3.2"
  - "Spring Data JPA"
  - "PostgreSQL 15"
  - "Redis 7"
  - "gRPC 1.60.0"
  - "Protobuf 3.25.1"
  - "Apache Kafka"
  - "Flyway Migrations 10.17.0"
  - "Spring Cache"
  - "Lombok"
  - "Spring Boot Admin Client 3.0.0"
  - "SpringDoc OpenAPI 2.6.0"
  # employee-service
  - "Java 23"
  - "Spring Boot 3.3.2"
  - "Spring Data JPA"
  - "PostgreSQL 15"
  - "Redis (for rate limiting)"
  - "Flyway Migrations 10.17.0"
  - "Spring Cloud Config Client 2023.0.3"
  - "Spring Boot Admin Client 3.0.0"
  - "Spring Security"
  - "Spring Kafka"
  - "Spring Data Redis"
  - "Lombok"
  - "SpringDoc OpenAPI 2.6.0"
  # inventory-service
  - "Java 23"
  - "Spring Boot 3.3.2"
  - "Spring Data JPA"
  - "PostgreSQL 15"
  - "Redis (for caching/reservations)"
  - "RabbitMQ (AMQP messaging)"
  - "JUnit 5 / Spring Boot Test (integration)"
  - "Flyway Migrations 10.17.0"
  - "Spring Boot Admin Client 3.0.0"
  - "Spring Cache"
  - "Lombok"
  - "SpringDoc OpenAPI 2.7.0"
  - "Logstash Logback Encoder 7.4"
  - "Janino 3.1.10"
  # notification-service
  - ""
  # order-service
  - "Java 23"
  - "Spring Boot 3.3.2"
  - "Spring Data JPA"
  - "Spring Security"
  - "Spring Boot Actuator"
  - "PostgreSQL"
  - "Redis"
  - "Flyway"
  - "OpenSearch"
  - "Logstash"
  - "Spring Boot Admin"
  - "OpenAPI/Swagger"
  - "Lombok"
  - "Gradle"
  # payment-service
  - "Java 23"
  - "Spring Boot 3.3.2"
  - "Spring Data JPA"
  - "Spring Security"
  - "Spring Boot Admin"
  - "PostgreSQL"
  - "Redis"
  - "Flyway"
  - "Stripe API (Planned)"
  - "OpenAPI/Swagger"
  - "Lombok"
  - "Kafka (Dependency Only)"
  - "Gradle"
  # product-service
  - "Spring Web"
  - "Spring Security (JWT)"
  - "Spring Data JPA"
  - "Spring Data Redis + Spring Cache"
  - "Spring Kafka"
  - "PostgreSQL + Flyway"
  - "H2 (tests)"
  - "Micrometer + Prometheus + Grafana + Loki"
  # store-service
  - "Java 23"
  - "Spring Boot 3.5.14"
  - "Spring Security + JWT"
  - "PostgreSQL 15 + Flyway"
  - "Redis 7 (cache)"
  - "Micrometer Prometheus"
  - "Loki logback appender"
  - "Springdoc OpenAPI 2.8.x"
  - "Docker / Nginx 1.27"
  # user-service
  - "Java 23"
  - "Spring Boot 3.5.14"
  - "Spring Web / Security / Validation"
  - "Spring Data JPA"
  - "PostgreSQL"
  - "Redis (Spring Data Redis + cache)"
  - "Apache Kafka (Spring Kafka)"
  - "Flyway (enabled in docker profile YAML when corrected)"
  - "grpc-java 1.60.0 + protobuf 3.25.1"
  - "JJWT 0.11.5"
  - "SpringDoc OpenAPI 2.6.0"
  - "Micrometer Prometheus + Loki logback appender"
  - "Spring Boot Admin client"
  - "Docker (eclipse-temurin:23)"
status: stable
createdAt: "2025-01-01"
updatedAt: "2026-06-08"
---

# Project Metadata

> Auto-generated by `scripts/merge_service_sources.py`. Edit service-level `docs/project/source/*.md` files, then regenerate.

<!-- BEGIN address-service -->
<!-- Source: address-service/docs/project/source/ProjectMetadata.md -->
# Project Metadata
> Fully functional microservice with complete REST API, PostgreSQL persistence, Redis caching/rate-limiting, Docker support, and comprehensive validation. Ready for cloud deployment with HTTPS/SSL configuration. Minor: consider adding integration tests and Kubernetes manifests for production-grade deploy.

<!-- END address-service -->

<!-- BEGIN auth-service -->
<!-- Source: auth-service/docs/project/source/ProjectMetadata.md -->
# Project Metadata
> Feature-rich authentication service with JWT tokens, 2FA, OAuth2, and Kafka event publishing. Includes **HTTP integration tests** (Testcontainers Redis/Kafka, in-process gRPC UserService stub). No embedded PostgreSQL—user-service owns persistence (gRPC). Potential improvements: broader unit tests, Micrometer metrics, Circuit Breaker on gRPC, Kubernetes manifests, CI/CD pipeline.

<!-- END auth-service -->

<!-- BEGIN cart-service -->
<!-- Source: cart-service/docs/project/source/ProjectMetadata.md -->
# Project Metadata
> DDD-based cart service with aggregate root pattern, gRPC endpoints for order-service, and Kafka integration for product events. Has unit tests for domain layer. Missing: integration tests, Kubernetes manifests, CI/CD pipeline. Potential improvements: Add CartPurchasedEvent publishing to Kafka when cart is cleared after order, implement Circuit Breaker for external calls, add Micrometer metrics for cart operations, add @RateLimit annotations on REST endpoints.

<!-- END cart-service -->

<!-- BEGIN employee-service -->
<!-- Source: employee-service/docs/project/source/ProjectMetadata.md -->
# Project Metadata
> Comprehensive employee management service with rich JPA entity (EmployeeEntity) and enums for roles, types, and status. Has @RateLimit annotations using libs-kernel shared library. PLACEHOLDER: No Dockerfile found, no docker-compose.yml in employee-service. Missing: Kubernetes manifests, CI/CD pipeline, unit/integration tests. Potential improvements: Add Kafka event publishing for employee lifecycle events, implement caching for frequently accessed employees, add Micrometer metrics.

<!-- END employee-service -->

<!-- BEGIN inventory-service -->
<!-- Source: inventory-service/docs/project/source/ProjectMetadata.md -->
# Project Metadata
> Comprehensive inventory service with batch tracking, reservations, and stock movements. Uses RabbitMQ (not Kafka like other services). Has Dockerfile (uses openjdk:17-jdk-slim, not Eclipse Temurin) and docker-compose for local stacks. **Testing:** integration suite (`test` profile, H2, JWT via `IntegrationTestJwtSupport`) — details in `ProjectFeature.md`. Still missing: broad unit coverage, Kubernetes manifests. Potential improvements: Micrometer metrics, Circuit Breaker for external calls, migrate to Kafka for consistency with other services.

<!-- END inventory-service -->

<!-- BEGIN notification-service -->
<!-- Source: notification-service/docs/project/source/ProjectMetadata.md -->
# Project Metadata
> Notes goes here....

<!-- END notification-service -->

<!-- BEGIN order-service -->
<!-- Source: order-service/docs/project/source/ProjectMetadata.md -->
# Project Metadata
> Order Service is a core backend microservice built with Java 23 and Spring Boot 3.3.2, implementing domain-driven design (DDD) and hexagonal architecture. It manages the complete order lifecycle including creation, status transitions, delivery/pickup methods, and integrates with PostgreSQL for persistence, Redis for caching, and OpenSearch/ELK stack for log aggregation.

<!--
  OBSERVATIONS FOR ProjectMetadata:
  ✅ POSITIVE:
    - Well-structured project with clear versioning (0.0.1-SNAPSHOT)
    - Comprehensive tech stack with modern frameworks
    - GitHub repository properly configured
    - DDD and hexagonal architecture properly implemented

  ⚠️ WARNINGS / MISSING / DANGEROUS:
    - liveDemoUrl is null - no live demo available yet
    - status is "develop" - not yet deployed to production
    - Java 23 requirement may cause toolchain issues (LSP errors show Java 25 on machine)
    - Hardcoded credentials in application.yml (POSTGRES_PASSWORD: "alexisAdmin1475963") - SECURITY RISK
    - No Dockerfile exists yet - needed for cloud deployment
    - createdAt/updatedAt dates are from git history but should be updated on actual releases
-->

<!-- END order-service -->

<!-- BEGIN payment-service -->
<!-- Source: payment-service/docs/project/source/ProjectMetadata.md -->
# Project Metadata
> Payment Service is a core backend microservice built with Java 23 and Spring Boot 3.3.2, implementing domain-driven design (DDD) with two aggregate roots (Payment and Sale). It integrates with Stripe for payment processing, handles webhooks, manages refunds, and automatically generates Sale records from completed payments. Uses PostgreSQL, Redis, and Spring Boot Admin.

<!--
  OBSERVATIONS FOR ProjectMetadata:
  ✅ POSITIVE:
    - Well-structured project with DDD and two aggregate roots
    - Modern Java 23 with records and enhanced switch expressions
    - Clean package structure under io.github.alexistrejo11.drugstore.payments
    - Comprehensive tech stack with Spring ecosystem
    - Kafka dependency included (ready for event streaming)
    - Flyway migrations configured for database versioning
    - OpenAPI/Swagger documentation configured

  ⚠️ WARNINGS / MISSING / DANGEROUS:
    - liveDemoUrl is null - no live demo available
    - status is "develop" - StripeGatewayAdapter is a STUB (returns null/empty!)
    - Java 23 requirement may cause toolchain issues (LSP errors show Java 25 on machine)
    - No Dockerfile exists yet - cannot build container image
    - No SecurityConfig found - "Security placeholder" in controllers with no JWT/auth configured
    - Kafka dependency in build.gradle but NO Kafka code implemented
    - application.yml uses environment variables for Stripe keys (good) but webhook secret exposure risk
    - Created and updated dates from git history - should be updated on actual releases
    - No CI/CD configuration visible
-->

<!-- END payment-service -->

<!-- BEGIN product-service -->
<!-- Source: product-service/docs/project/source/ProjectMetadata.md -->
# Project Metadata

## Notes

- Hardcoded default DB password appears in `application.yml`; replace with required env var only.
- `management.endpoints.web.exposure.include: "*"` is too open for production.
- `flyway.clean-disabled: false` is dangerous outside local/dev environments.

<!-- END product-service -->

<!-- BEGIN store-service -->
<!-- Source: store-service/docs/project/source/ProjectMetadata.md -->
# Project Metadata

> [!danger] Dev `application.yml` drift  
> Default **dev** profile still names the app `product-service`, points JDBC at **`product_DB` / drugstore_products**, and logs to `products-service.log`. Prefer **`spring.profiles.active=docker`** or align `application.yml` with store DB names or local runs will hit the wrong database.

> [!warning] Secrets in repo history  
> `application.yml` contains a **hardcoded Postgres password** placeholder pattern; rotate credentials and use env-only secrets in shared environments.

> [!note] Not production-complete  
> Outbound **store status events** are logged but **not sent to Kafka** (`StoreEventPublisherImpl` is a stub). Compose **requires** `JWT_SECRET_KEY` and `SPRING_KAFKA_BOOTSTRAP_SERVERS` even when messaging is unused.

<!-- END store-service -->

<!-- BEGIN user-service -->
<!-- Source: user-service/docs/project/source/ProjectMetadata.md -->
# Project Metadata

## Notes (important / dangerous / missing)

1. **Dockerfile matches Java 23** — Builder and runtime use Temurin 23 (`eclipse-temurin:23-jdk-noble` / `23-jre-alpine`). Older docs mentioning OpenJDK 17 are **obsolete** for this module.
2. **`application-docker.yml` drift** — Contains **store-service** naming and defaults. Treat as **broken for user-service** until realigned (DB, app name, springdoc scan, management tags).
3. **OpenAPI** — `packages-to-scan` references `...drugstore.stores`; user controllers live under `...drugstore.users`. **Missing / wrong** for first-class API docs.
4. **gRPC** — Proto + `UserGrpcServer` **implemented** but **no server startup** bean; inter-service RPC is **not operational** from this artifact alone.
5. **CQRS-style buses** — `CommandBus` / `QueryBus` mediate writes and reads (structured hexagonal layering).
6. **Logging copy-paste** — Default `application.yml` log file path names **products-service**; fix to avoid operational confusion.
7. **Compose host port env** — `ORDER_SERVICE_HOST_PORT` names an order service variable but maps **user-service** HTTP (`default 8086:8080`). Confusing but functional.
8. **Host Prometheus :9090** — Compose binds Prometheus to host **9090**; JVM `GRPC_PORT` default is also **9090** but **gRPC not started** — avoid assuming gRPC availability on localhost:9090.
9. **Kafka** — `spring.json.trusted.packages: '*'` reduces deserialization safety; tighten for production.

<!-- END user-service -->
