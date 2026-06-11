---
problemStatement:
  problemTitle: "Centralized store master data for a distributed drugstore platform"
  problemDescription: "Product and order services need a single source of truth for store identity, address, schedule, geolocation, and operational status across channels."
  problemList:
    - "Store data must stay consistent with address and business rules (status transitions, soft delete)."
    - "Read-heavy discovery flows need low latency under concurrent traffic."
    - "Operators need secured mutation APIs while storefronts may read catalog data."

solution:
  solutionTitle: "Store Service (hexagonal REST API)"
  solutionList:
    - title: "Domain-driven core + adapters"
      description: "Commands and queries go through use cases; JPA implements persistence; REST exposes `/api/v2/stores` with OpenAPI."
    - title: "Redis-backed caching + rate limits"
      description: "Query results cached in named regions; method-level rate limits via shared-kernel profiles (global + per-endpoint)."
    - title: "Docker-first operations"
      description: "Compose brings Postgres, Redis, observability stack, and Nginx TLS in front of scalable store-service replicas."

keyMetrics:
  metricsTitle: "Targets (placeholder — not instrumented as SLOs yet)"
  metricsList:
    - "p95 read < 200ms at the app (exclude WAN) — TBD"
    - "Cache hit ratio for `stores` / `store_searches` — TBD"

coverImage:
  url: "https://placehold.co/1200x630/1a237e/ffffff?text=Store+Service"
  alt: "Placeholder cover for store-service"
  credit: "Placeholder"

links:
  github: "https://github.com/alexisTrejo11/drugstore-platform"
  demo: null
  documentation: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/store-service/docs/project"
  dockerHub: null

mediaGallery:
  title: "Screenshots"
  description: "Add Swagger UI and Grafana dashboard captures when available."
  items:
    - type: "image"
      url: "https://placehold.co/800x450/e8eaf6/3949ab?text=Swagger+UI+TBD"
      thumbnail: "https://placehold.co/200x112/e8eaf6/3949ab?text=Swagger"
      title: "OpenAPI / Swagger UI"
      description: "Available at /swagger-ui.html when SWAGGER_ENABLED=true"
      alt: "Placeholder Swagger screenshot"
      category: "screenshot"

mediaItems:
  - type: "image"
    url: "https://placehold.co/800x450/eeeeee/333333?text=Architecture+Diagram+TBD"
    thumbnail: "https://placehold.co/200x112/eeeeee/333333?text=Arch"
    title: "High-level topology"
    description: "Client → Nginx → store-service → Postgres / Redis"
    alt: "Placeholder architecture diagram"
    category: "diagram"

metrics:
  - label: "Service version"
    value: "2.0.0"
    description: "Gradle project version"
    icon: "package"
    unit: ""
    trend: "stable"
    threshold: null
  - label: "API base path"
    value: "/api/v2/stores"
    description: "All store REST resources"
    icon: "api"
    unit: ""
    trend: "stable"
    threshold: null
---

# Overview

Drugstore **store-service** owns **physical store master data**: code, name, contact, postal address, geolocation JSON schedule (`schedule_config`), and lifecycle statuses (`ACTIVE`, `INACTIVE`, `UNDER_MAINTENANCE`, `TEMPORARILY_CLOSED`).

### Highlighted risks & gaps

| Severity | Topic |
|---------|--------|
| **Danger** | **Dev profile YAML** mismatches prod (`product-service`, wrong DB env key `product_DB`) — risk of corrupting wrong schema locally. |
| **Danger** | **Redis serializer** enables Jackson default typing (`NON_FINAL`) — classic ** deserialization gadget** risk if Redis is reachable by an attacker; harden serializer strategy for production. |
| **Danger** | **Swagger + actuator footprint**: dev exposes broad actuator CORS/origins; lock down before any public demo. |
| **Missing** | **Kafka publishing** is stubbed; downstream consumers never receive events. |
| **Missing** | **`PATCH temporary-closure` and `PATCH deactivate`** omit `@RateLimit` — inconsistent abuse surface vs other commands. |
