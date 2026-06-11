---
projectId: "payment-service"
featured: true
name: "Payment Service"
language: "Java 23"
category: "backend"
framework: "Spring Boot 3.3.2"
version: "0.0.1-SNAPSHOT"
repositoryUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service"
liveDemoUrl: null
description: "A microservice responsible for payment processing and sales management in a drugstore platform, implementing DDD with Stripe integration for payment gateway operations including refunds, webhooks, and sales generation from completed payments."
techStack:
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
status: "develop"
createdAt: "2026-02-21T16:27:36.000Z"
updatedAt: "2026-03-04T13:33:01.000Z"

---

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
