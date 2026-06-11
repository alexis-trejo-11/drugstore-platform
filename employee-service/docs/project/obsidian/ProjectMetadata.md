---
projectId: "employee-service-v1"
featured: true
name: "Employee Service"
language: "Java"
category: "backend"
framework: "Spring Boot 3.3.2"
version: "0.0.1-SNAPSHOT"
repositoryUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/employee-service"
liveDemoUrl: null
description: "Employee management microservice for the drugstore platform. Manages employee data including personal info, workday schedules (JSONB), certifications, compensation, employment status (ACTIVE, INACTIVE, ON_LEAVE, SUSPENDED, TERMINATED), and role-based access (PHARMACIST, PHARMACY_TECHNICIAN, STORE_MANAGER, etc.)."
techStack:
  - "Java 23"
  - "Spring Boot 3.3.2"
  - "Spring Data JPA"
  - "PostgreSQL 15"
  - "Redis (for rate limiting)"
  - "Flyway Migrations 10.17.0"
  - "Spring Cloud Config Client 2023.0.3"
  - "Spring Boot Admin Client 3.0.0"
  - "Spring Security"
  - "Spring Kafka"
  - "Spring Data Redis"
  - "Lombok"
  - "SpringDoc OpenAPI 2.6.0"
status: "develop"
createdAt: "2025-10-01T00:00:00.000Z"
updatedAt: "2026-04-29T00:00:00.000Z"

---

# Project Metadata
> Comprehensive employee management service with rich JPA entity (EmployeeEntity) and enums for roles, types, and status. Has @RateLimit annotations using libs-kernel shared library. PLACEHOLDER: No Dockerfile found, no docker-compose.yml in employee-service. Missing: Kubernetes manifests, CI/CD pipeline, unit/integration tests. Potential improvements: Add Kafka event publishing for employee lifecycle events, implement caching for frequently accessed employees, add Micrometer metrics.
