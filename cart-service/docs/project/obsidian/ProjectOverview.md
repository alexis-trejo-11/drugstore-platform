---
problemStatement:
  problemTitle: "Shopping Cart Management in Microservices E-Commerce"
  problemDescription: "In a microservices e-commerce platform, shopping cart functionality must be isolated from other services while providing real-time cart management, product availability checks, save-for-later (afterwards) feature, and efficient inter-service communication via gRPC."
  problemList:
    - "Need isolated cart aggregate with business logic encapsulation"
    - "Support for cart items with quantity management"
    - "Save-for-later (afterwards) feature for items not ready for checkout"
    - "gRPC endpoints for order-service to access carts during checkout"
    - "Kafka event consumption for product updates (price changes, availability)"
    - "Redis caching for frequently accessed cart data"
    - "Pagination and search capabilities for admin management"

solution:
  solutionTitle: "DDD Cart Service with Aggregate Root Pattern"
  solutionList:
    - title: "Cart Aggregate Root"
      description: "Cart.java is the aggregate root with items and afterwardsItems, encapsulating all business logic for add/update/remove/clear operations"
    - title: "gRPC Service Interface"
      description: "CartGrpcService exposes GetUserCart and ClearCart endpoints for order-service integration during checkout flow"
    - title: "Afterwards (Save-for-Later)"
      description: "Separate list for items saved for later, with move-to-afterwards and restore-from-afterwards operations"
    - title: "Kafka Product Events"
      description: "ProductEventConsumer listens to product-events topic and updates cart items via ProductEventHandler"
    - title: "Value Objects"
      description: "CartId, CustomerId, ProductId, Quantity, ItemPrice as strongly-typed value objects with validation"
    - title: "Redis Caching"
      description: "Spring Cache with Redis for cart lookups, configured via RedisCacheConfig"

keyMetrics:
  metricsTitle: "Cart Service Key Metrics"
  metricsList:
    - "Max 100 unique items per cart (MAX_ITEMS_PER_CART)"
    - "gRPC endpoints: 2 (GetUserCart, ClearCart)"
    - "REST endpoints: 5+ (user + admin controllers)"
    - "Domain events: CartPurchasedEvent (PLACEHOLDER: not published yet)"
    - "Kafka topic: product-events for product updates"
    - "Unit tests: 11 test files in src/test"

coverImage:
  url: "https://placeholder-drugstore.com/images/cart-service-cover.png"
  alt: "Cart Service Architecture Diagram"
  credit: "Drugstore Platform Team"

links:
  github: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/cart-service"
  demo: null
  documentation: "https://api.ecommerce.com/cart-service/swagger-ui"
  dockerHub: "https://hub.docker.com/r/alexistrejo11/cart-service"

mediaGallery:
  title: "Cart Service Media Gallery"
  description: "Screenshots and diagrams of the Cart Service"
  items:
    - type: "image"
      url: "https://placeholder-drugstore.com/images/cart-swagger.png"
      thumbnail: "https://placeholder-drugstore.com/images/cart-swagger-thumb.png"
      title: "Swagger API Documentation"
      description: "OpenAPI documentation for cart endpoints"
      alt: "Swagger UI screenshot"
      category: "screenshot"
    - type: "image"
      url: "https://placeholder-drugstore.com/images/cart-domain-model.png"
      thumbnail: "https://placeholder-drugstore.com/images/cart-domain-model-thumb.png"
      title: "Domain Model Diagram"
      description: "Cart aggregate root with items and afterwardsItems"
      alt: "Domain model diagram"
      category: "architecture"

mediaItems:
  - type: "image"
    url: "https://placeholder-drugstore.com/images/cart-grpc-flow.png"
    thumbnail: "https://placeholder-drugstore.com/images/cart-grpc-flow-thumb.png"
    title: "gRPC Checkout Flow"
    description: "How order-service calls cart-service via gRPC during checkout"
    alt: "gRPC flow diagram"
    category: "diagram"

metrics:
  - label: "API Endpoints"
    value: "5+"
    description: "REST endpoints (user + admin controllers)"
    icon: "api"
    unit: "endpoints"
    trend: "stable"
    threshold: null
  - label: "gRPC Methods"
    value: "2"
    description: "GetUserCart, ClearCart for order-service"
    icon: "grpc"
    unit: "methods"
    trend: "stable"
    threshold: null
  - label: "Max Items/Cart"
    value: "100"
    description: "Maximum unique products per cart"
    icon: "cart"
    unit: "items"
    trend: "stable"
    threshold: 100
---
# Overview

> Well-structured DDD cart service with aggregate root pattern. Has unit tests for domain layer (11 test files). Potential issues: No @RateLimit annotations on REST endpoints (unlike address-service and auth-service), CartPurchasedEvent defined but not published to Kafka, Java 23 compatibility issues with Gradle 8.11. Improvements needed: Add integration tests, Kubernetes manifests, CI/CD pipeline, publish cart events to Kafka.
