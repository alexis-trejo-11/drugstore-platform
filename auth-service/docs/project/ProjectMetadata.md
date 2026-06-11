# Project metadata

Plain-Markdown twin of `docs/project/source/ProjectMetadata.md`.

| Field | Value |
|--------|--------|
| **Project ID** | `auth-service-v1` |
| **Name** | Auth Service |
| **Featured** | Yes |
| **Language** | Java |
| **Category** | backend |
| **Framework** | Spring Boot 3.3.2 |
| **Version** | 0.0.1-SNAPSHOT |
| **Status** | develop |
| **Created** | 2025-08-01 |
| **Updated** | 2026-05-01 |
| **Repository** | [github.com/alexisTrejo11/drugstore-platform/tree/main/auth-service](https://github.com/alexisTrejo11/drugstore-platform/tree/main/auth-service) |
| **Live demo** | *Not configured* |

## Description

Authentication and authorization microservice for the drugstore platform. Handles user registration (Customer/Employee/Admin), JWT token management (access + refresh), password reset flows, two-factor authentication via TOTP, OAuth2 social login, and publishes user events to Kafka for inter-service communication.

## Tech stack

- Java 23
- Spring Boot 3.3.2
- JUnit 5 / Testcontainers (integration tests)
- Spring Security
- Spring Data JPA
- Spring Data Redis
- Spring Kafka
- gRPC (user-service communication)
- JWT (JJWT 0.11.5)
- Redis 7
- PostgreSQL (via user-service, not local)
- Flyway Migrations
- Lombok
- Protobuf/gRPC
- OAuth2 Client
- **shared-kernel** `2.0.0` from GitHub Packages (`io.github.alexisTrejo11:shared-kernel`)

## Highlighted notes

- **HTTP integration tests** use Testcontainers (Redis, Kafka) and an in-process gRPC `UserService` stub; Docker required to run the suite.
- **No embedded PostgreSQL** — user persistence is owned by user-service (gRPC).
- **Potential improvements:** broader unit tests, Micrometer metrics, Resilience4j Circuit Breaker on gRPC, Kubernetes manifests, CI/CD pipeline.
