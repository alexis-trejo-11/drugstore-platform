---
projectId: "inventory-service-v1"
featured: true
name: "Inventory Service"
language: "Java"
category: "backend"
framework: "Spring Boot 3.3.2"
version: "0.0.1-SNAPSHOT"
repositoryUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/inventory-service"
liveDemoUrl: null
description: "Inventory management microservice for the drugstore platform. Manages product inventory with batch tracking (lot numbers, expiration dates), stock reservations for order processing, inventory movements (adjustments, transfers), low-stock alerts, and RabbitMQ messaging for inventory events. Includes Spring Boot integration tests (profile test, H2, real JWT)."
techStack:
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
status: "develop"
createdAt: "2025-11-01T00:00:00.000Z"
updatedAt: "2026-04-29T00:00:00.000Z"

---

# Project Metadata
> Comprehensive inventory service with batch tracking, reservations, and stock movements. Uses RabbitMQ (not Kafka like other services). Has Dockerfile (uses openjdk:17-jdk-slim, not Eclipse Temurin) and docker-compose for local stacks. **Testing:** integration suite (`test` profile, H2, JWT via `IntegrationTestJwtSupport`) — details in `ProjectFeature.md`. Still missing: broad unit coverage, Kubernetes manifests. Potential improvements: Micrometer metrics, Circuit Breaker for external calls, migrate to Kafka for consistency with other services.
