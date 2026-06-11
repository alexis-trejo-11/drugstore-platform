---
# ProjectFeature[]
features:
  - id: "initiate-payment"
    title: "Initiate Payment"
    description: "Creates a payment in PENDING state, registers a Stripe PaymentIntent, transitions to PROCESSING and returns the clientSecret needed by the frontend to confirm payment."
    icon: "credit-card"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service/src/main/java/io/github/alexistrejo11/drugstore/payments/infrastructure/adapter/input/web/controller/PaymentController.java"
    highlights:
      - "Creates Payment aggregate in PENDING state"
      - "Registers Stripe PaymentIntent via gateway adapter"
      - "Returns clientSecret for frontend Stripe.js confirmation"
      - "Three-step flow: POST /payments → stripe.confirmPayment() → webhook"
    techStack:
      - "Spring Boot"
      - "Stripe API (Planned)"
      - "Domain Events"
    metrics:
      - label: "Payment Methods"
        value: "6"
        trend: "stable"
        icon: "payment"
    codeSnippet:
      language: "java"
      filename: "PaymentController.java"
      code: |
        @PostMapping
        public ResponseEntity<ResponseWrapper<PaymentResponse>> initiatePayment(
            @Valid @RequestBody InitiatePaymentRequest request) {
            PaymentResponse response = paymentService.initiatePayment(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseWrapper.created(response));
        }

  - id: "process-refund"
    title: "Process Refund"
    description: "Processes full or partial refunds for completed payments. Calls Stripe to issue refund, then updates Payment and Sale status accordingly."
    icon: "undo"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service/src/main/java/io/github/alexistrejo11/drugstore/payments/infrastructure/adapter/input/web/controller/PaymentController.java"
    highlights:
      - "Supports full refunds (amount=null) and partial refunds"
      - "Validates refundable amount before processing"
      - "Updates Payment status to REFUNDED or PARTIALLY_REFUNDED"
      - "Updates Sale status accordingly"
    techStack:
      - "Spring Boot"
      - "Stripe API (Planned)"
      - "Domain Validation"

  - id: "stripe-webhook-handler"
    title: "Stripe Webhook Handler"
    description: "Receives and processes Stripe webhook events with signature verification. Handles payment_intent.succeeded, payment_intent.payment_failed, and charge.refunded events."
    icon: "webhook"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service/src/main/java/io/github/alexistrejo11/drugstore/payments/infrastructure/adapter/input/web/controller/StripeWebhookController.java"
    highlights:
      - "Verifies Stripe-Signature header before processing"
      - "Handles payment_intent.succeeded → Payment COMPLETED → Sale CREATED"
      - "Handles payment_intent.payment_failed → Payment FAILED"
      - "Handles charge.refunded → Payment REFUNDED → Sale updated"
      - "Idempotent processing (checks payment.isCompleted() before confirming)"
      - "Always returns 200 to prevent Stripe retries"
    techStack:
      - "Spring Boot"
      - "Stripe SDK (Planned)"
      - "Spring Events"

  - id: "payment-query"
    title: "Payment Query Operations"
    description: "Retrieve payments by ID, order ID, or customer ID. Supports single payment lookup and customer payment history."
    icon: "search"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service/src/main/java/io/github/alexistrejo11/drugstore/payments/infrastructure/adapter/input/web/controller/PaymentController.java"
    highlights:
      - "GET /api/v1/payments/{paymentId} - Single payment lookup"
      - "GET /api/v1/payments/order/{orderId} - Payment by order (one-to-one)"
      - "GET /api/v1/payments/customer/{customerId} - All payments for customer"
      - "Results ordered by creation date descending"
    techStack:
      - "Spring Boot"
      - "Spring Data JPA"
      - "ResponseWrapper"

  - id: "sale-query"
    title: "Sale Query Operations"
    description: "Read-only sale records automatically created from completed payments. Sales represent confirmed business transactions."
    icon: "receipt"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service/src/main/java/io/github/alexistrejo11/drugstore/payments/infrastructure/adapter/input/web/controller/SaleController.java"
    highlights:
      - "GET /api/v1/sales/{saleId} - Single sale lookup with net amount"
      - "GET /api/v1/sales/order/{orderId} - Sale by order ID"
      - "GET /api/v1/sales/customer/{customerId} - All sales for customer"
      - "Includes confirmed, refunded, and cancelled sales"
    techStack:
      - "Spring Boot"
      - "Spring Data JPA"
      - "Domain Events"

  - id: "payment-state-machine"
    title: "Payment State Machine"
    description: "Payment aggregate with 6 states and validated transitions. Enforces business rules for payment lifecycle."
    icon: "exchange-alt"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service/src/main/java/io/github/alexistrejo11/drugstore/payments/core/domain/model/Payment.java"
    highlights:
      - "PENDING → PROCESSING → COMPLETED → (REFUNDED | PARTIALLY_REFUNDED)"
      - "PENDING → CANCELLED"
      - "PROCESSING → FAILED"
      - "Business rule validation in each state transition"
      - "Automatic timestamp tracking (PaymentTimeStamps)"
    techStack:
      - "Java 23"
      - "Domain-Driven Design"
      - "Enum-based State Machine"

  - id: "sale-state-machine"
    title: "Sale State Machine"
    description: "Sale aggregate with 4 states, automatically generated from completed payments. Represents confirmed business transactions."
    icon: "chart-line"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service/src/main/java/io/github/alexistrejo11/drugstore/payments/core/domain/model/Sale.java"
    highlights:
      - "CONFIRMED → REFUNDED | PARTIALLY_REFUNDED"
      - "CONFIRMED → CANCELLED"
      - "Created automatically from completed Payment"
      - "Tracks refunded amount and net amount"
    techStack:
      - "Java 23"
      - "Domain-Driven Design"
      - "Factory Method (fromPayment)"

  - id: "domain-events"
    title: "Domain Events for Event-Driven Architecture"
    description: "PaymentCompletedEvent and PaymentFailedEvent enable loose coupling. SpringEventPublisherAdapter publishes events for in-process handling."
    icon: "bolt"
    category: "messaging"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service/src/main/java/io/github/alexistrejo11/drugstore/payments/core/domain/events"
    highlights:
      - "PaymentCompletedEvent triggers Sale creation"
      - "PaymentFailedEvent allows other services to handle failures"
      - "SpringEventPublisherAdapter for in-process events"
      - "Extensible for Kafka/RabbitMQ migration (only adapter needs change)"
    techStack:
      - "Spring Events"
      - "Domain-Driven Design"
      - "Kafka (Planned)"

  - id: "stripe-gateway-integration"
    title: "Stripe Gateway Integration (Planned)"
    description: "StripeGatewayAdapter implements StripeGatewayPort for PaymentIntent creation, charge processing, and refund operations."
    icon: "credit-card"
    category: "integration"
    status: "experimental"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service/src/main/java/io/github/alexistrejo11/drugstore/payments/infrastructure/adapter/output/StripeGatewayAdapter.java"
    highlights:
      - "Implements StripeGatewayPort interface"
      - "createPaymentIntent() - Creates Stripe PaymentIntent"
      - "refundCharge() - Processes full refunds"
      - "partialRefundCharge() - Processes partial refunds"
      - "⚠️ CURRENTLY A STUB - Returns null/empty strings!"
    techStack:
      - "Stripe API"
      - "Spring Service"
      - "Port-Adapter Pattern"

  - id: "value-objects"
    title: "Strongly-Typed Value Objects"
    description: "Value objects (PaymentID, OrderID, CustomerID, Money, PaymentGatewayRef, RefundInfo) provide type safety and encapsulate validation logic."
    icon: "cube"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service/src/main/java/io/github/alexistrejo11/drugstore/payments/core/domain/valueobjects"
    highlights:
      - "PaymentID, OrderID, CustomerID - Strongly-typed identifiers"
      - "Money - Monetary amounts with currency support"
      - "PaymentGatewayRef - Tracks Stripe PaymentIntent and Charge IDs"
      - "RefundInfo - Captures refund details (amount, reason, gateway ID)"
      - "PaymentTimeStamps/SaleTimeStamps - Audit trail"
    techStack:
      - "Java 23"
      - "Domain-Driven Design"
      - "Value Object Pattern"

  - id: "flyway-migrations"
    title: "Database Migrations"
    description: "Versioned database schema management with Flyway for reliable and repeatable migrations."
    icon: "database"
    category: "database"
    status: "stable"
    highlights:
      - "Flyway core and PostgreSQL support in build.gradle"
      - "Migrations located in classpath:db/migration"
      - "JPA ddl-auto: validate (Flyway owns schema)"
      - "HikariCP connection pooling configured"
    techStack:
      - "Flyway"
      - "PostgreSQL"
      - "Hibernate"

  - id: "api-documentation"
    title: "OpenAPI Documentation"
    description: "Automated API documentation with Swagger UI and OpenAPI 3 annotations for all endpoints."
    icon: "book"
    category: "api"
    status: "stable"
    highlights:
      - "Swagger UI available at /swagger-ui.html"
      - "API docs at /v3/api-docs"
      - "OpenAPI annotations on all controllers"
      - "Response examples and status codes documented"
    techStack:
      - "springdoc-openapi-starter-webmvc-ui:2.6.0"
      - "Swagger UI"
---
# Project Features
> Payment Service provides 13+ features including dual aggregate roots (Payment and Sale), 6-state payment lifecycle, Stripe webhook handling, domain events, and strongly-typed value objects. The service follows DDD principles with hexagonal architecture. Stripe integration is planned but StripeGatewayAdapter is currently a STUB.

<!--
  OBSERVATIONS FOR ProjectFeature:
  ✅ POSITIVE:
    - 13 well-documented features covering all aspects of payment processing
    - Dual aggregate roots (Payment and Sale) with separate lifecycles
    - Clean hexagonal architecture with ports and adapters
    - Domain events (PaymentCompletedEvent, PaymentFailedEvent) for loose coupling
    - Strongly-typed value objects for type safety
    - Comprehensive webhook handling with Stripe signature verification
    - Flyway migrations with JPA validation mode
    - HikariCP connection pooling configured
    - OpenAPI documentation with springdoc-openapi

  ⚠️ WARNINGS / MISSING / DANGEROUS:
    - StripeGatewayAdapter IS A STUB - all methods return null or empty strings (lines 11, 16, 21)!
    - Stripe integration "Planned" but no actual Stripe SDK dependency in build.gradle
    - PaymentApplicationServiceImpl NOT FOUND - only interface exists, no implementation!
    - No unit/integration test coverage mentioned in features
    - Kafka dependency in build.gradle but NO Kafka code implemented
    - No SecurityConfig - controllers have "Security placeholder" comments
    - Spring Boot Actuator NOT in build.gradle but referenced in comments
    - No rate limiting configured (mentioned as "placeholder" in controllers)
    - SaleController says "Sales are read-only from API perspective" but no enforcement
    - Refund flow depends on StripeGatewayAdapter which is NOT implemented
    - No notification feature - customers not notified on payment completion/failure
    - PaymentMethod enum has 6 methods but Stripe may not support all (e.g., CASH, INTERNAL_CREDIT)
-->
