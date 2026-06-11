---
projectId: "cart-service-v1"
featured: true
name: "Cart Service"
language: "Java"
category: "backend"
framework: "Spring Boot 3.3.2"
version: "0.0.1-SNAPSHOT"
repositoryUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/cart-service"
liveDemoUrl: null
description: "Shopping cart microservice for the drugstore platform implementing Domain-Driven Design with Cart aggregate root. Supports cart items management, afterwards items (save-for-later), gRPC endpoints for inter-service communication, Kafka event consumption for product updates, Redis caching, and comprehensive validation."
techStack:
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
status: "develop"
createdAt: "2025-09-01T00:00:00.000Z"
updatedAt: "2026-04-29T00:00:00.000Z"

---

# Project Metadata
> DDD-based cart service with aggregate root pattern, gRPC endpoints for order-service, and Kafka integration for product events. Has unit tests for domain layer. Missing: integration tests, Kubernetes manifests, CI/CD pipeline. Potential improvements: Add CartPurchasedEvent publishing to Kafka when cart is cleared after order, implement Circuit Breaker for external calls, add Micrometer metrics for cart operations, add @RateLimit annotations on REST endpoints.
