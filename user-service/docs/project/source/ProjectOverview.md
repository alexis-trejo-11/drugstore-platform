---
problemStatement:
  problemTitle: "Centralized identity and profile for the drugstore platform"
  problemDescription: "Order, cart, and store services need a single source of truth for user records, credentials validation, lifecycle (activate/ban/delete), and customer profile data without duplicating persistence rules."
  problemList:
    - "REST API for authenticated reads and manager/admin mutations"
    - "Kafka consumption for user.created / user.updated / user.deleted side effects"
    - "Inter-service checks (uniqueness, credentials) intended via gRPC (see architecture notes)"
    - "Redis-backed caching and rate limiting hooks (shared-kernel)"

solution:
  solutionTitle: "Spring Boot user microservice (hexagonal-style ports + buses)"
  solutionList:
    - title: "Command / query separation"
      description: "Write paths use CommandBus; reads use QueryBus over domain ports and JPA adapters."
    - title: "JWT stateless API"
      description: "libs_kernel JwtAuthenticationFilter; manager routes require ADMIN or MANAGER."
    - title: "Docker-first local stack"
      description: "Compose: PostgreSQL, Redis, optional external Kafka, Nginx TLS edge, Prometheus, Loki, Grafana."

keyMetrics:
  metricsTitle: "Suggested operational targets (placeholders)"
  metricsList:
    - "p95 read latency < 150ms at steady state (not measured in-repo)"
    - "Actuator /actuator/health for container orchestration"
    - "Flyway on in docker profile when application-docker.yml is corrected (see risks)"

coverImage:
  url: "https://placehold.co/1200x630/2196F3/ffffff?text=User+Service"
  alt: "Placeholder cover for user-service documentation"
  credit: "Placeholder image"

links:
  github: "https://github.com/PLACEHOLDER/drugstore-platform"
  demo: null
  documentation: "https://PLACEHOLDER.docs/user-service"
  dockerHub: null

mediaGallery:
  title: "Architecture snapshots"
  description: "Replace with real screenshots (Swagger UI, Grafana dashboards)."
  items:
    - type: "image"
      url: "https://placehold.co/800x450/e0e0e0/333?text=Swagger+UI"
      thumbnail: "https://placehold.co/200x113/e0e0e0/333?text=Thumb"
      title: "API docs (placeholder)"
      description: "Springdoc OpenAPI — fix packages-to-scan for this service first."
      alt: "Placeholder screenshot"
      category: "screenshot"

mediaItems:
  - type: "image"
    url: "https://placehold.co/800x450/009688/ffffff?text=Nginx+TLS"
    thumbnail: "https://placehold.co/200x113/009688/ffffff?text=TLS"
    title: "Nginx edge (Compose)"
    description: "TLS termination on :443; upstream to user-service:8080."
    alt: "Nginx placeholder"
    category: "diagram"

metrics:
  - label: "Service version (build.gradle)"
    value: "2.0.0"
    description: "Gradle project version string"
    icon: "package"
    unit: ""
    trend: "stable"
    threshold: null
  - label: "Java toolchain"
    value: "23"
    description: "Eclipse Temurin in Dockerfile matches toolchain"
    icon: "java"
    unit: ""
    trend: "stable"
    threshold: null
---

# Overview

The **user-service** is a Spring Boot microservice that owns **user persistence**, **profile** data, **JWT-protected REST APIs**, **Kafka consumption** for user lifecycle events, and (in code only) a **gRPC service definition** intended for other services.

## Highlighted risks (read first)

| Severity | Topic | Note |
|----------|--------|------|
| **High** | `application-docker.yml` | File content matches **store-service** (wrong `spring.application.name`, DB name defaults, springdoc scan, log/metrics labels). Docker runs may not match user-service expectations until fixed. |
| **High** | OpenAPI scan | `springdoc.packages-to-scan` points at `io.github.alexisTrejo11.drugstore.**stores**`, not `...users**` — Swagger may omit user controllers. |
| **Medium** | gRPC | `UserGrpcServer` exists but **no gRPC Netty server / Spring gRPC starter** wires it; `GrpcServerConfig` is empty. Port `9090` in config does not expose a working RPC server in this codebase. |
| **Medium** | Kafka consumer | On failure, **no ack** and **no DLQ** — throws `RuntimeException`; can cause tight retry loops (TODOs in code). |
| **Medium** | Dev profile | Default JWT secret placeholder, Flyway **disabled** with `ddl-auto: update`, actuator **`*`** exposure — not production-safe. |

## What is intentionally generic / placeholder

- **Cloud**: no AWS/GCP/Azure resources are defined in-repo; YAML uses dummy provider names below in infrastructure docs.
- **Links**: GitHub/demo URLs are placeholders until you publish the repo.
