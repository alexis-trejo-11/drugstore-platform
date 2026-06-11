---
projectId: "auth-service-v1"
featured: true
name: "Auth Service"
language: "Java"
category: "backend"
framework: "Spring Boot 3.3.2"
version: "0.0.1-SNAPSHOT"
repositoryUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/auth-service"
liveDemoUrl: null
description: "Authentication and authorization microservice for the drugstore platform. Handles user registration (Customer/Employee/Admin), JWT token management (access + refresh), password reset flows, two-factor authentication via TOTP, OAuth2 social login, and publishes user events to Kafka for inter-service communication."
techStack:
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
status: "develop"
createdAt: "2025-08-01T00:00:00.000Z"
updatedAt: "2026-05-01T00:00:00.000Z"

---

# Project Metadata
> Feature-rich authentication service with JWT tokens, 2FA, OAuth2, and Kafka event publishing. Includes **HTTP integration tests** (Testcontainers Redis/Kafka, in-process gRPC UserService stub). No embedded PostgreSQL—user-service owns persistence (gRPC). Potential improvements: broader unit tests, Micrometer metrics, Circuit Breaker on gRPC, Kubernetes manifests, CI/CD pipeline.
