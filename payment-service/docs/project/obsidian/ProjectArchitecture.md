---
# ArchitectureLayer[]
layers:
  - name: "API Layer (Input Adapters)"
    description: "REST controllers handling HTTP requests, input validation, and response mapping. Three controllers: Payment, Sale, and StripeWebhook."
    color: "#4CAF50"
    expanded: true
    components:
      - "PaymentController - Payment lifecycle operations (initiate, refund, query)"
      - "SaleController - Sale queries (read-only from API)"
      - "StripeWebhookController - Stripe webhook receiver with signature verification"
    responsibilities:
      - "Accept HTTP requests with validation"
      - "Verify Stripe webhook signatures"
      - "Map requests to application service methods"
      - "Return standardized ResponseWrapper responses"
    technologies:
      - "Spring Web MVC"
      - "Spring Validation"
      - "OpenAPI/Swagger"
      - "Jackson"

  - name: "Application Layer"
    description: "Application services orchestrating domain logic execution and coordinating between aggregates."
    color: "#2196F3"
    expanded: true
    components:
      - "PaymentApplicationService - Interface defining payment and sale operations"
      - "PaymentApplicationServiceImpl - Implementation (if exists, NOT FOUND in scan)"
    responsibilities:
      - "Orchestrate domain logic execution"
      - "Handle payment initiation and refund processing"
      - "Process Stripe webhook events"
      - "Coordinate between Payment and Sale aggregates"
    technologies:
      - "Spring Boot"
      - "Lombok"
      - "Shared Kernel Library"

  - name: "Domain Layer (Core)"
    description: "Two aggregate roots (Payment and Sale) with rich business logic, domain events, and value objects following DDD principles."
    color: "#FF9800"
    expanded: true
    components:
      - "Payment - Aggregate root with 6-state lifecycle"
      - "Sale - Aggregate root auto-generated from completed payments"
      - "PaymentStatus - Enum with 6 states (PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED, REFUNDED, PARTIALLY_REFUNDED)"
      - "SaleStatus - Enum with 4 states (CONFIRMED, CANCELLED, REFUNDED, PARTIALLY_REFUNDED)"
      - "PaymentMethod - Enum for 6 payment methods"
      - "PaymentCompletedEvent - Domain event when payment succeeds"
      - "PaymentFailedEvent - Domain event when payment fails"
      - "Money - Value object for monetary amounts"
      - "PaymentGatewayRef - Value object tracking Stripe IDs"
      - "RefundInfo - Value object for refund details"
    responsibilities:
      - "Encapsulate business rules and invariants"
      - "Validate state transitions"
      - "Publish domain events"
      - "Enforce aggregate consistency boundaries"
    technologies:
      - "Java 23"
      - "Lombok"
      - "Domain-Driven Design"

  - name: "Infrastructure Layer (Output Adapters)"
    description: "Persistence, external service integration, and technical concerns implementation."
    color: "#9C27B0"
    expanded: true
    components:
      - "PaymentRepositoryImpl - JPA-based payment persistence"
      - "SaleRepositoryImpl - JPA-based sale persistence"
      - "JpaPaymentRepository - Spring Data JPA interface"
      - "JpaSaleRepository - Spring Data JPA interface"
      - "StripeGatewayAdapter - STUB implementation of StripeGatewayPort"
      - "SpringEventPublisherAdapter - Spring ApplicationEventPublisher adapter"
      - "PaymentMapper - Domain to JPA entity mapping"
      - "SaleMapper - Domain to JPA entity mapping"
    responsibilities:
      - "Implement output ports from domain"
      - "Persist domain objects"
      - "Integrate with external services (Stripe)"
      - "Publish domain events"
    technologies:
      - "Spring Data JPA"
      - "Hibernate"
      - "PostgreSQL"
      - "Redis"
      - "Stripe API (Planned)"

  - name: "Observability Layer"
    description: "Monitoring and logging infrastructure for production readiness."
    color: "#F44336"
    expanded: false
    components:
      - "Spring Boot Admin Client - Management console registration"
      - "OpenAPI/Swagger - API documentation at /v3/api-docs"
    responsibilities:
      - "Expose health and metrics endpoints (Actuator NOT in build.gradle)"
      - "Generate API documentation"
      - "Provide operational insights"
    technologies:
      - "Spring Boot Admin"
      - "SpringDoc OpenAPI"
      - "SLF4J"

# DesignPattern[]
designPatterns:
  - title: "Dual Aggregate Roots (DDD)"
    emoji: "📦"
    description: "Payment and Sale are separate aggregate roots. Sale is auto-generated from completed Payment, representing confirmed business transactions."
    category: "Domain"
    badge: "DDD"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service/src/main/java/io/github/alexistrejo11/drugstore/payments/core/domain/model"

  - title: "Factory Method"
    emoji: "🔨"
    description: "Payment.create() and Sale.fromPayment() factory methods encapsulate object creation with validation."
    category: "Creational"
    badge: "GoF"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service/src/main/java/io/github/alexistrejo11/drugstore/payments/core/domain/model/Payment.java"

  - title: "State Machine (Enum-based)"
    emoji: "🔄"
    description: "PaymentStatus (6 states) and SaleStatus (4 states) enums define valid state transitions with business rule validation."
    category: "Behavioral"
    badge: "GoF"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service/src/main/java/io/github/alexistrejo11/drugstore/payments/core/domain/model/enums"

  - title: "Port-Adapter Pattern"
    emoji: "🏛️"
    description: "StripeGatewayPort (port) and StripeGatewayAdapter (adapter) separate domain from infrastructure. Easy to swap implementations."
    category: "Architecture"
    badge: "Hexagonal"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service/src/main/java/io/github/alexistrejo11/drugstore/payments/infrastructure/adapter/output/StripeGatewayAdapter.java"

  - title: "Domain Events"
    emoji: "⚡"
    description: "PaymentCompletedEvent and PaymentFailedEvent enable loose coupling. SpringEventPublisherAdapter for in-process events."
    category: "Domain"
    badge: "DDD"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service/src/main/java/io/github/alexistrejo11/drugstore/payments/core/domain/events"

  - title: "Value Object"
    emoji: "🎯"
    description: "Strongly-typed value objects (PaymentID, OrderID, CustomerID, Money, PaymentGatewayRef) provide type safety."
    category: "Domain"
    badge: "DDD"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service/src/main/java/io/github/alexistrejo11/drugstore/payments/core/domain/valueobjects"

  - title: "Reconstruct Pattern"
    emoji: "🔄"
    description: "Payment.reconstruct() and Sale.reconstruct() factory methods for rebuilding objects from persistence (JPA entities)."
    category: "Creational"
    badge: "DDD"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service/src/main/java/io/github/alexistrejo11/drugstore/payments/core/domain/model"

  - title: "Domain Validation"
    emoji: "✅"
    description: "DomainValidation utility class for validating domain invariants (requireNonNull, requireNonBlank, requirePositive)."
    category: "Domain"
    badge: "Helper"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service/src/main/java/io/github/alexistrejo11/drugstore/payments/core/domain/validation/DomainValidation.java"

# StrategyItem[] - Scalability
scalabilityStrategies:
  - title: "Redis Caching"
    description: "Redis in build.gradle for caching frequently accessed payment/sale data (not yet implemented in code)"
  - title: "Stateless Services"
    description: "Service is stateless and can be horizontally scaled behind a load balancer"
  - title: "Database Indexing"
    description: "Flyway migrations create appropriate indexes on frequently queried columns (paymentId, orderId, customerId)"
  - title: "Event-Driven Architecture"
    description: "Domain events enable async processing. Spring Events used now, extensible for Kafka (dependency already in build.gradle)"

# StrategyItem[] - Security
securityStrategies:
  - title: "Stripe Webhook Signature Verification"
    description: "StripeWebhookVerifier validates Stripe-Signature header before processing webhook events"
  - title: "Environment Variables for Secrets"
    description: "Stripe API key and webhook secret loaded from environment variables (STRIPE_API_KEY, STRIPE_WEBHOOK_SECRET)"
  - title: "Security Placeholder"
    description: "⚠️ NO SecurityConfig found - controllers have 'Security placeholder' comments but no JWT/auth configured"
  - title: "Input Validation"
    description: "Bean Validation annotations (@Valid, @NotBlank) on all request DTOs"
  - title: "Webhook Endpoint Exclusion"
    description: "Stripe webhook endpoint should be excluded from JWT auth in SecurityFilterChain (documented in comments)"

# CacheStrategy[]
cacheStrategies:
  - name: "Payment Cache"
    description: "Planned Redis cache for frequently accessed payment data"
    ttl: "300s (planned)"
    coverage: "PaymentApplicationService (planned)"
  - name: "Sale Cache"
    description: "Planned Redis cache for sale queries"
    ttl: "600s (planned)"
    coverage: "SaleController (planned)"

# ArchitectureFeature[]
architectureFeatures:
  - title: "Dual Aggregate Roots"
    emoji: "📦"
    description: "Payment and Sale aggregates with separate lifecycles and state machines"
  - title: "Domain Events"
    emoji: "⚡"
    description: "PaymentCompletedEvent and PaymentFailedEvent for loose coupling"
  - title: "Port-Adapter Architecture"
    emoji: "🏛️"
    description: "Clean separation between domain logic and infrastructure with StripeGatewayPort"
  - title: "Strongly-Typed IDs"
    emoji: "🎯"
    description: "PaymentID, OrderID, CustomerID value objects prevent ID confusion"
  - title: "Factory Methods"
    emoji: "🔨"
    description: "create(), fromPayment(), reconstruct() methods encapsulate object creation"
  - title: "Value Objects"
    emoji: "💎"
    description: "Money, PaymentGatewayRef, RefundInfo encapsulate domain concepts"

# ArchitectureDiagramModel
architectureDiagram:
  legendItems:
    - type: "client"
      label: "Frontend (Stripe.js)"
      color: "#4CAF50"
      icon: "user"
    - type: "gateway"
      label: "Nginx (Docker edge)"
      color: "#009688"
      icon: "nginx"
    - type: "service"
      label: "Payment Service"
      color: "#FF9800"
      icon: "service"
    - type: "database"
      label: "PostgreSQL"
      color: "#9C27B0"
      icon: "database"
    - type: "queue"
      label: "Stripe"
      color: "#F44336"
      icon: "payment"
    - type: "monitoring"
      label: "Spring Boot Admin"
      color: "#607D8B"
      icon: "monitoring"
  nodes:
    - id: "frontend"
      label: "Frontend"
      type: "client"
      x: 100
      y: 50
      connections: ["nginx", "stripe"]
      status: "healthy"
      traffic: 1000
    - id: "nginx"
      label: "Nginx (TLS + LB)"
      type: "gateway"
      x: 280
      y: 50
      connections: ["payment-service"]
      status: "healthy"
      traffic: 900
    - id: "payment-service"
      label: "Payment Service"
      type: "service"
      x: 500
      y: 50
      connections: ["postgres", "stripe-api", "events"]
      status: "healthy"
      traffic: 800
    - id: "postgres"
      label: "PostgreSQL"
      type: "database"
      x: 300
      y: 150
      connections: []
      status: "healthy"
      traffic: 400
    - id: "stripe"
      label: "Stripe"
      type: "queue"
      x: 700
      y: 50
      connections: ["payment-service"]
      status: "healthy"
      traffic: 600
    - id: "stripe-api"
      label: "Stripe API"
      type: "queue"
      x: 700
      y: 120
      connections: []
      status: "warning"
      traffic: 300
    - id: "events"
      label: "Domain Events"
      type: "monitoring"
      x: 500
      y: 150
      connections: ["other-services"]
      status: "healthy"
      traffic: 200
    - id: "other-services"
      label: "Other Services"
      type: "service"
      x: 500
      y: 250
      connections: []
      status: "healthy"
      traffic: 150
  connections:
    - id: "conn-1"
      from: "frontend"
      to: "nginx"
      label: "HTTPS :443"
      protocol: "HTTPS"
      isActive: true
    - id: "conn-1b"
      from: "nginx"
      to: "payment-service"
      label: "HTTP :8080 (internal)"
      protocol: "HTTP"
      isActive: true
    - id: "conn-2"
      from: "frontend"
      to: "stripe"
      label: "Stripe.js"
      protocol: "HTTPS"
      isActive: true
    - id: "conn-3"
      from: "payment-service"
      to: "postgres"
      label: "JDBC"
      protocol: "PostgreSQL"
      isActive: true
    - id: "conn-4"
      from: "payment-service"
      to: "stripe-api"
      label: "Stripe SDK"
      protocol: "HTTPS"
      isActive: false
    - id: "conn-5"
      from: "stripe"
      to: "payment-service"
      label: "Webhook"
      protocol: "HTTPS"
      isActive: true
    - id: "conn-6"
      from: "payment-service"
      to: "events"
      label: "Domain Events"
      protocol: "Spring Events"
      isActive: true
    - id: "conn-7"
      from: "events"
      to: "other-services"
      label: "Sale Creation"
      protocol: "Event"
      isActive: true

# DataFlowModel
dataFlow:
  requestFlow:
    - number: 1
      title: "Initiate Payment (via Nginx)"
      description: "Browser uses HTTPS :443 to payment-nginx; Nginx proxies HTTP to payment-service:8080. POST /api/v1/payments with orderId, customerId, amount"
      icon: "nginx"
    - number: 2
      title: "Create Payment Aggregate"
      description: "Payment.create() builds Payment in PENDING state with validation"
      icon: "domain"
    - number: 3
      title: "Create Stripe PaymentIntent"
      description: "StripeGatewayAdapter.createPaymentIntent() - ⚠️ STUB, returns null!"
      icon: "gateway"
    - number: 4
      title: "Return clientSecret"
      description: "PaymentResponse with clientSecret sent to frontend"
      icon: "response"
    - number: 5
      title: "Frontend Confirms Payment"
      description: "Frontend calls stripe.confirmPayment(clientSecret) which calls Stripe"
      icon: "stripe"
    - number: 6
      title: "Stripe Sends Webhook"
      description: "Stripe calls POST /api/v1/webhooks/stripe with event data"
      icon: "webhook"
    - number: 7
      title: "Verify & Process Webhook"
      description: "StripeWebhookVerifier validates signature, PaymentApplicationService handles event"
      icon: "security"
    - number: 8
      title: "Complete Payment & Create Sale"
      description: "Payment.complete() → PaymentCompletedEvent → Sale.fromPayment()"
      icon: "success"
  eventFlow:
    - number: 1
      title: "PaymentCompletedEvent Published"
      description: "SpringEventPublisherAdapter publishes event via Spring ApplicationEventPublisher"
      icon: "event"
    - number: 2
      title: "Sale Created"
      description: "Application layer listens to event and creates Sale aggregate from completed Payment"
      icon: "sale"
    - number: 3
      title: "Other Services Notified"
      description: "Order Service can listen to event to update order status to CONFIRMED"
      icon: "notification"

# TechDecisionsModel
techDecisions:
  decisions:
    - title: "Dual Aggregate Roots (Payment + Sale)"
      problem: "Need to separate payment processing lifecycle from confirmed business transaction representation"
      solution: "Payment aggregate handles payment lifecycle; Sale aggregate auto-generated from completed payments"
      alternatives: ["Single Payment entity with sale flag", "Only Payment, no Sale", "Separate microservices"]
      outcome: "Clear separation of concerns, Sale represents confirmed business transaction, easier auditing"
      icon: "domain"

    - title: "Java 23 with Records and Pattern Matching"
      problem: "Need modern language features for concise, type-safe DTOs and value objects"
      solution: "Used Java 23 with records for DTOs and modern language features"
      alternatives: ["Java 17 (LTS)", "Kotlin", "Scala"]
      outcome: "Concise code, immutable DTOs, pattern matching for type-safe operations"
      icon: "language"

    - title: "Spring Boot 3.3.2 with Spring Cloud"
      problem: "Need a mature framework for building microservices with cloud-native features"
      solution: "Chose Spring Boot 3.3.2 with Spring Cloud 2023.0.3 for dependency management"
      alternatives: ["Quarkus", "Micronaut", "Dropwizard"]
      outcome: "Rich ecosystem, easy integration with Spring Cloud services, extensive community support"
      icon: "spring"

    - title: "PostgreSQL with Flyway Migrations"
      problem: "Need reliable relational storage with versioned schema management"
      solution: "PostgreSQL as primary database with Flyway for migrations, JPA ddl-auto: validate"
      alternatives: ["MySQL", "H2 (for tests)", "MongoDB"]
      outcome: "ACID transactions, JSON support, reliable schema versioning, Hibernate only validates"
      icon: "database"

    - title: "Stripe Gateway Adapter (STUB)"
      problem: "Need to integrate with Stripe for payment processing but implementation pending"
      solution: "Created StripeGatewayAdapter implementing StripeGatewayPort - currently a STUB returning null/empty"
      alternatives: ["PayPal", "Square", "Implement later"]
      outcome: "⚠️ STUB - needs actual Stripe SDK integration, all methods return null!"
      icon: "payment"

    - title: "Spring Events for Domain Events"
      problem: "Need to publish domain events for in-process handling (Sale creation from Payment)"
      solution: "SpringEventPublisherAdapter implements PaymentEventPublisher port using Spring ApplicationEventPublisher"
      alternatives: ["Kafka (added as dependency but not implemented)", "RabbitMQ", "Direct method calls"]
      outcome: "Synchronous in-process events, easy to migrate to Kafka later (only adapter changes)"
      icon: "events"

    - title: "Strongly-Typed Value Objects"
      problem: "Need to prevent ID confusion and encapsulate monetary calculations with currency"
      solution: "Created PaymentID, OrderID, CustomerID, Money, PaymentGatewayRef, RefundInfo value objects"
      alternatives: ["Use String/BigDecimal everywhere", "Use primitives", "Lighter approach"]
      outcome: "Type safety, encapsulates validation, prevents ID mix-ups, clearer domain model"
      icon: "value"

    - title: "Kafka Dependency (Not Implemented)"
      problem: "Future need for event streaming and async communication between microservices"
      solution: "Added spring-kafka dependency in build.gradle but NO implementation yet"
      alternatives: ["RabbitMQ", "Wait until needed", "Implement now"]
      outcome: "⚠️ Dependency added but no code - compile-time dependency with no runtime usage"
      icon: "queue"
---
# Architecture
> Payment Service follows hexagonal architecture with DDD principles. Two aggregate roots (Payment and Sale) with rich business logic. Ports define contracts; adapters implement them. Design patterns include Factory Method, State Machine (enum-based), Port-Adapter, Domain Events, and Value Object. StripeGatewayAdapter is a STUB - needs implementation. Kafka dependency added but not implemented.

<!--
  OBSERVATIONS FOR ProjectArchitecture:
  ✅ POSITIVE:
    - Clean hexagonal architecture with 5 well-defined layers
    - 8 design patterns identified and documented (DDD, GoF, and custom)
    - Dual aggregate roots with separate state machines
    - State machines implemented in PaymentStatus (6 states) and SaleStatus (4 states) enums
    - Domain events (PaymentCompletedEvent, PaymentFailedEvent) for loose coupling
    - Architecture diagram with 8 nodes and 7 connections defined
    - Data flow documented for both request/response and event flows
    - 8 tech decisions documented with alternatives and outcomes
    - Strongly-typed value objects for type safety
    - Factory methods (create, fromPayment, reconstruct) for object creation

  ⚠️ WARNINGS / MISSING / DANGEROUS:
    - StripeGatewayAdapter IS A STUB - all methods return null or empty strings!
    - PaymentApplicationServiceImpl NOT FOUND - only interface exists!
    - Kafka dependency in build.gradle but NO Kafka code implemented
    - NO SecurityConfig - "Security placeholder" in controllers, no JWT/auth configured
    - API Gateway shown in architecture diagram but NOT IMPLEMENTED (marked as "Planned")
    - Stripe API connection (conn-4) marked as INACTIVE - adapter is a stub!
    - Spring Boot Actuator NOT in build.gradle dependencies (only Admin Client)
    - No circuit breaker pattern implemented for resilience
    - No API versioning strategy documented beyond "/api/v1" path
    - Shared kernel library (io.github.alexistrejo11:shared-kernel:1.0.0) dependency not versioned with project
    - SaleController says "Sales are read-only" but no enforcement at API level
    - No CORS configuration documented
    - Service runs on HTTP (port 8085) - NO SSL/HTTPS configured (unlike order-service)
    - HikariCP max-lifetime: 1200000ms (20 minutes) - verify this is correct (typo? Should be 120000?)
-->
