---
problemStatement:
  problemTitle: "Inventory Management in Microservices Pharmacy"
  problemDescription: "A drugstore pharmacy needs comprehensive inventory management including batch tracking (lot numbers, expiration dates), stock reservations for order processing, inventory movements (adjustments, transfers between warehouses), low-stock alerts, and event publishing for inventory changes."
  problemList:
    - "Track inventory per product with total/available/reserved quantities"
    - "Manage inventory batches with lot numbers, manufacturing/expiration dates"
    - "Support stock reservations for pending orders (prevent overselling)"
    - "Track inventory movements (adjustments, transfers) for audit trail"
    - "Generate low-stock alerts based on reorder levels"
    - "Publish inventory events to message queue for other services"
    - "Warehouse location tracking for multi-location support"

solution:
  solutionTitle: "Comprehensive Inventory Management Service"
  solutionList:
    - title: "Inventory Entity with Batch Tracking"
      description: "InventoryEntity tracks total/available/reserved quantities, batches with lot numbers and expiration dates"
    - title: "Stock Reservation System"
      description: "Reservation system prevents overselling during order processing with automatic release on timeout"
    - title: "Inventory Movements Tracking"
      description: "All stock adjustments and transfers tracked via InventoryMovementEntity with audit trail"
    - title: "Low-Stock Alerts"
      description: "InventoryAlertEntity generates alerts when stock falls below reorder level"
    - title: "RabbitMQ Messaging"
      description: "Publishes inventory events (created, updated, low-stock) to RabbitMQ for other services"
    - title: "Redis Caching"
      description: "Spring Cache with Redis for frequently accessed inventory data"
    - title: "Integration testing (profile test)"
      description: "H2 in-memory, MockMvc REST tests, real JWT headers (JwtAuthenticationFilter); see docs/project/ProjectFeature.md"

keyMetrics:
  metricsTitle: "Inventory Service Key Metrics"
  metricsList:
    - "InventoryEntity with total/available/reserved quantity tracking"
    - "InventoryBatchEntity with lot numbers, expiration dates, supplier info"
    - "StockReservationEntity for order processing (prevent overselling)"
    - "InventoryMovementEntity for adjustments and transfers tracking"
    - "InventoryAlertEntity for low-stock notifications"
    - "REST API with 15+ endpoints (inventory, batches, reservations, movements)"
    - "RabbitMQ integration (AMQP) for event publishing"
    - "Integration tests: InventoryApiIntegrationTest + JWT helper (profile test)"

coverImage:
  url: "https://placeholder-drugstore.com/images/inventory-service-cover.png"
  alt: "Inventory Service Architecture Diagram"
  credit: "Drugstore Platform Team"

links:
  github: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/inventory-service"
  demo: null
  documentation: "https://api.ecommerce.com/inventory-service/swagger-ui"
  dockerHub: "https://hub.docker.com/r/alexistrejo11/inventory-service"

mediaGallery:
  title: "Inventory Service Media Gallery"
  description: "Screenshots and diagrams of the Inventory Service"
  items:
    - type: "image"
      url: "https://placeholder-drugstore.com/images/inventory-swagger.png"
      thumbnail: "https://placeholder-drugstore.com/images/inventory-swagger-thumb.png"
      title: "Swagger API Documentation"
      description: "OpenAPI documentation for inventory endpoints"
      alt: "Swagger UI screenshot"
      category: "screenshot"
    - type: "image"
      url: "https://placeholder-drugstore.com/images/inventory-entity-model.png"
      thumbnail: "https://placeholder-drugstore.com/images/inventory-entity-model-thumb.png"
      title: "JPA Entity Model"
      description: "Inventory, Batch, Reservation, Movement, Alert entities"
      alt: "Entity model diagram"
      category: "architecture"

mediaItems:
  - type: "image"
    url: "https://placeholder-drugstore.com/images/inventory-reservation-flow.png"
    thumbnail: "https://placeholder-drugstore.com/images/inventory-reservation-flow-thumb.png"
    title: "Stock Reservation Flow"
    description: "How reservations prevent overselling during order processing"
    alt: "Reservation flow diagram"
    category: "diagram"

metrics:
  - label: "API Endpoints"
    value: "15+"
    description: "REST endpoints (inventory, batches, reservations, movements)"
    icon: "api"
    unit: "endpoints"
    trend: "stable"
    threshold: null
  - label: "Entities"
    value: "5"
    description: "Inventory, Batch, Movement, Reservation, Alert"
    icon: "database"
    unit: "entities"
    trend: "stable"
    threshold: null
  - label: "Messaging"
    value: "RabbitMQ"
    description: "AMQP for inventory events (not Kafka like other services)"
    icon: "rabbitmq"
    unit: "type"
    trend: "stable"
    threshold: null
---
# Overview

> Comprehensive inventory service with batch tracking, reservations, and stock movements. Uses RabbitMQ (not Kafka like other services - inconsistency). Dockerfile uses openjdk:17-jdk-slim (not Eclipse Temurin like other services). **Testing:** Spring Boot integration tests run under profile `test` (H2, real JWT headers); see `docs/project/ProjectFeature.md`. **Still missing:** broader unit coverage, migrate to Kafka for consistency, Kubernetes manifests, CI/CD pipeline.
