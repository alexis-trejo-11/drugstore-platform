---
projectId: "store-service"
featured: false
name: "Store Service"
language: "Java"
category: "backend"
framework: "Spring Boot"
version: "2.0.0"
repositoryUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/store-service"
liveDemoUrl: null
description: "Hexagonal Spring Boot microservice for drugstore store catalog: CRUD-style commands, search and pagination, Redis caching, JWT security, Flyway/PostgreSQL, optional Kafka hook (stub), Docker Compose with Nginx, Prometheus, Loki, and Grafana."
techStack:
  - "Java 23"
  - "Spring Boot 3.5.14"
  - "Spring Security + JWT"
  - "PostgreSQL 15 + Flyway"
  - "Redis 7 (cache)"
  - "Micrometer Prometheus"
  - "Loki logback appender"
  - "Springdoc OpenAPI 2.8.x"
  - "Docker / Nginx 1.27"
status: "develop"
createdAt: "2026-01-01T00:00:00.000Z"
updatedAt: "2026-05-06T00:00:00.000Z"
---

# Project Metadata

> [!danger] Dev `application.yml` drift  
> Default **dev** profile still names the app `product-service`, points JDBC at **`product_DB` / drugstore_products**, and logs to `products-service.log`. Prefer **`spring.profiles.active=docker`** or align `application.yml` with store DB names or local runs will hit the wrong database.

> [!warning] Secrets in repo history  
> `application.yml` contains a **hardcoded Postgres password** placeholder pattern; rotate credentials and use env-only secrets in shared environments.

> [!note] Not production-complete  
> Outbound **store status events** are logged but **not sent to Kafka** (`StoreEventPublisherImpl` is a stub). Compose **requires** `JWT_SECRET_KEY` and `SPRING_KAFKA_BOOTSTRAP_SERVERS` even when messaging is unused.
