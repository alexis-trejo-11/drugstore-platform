---
projectId: "order-service"
featured: true
name: "Order Service"
language: "Java 23"
category: "backend"
framework: "Spring Boot 3.3.2"
version: "0.0.1-SNAPSHOT"
repositoryUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/order-service"
liveDemoUrl: null
description: "A microservice responsible for complete order lifecycle management in a drugstore platform, implementing DDD and hexagonal architecture with support for multiple delivery methods including store pickup and home delivery."
techStack:
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
status: "develop"
createdAt: "2025-07-25T11:07:47.000Z"
updatedAt: "2026-03-02T18:30:28.000Z"

---

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
