---
projectId: "user-service"
featured: false
name: "User Service"
language: "Java 23"
category: "backend"
framework: "Spring Boot 3.5.14"
version: "2.0.0"
repositoryUrl: "https://github.com/PLACEHOLDER/drugstore-platform/tree/main/user-service"
liveDemoUrl: null
description: "User and profile microservice: JWT REST API (/api/v2/users, manager lifecycle, profile), Redis, PostgreSQL, Kafka consumers for user.* topics, protobuf/gRPC stubs and UserGrpcServer implementation (server bootstrap not wired). Observability via Actuator, Prometheus registry, Loki logback appender."
techStack:
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
status: "develop"
createdAt: "2026-01-01T00:00:00.000Z"
updatedAt: "2026-05-06T00:00:00.000Z"
---

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
