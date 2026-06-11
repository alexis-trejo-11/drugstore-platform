---
problemStatement:
  problemTitle: "Centralized Authentication in Microservices Architecture"
  problemDescription: "In a microservices e-commerce platform, authentication must be centralized to avoid duplication across services. Requirements include JWT token management, refresh token rotation, two-factor authentication, OAuth2 social login, password reset flows, and publishing auth events to other services via Kafka."
  problemList:
    - "No centralized auth service across microservices"
    - "JWT access + refresh token management needed"
    - "Two-factor authentication (TOTP) for enhanced security"
    - "OAuth2 social login (Google, GitHub, etc.)"
    - "Password reset flow with email tokens"
    - "Kafka event publishing for user lifecycle events"
    - "gRPC communication with user-service for user data"

solution:
  solutionTitle: "Comprehensive Auth Service with DDD Architecture"
  solutionList:
    - title: "DDD Orchestrator Pattern"
      description: "UseCasesOrquestrator coordinates all authentication use cases following Domain-Driven Design principles"
    - title: "JWT Token Management"
      description: "TokenFactory creates ACCESS (short-lived) and REFRESH (long-lived) JWT tokens with JJWT library"
    - title: "Multi-Channel Event Publishing"
      description: "UserEventProducer publishes to Kafka topics: user.created, user.updated, user.deleted, auth.password-changed, auth.account-activated, auth.two-factor-enabled/disabled"
    - title: "Redis Session Management"
      description: "RedisSessionRepository manages refresh token sessions with blacklisting support"
    - title: "gRPC User Service Client"
      description: "UserServiceGrpcClient communicates with user-service for user CRUD operations via Protobuf"

keyMetrics:
  metricsTitle: "Auth Service Key Metrics"
  metricsList:
    - "JWT Access Token expiration: configurable (default 15min)"
    - "JWT Refresh Token expiration: configurable (default 7 days)"
    - "Redis-backed session storage with blacklist support"
    - "Kafka integration with 7+ event topics"
    - "REST API with 12+ endpoints across 4 controllers"
    - "OAuth2 support with custom OAuth2UserService"

coverImage:
  url: "https://placeholder-drugstore.com/images/auth-service-cover.png"
  alt: "Auth Service Architecture Diagram"
  credit: "Drugstore Platform Team"

links:
  github: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/auth-service"
  demo: null
  documentation: "https://api.ecommerce.com/auth-service/swagger-ui"
  dockerHub: "https://hub.docker.com/r/alexistrejo11/auth-service"

mediaGallery:
  title: "Auth Service Media Gallery"
  description: "Screenshots and diagrams of the Auth Service"
  items:
    - type: "image"
      url: "https://placeholder-drugstore.com/images/auth-swagger.png"
      thumbnail: "https://placeholder-drugstore.com/images/auth-swagger-thumb.png"
      title: "Swagger API Documentation"
      description: "OpenAPI documentation for auth endpoints"
      alt: "Swagger UI screenshot"
      category: "screenshot"
    - type: "image"
      url: "https://placeholder-drugstore.com/images/auth-architecture.png"
      thumbnail: "https://placeholder-drugstore.com/images/auth-architecture-thumb.png"
      title: "DDD Architecture"
      description: "Domain-Driven Design layered architecture"
      alt: "Architecture diagram"
      category: "architecture"

mediaItems:
  - type: "image"
    url: "https://placeholder-drugstore.com/images/auth-jwt-flow.png"
    thumbnail: "https://placeholder-drugstore.com/images/auth-jwt-flow-thumb.png"
    title: "JWT Token Flow"
    description: "Access + Refresh token rotation flow"
    alt: "JWT flow diagram"
    category: "diagram"

metrics:
  - label: "Integration tests"
    value: "AuthEndpointsIntegrationTest"
    description: "Full-stack HTTP tests with Testcontainers Redis/Kafka + gRPC stub (Docker required)"
    icon: "test-tube"
    unit: "suite"
    trend: "stable"
    threshold: null
  - label: "API Endpoints"
    value: "12+"
    description: "Total REST endpoints across 4 controllers"
    icon: "api"
    unit: "endpoints"
    trend: "stable"
    threshold: null
  - label: "Kafka Topics"
    value: "7+"
    description: "Event types published to Kafka"
    icon: "kafka"
    unit: "topics"
    trend: "up"
    threshold: null
  - label: "Token Types"
    value: "4"
    description: "ACCESS, REFRESH, ACTIVATION, TWO_FA"
    icon: "token"
    unit: "types"
    trend: "stable"
    threshold: null
---
# Overview

> Production-ready auth service with JWT, 2FA, OAuth2, Kafka, and gRPC. PostgreSQL is not embedded locally—user records live in user-service (accessed via gRPC). **Integration tests** (`integration-test` + `test` profiles) run against Testcontainers Redis/Kafka and an in-process UserService; use Docker locally/CI. Further improvements: more unit tests, Kubernetes manifests, Circuit Breaker on gRPC, Micrometer metrics.
