---
problemStatement:
  problemTitle: "Centralize and secure product-catalog operations"
  problemDescription: "Drugstore platform needs a single source of truth for product data with validation, fast reads, and cross-service event propagation."
  problemList:
    - "Inconsistent product data across services without a canonical owner."
    - "Need controlled write access with JWT roles (ADMIN/MANAGER)."
    - "Read-heavy catalog lookups need caching to reduce DB load."
    - "Other domains require product change events in near real-time."

solution:
  solutionTitle: "Spring Boot microservice with DDD-ish layering and event-driven integration"
  solutionList:
    - title: "Catalog API"
      description: "REST endpoints for CRUD, search, category listing, and restore of soft-deleted products."
    - title: "Validation + Security"
      description: "Domain value objects/validators plus JWT-based role authorization and request rate limits."
    - title: "Caching + Events"
      description: "Redis-backed cache decorators for query use cases and Kafka publishing for product lifecycle events."

keyMetrics:
  metricsTitle: "Operational and delivery metrics"
  metricsList:
    - "8 public/protected REST endpoints under /api/v2/products"
    - "4 cache regions (productById, productBySKU, productByBarcode, productSearch)"
    - "1 primary product-events topic with DLT support"
    - "7 passing integration tests in ProductApiIntegrationTest"

coverImage:
  url: "https://placehold.co/1200x630?text=Drugstore+Product+Service"
  alt: "Product Service architecture overview"
  credit: "Internal documentation"

links:
  github: "https://github.com/alexisTrejo11/drugstore-platform"
  demo: "https://placeholder.drugstore.local/product-service-demo"
  documentation: "docs/project"
  dockerHub: "https://hub.docker.com/r/placeholder/product-service"

mediaGallery:
  title: "Service references"
  description: "Core service artifacts"
  items:
    - type: "image"
      url: "src/main/java/io/github/alexisTrejo11/drugstore/products/adapter/in/web/controller/ProductController.java"
      thumbnail: "https://placehold.co/320x200?text=Controller"
      title: "REST Controller"
      description: "Main API surface for product operations."
      alt: "ProductController path reference"
      category: "code"

mediaItems:
  - type: "image"
    url: "docker-compose.yml"
    thumbnail: "https://placehold.co/320x200?text=Compose"
    title: "Compose topology"
    description: "App + nginx + redis + postgres + observability services."
    alt: "docker compose reference"
    category: "infra"

metrics:
  - label: "Endpoints"
    value: "8"
    description: "HTTP endpoints exposed by ProductController."
    icon: "api"
    unit: "routes"
    trend: "stable"
    threshold: "N/A"
  - label: "Cache Regions"
    value: "4"
    description: "Core cache buckets used by caching decorators."
    icon: "database"
    unit: "regions"
    trend: "stable"
    threshold: "N/A"
  - label: "Integration Tests"
    value: "7"
    description: "Current ProductApiIntegrationTest coverage count."
    icon: "test"
    unit: "tests"
    trend: "stable"
    threshold: "N/A"
---

# Overview

- Product Service is the catalog owner in the Drugstore platform.
- It combines CRUD/search APIs, JWT authn/authz, Redis caching, and Kafka event output.

## Notes

- Configuration has mixed env var names (`product_DB` vs `POSTGRES_DB_NAME`) that can cause misconfiguration.
- Swagger and broad actuator exposure are enabled by default; tighten for non-dev environments.
	