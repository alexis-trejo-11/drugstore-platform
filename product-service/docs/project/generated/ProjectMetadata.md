# Project Metadata

- **Project ID:** `drugstore-product-service`
- **Name:** Drugstore Product Service
- **Category:** Backend microservice
- **Language:** Java 23
- **Framework:** Spring Boot 3.5.14
- **Version:** 2.0.0
- **Repository:** <https://github.com/alexisTrejo11/drugstore-platform>
- **Live Demo (placeholder):** <https://placeholder.drugstore.local/product-service>
- **Status:** develop

## Description

Microservice that owns product catalog CRUD/search, applies domain validations, and publishes product lifecycle events for other platform services.

## Tech Stack

- Spring Web
- Spring Security (JWT)
- Spring Data JPA
- Spring Data Redis + Spring Cache
- Spring Kafka
- PostgreSQL + Flyway
- H2 for test runtime
- Micrometer + Prometheus + Grafana + Loki

## Notes

- Hardcoded default DB password appears in `application.yml`; replace with required env var only.
- `management.endpoints.web.exposure.include: "*"` is too open for production.
- `flyway.clean-disabled: false` is dangerous outside local/dev environments.
