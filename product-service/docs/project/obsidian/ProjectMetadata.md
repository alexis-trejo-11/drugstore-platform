---
projectId: "drugstore-product-service"
featured: true
name: "Drugstore Product Service"
language: "Java 23"
category: "backend"
framework: "Spring Boot 3.5.14"
version: "2.0.0"
repositoryUrl: "https://github.com/alexisTrejo11/drugstore-platform"
liveDemoUrl: "https://placeholder.drugstore.local/product-service"
description: "Microservice that owns product catalog CRUD/search, enforces domain validations, and publishes product lifecycle events."
techStack:
  - "Spring Web"
  - "Spring Security (JWT)"
  - "Spring Data JPA"
  - "Spring Data Redis + Spring Cache"
  - "Spring Kafka"
  - "PostgreSQL + Flyway"
  - "H2 (tests)"
  - "Micrometer + Prometheus + Grafana + Loki"
status: "develop"
createdAt: "2026-01-01T00:00:00.000Z"
updatedAt: "2026-05-05T21:20:00.000Z"
---

# Project Metadata

## Notes

- Hardcoded default DB password appears in `application.yml`; replace with required env var only.
- `management.endpoints.web.exposure.include: "*"` is too open for production.
- `flyway.clean-disabled: false` is dangerous outside local/dev environments.
