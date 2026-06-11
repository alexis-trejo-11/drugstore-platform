---
problemStatement:
  problemTitle: "Payment Processing Complexity in Microservices Architecture"
  problemDescription: "A drugstore platform needs a reliable payment processing system that can handle multiple payment methods, integrate with external payment gateways (Stripe), manage the complete payment lifecycle including refunds, and automatically generate sales records. The system must handle webhooks from payment providers, maintain idempotency, and provide traceability between payments and orders."
  problemList:
    - "Managing complex payment state transitions (PENDING → PROCESSING → COMPLETED → REFUNDED)"
    - "Integrating with external payment gateway (Stripe) for PaymentIntents and refunds"
    - "Handling asynchronous webhook events from Stripe with signature verification"
    - "Managing two aggregate roots (Payment and Sale) with different lifecycles"
    - "Supporting multiple payment methods (Credit Card, Debit, Cash, Bank Transfer, Digital Wallet)"
    - "Processing full and partial refunds with gateway synchronization"
    - "Maintaining idempotency for webhook event processing"
    - "Providing traceability between Payment, Sale, and Order entities"

solution:
  solutionTitle: "DDD with Dual Aggregate Roots and Stripe Integration"
  solutionList:
    - title: "Dual Aggregate Roots"
      description: "Payment aggregate manages payment lifecycle; Sale aggregate is auto-generated from completed payments representing confirmed business transactions"
    - title: "Stripe Integration (Planned)"
      description: "StripeGatewayAdapter implements StripeGatewayPort for PaymentIntent creation, charge processing, and refund operations"
    - title: "Webhook Handling"
      description: "Dedicated StripeWebhookController with signature verification, event parsing, and idempotent processing"
    - title: "State Machine Implementation"
      description: "PaymentStatus (6 states) and SaleStatus (4 states) enums with business rule validation for transitions"
    - title: "Domain Events"
      description: "PaymentCompletedEvent and PaymentFailedEvent enable loose coupling and trigger Sale creation"
    - title: "Spring Event Publishing"
      description: "SpringEventPublisherAdapter implements PaymentEventPublisher port for in-process domain event publishing"
    - title: "Value Objects"
      description: "Strongly-typed value objects (PaymentID, OrderID, CustomerID, Money, PaymentGatewayRef, RefundInfo) for type safety"

coverImage:
  url: "/assets/projects/payment-service-cover.png"
  alt: "Payment Service Architecture Diagram"
  credit: "Drugstore Platform Team"

links:
  github: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service"
  demo: null
  documentation: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service/docs"
  dockerHub: null

mediaGallery:
  title: "Payment Service Screenshots and Diagrams"
  description: "Visual representations of the payment service architecture and API"
  items:
    - type: "image"
      url: "/assets/projects/payment-service-api-swagger.png"
      thumbnail: "/assets/projects/payment-service-api-swagger-thumb.png"
      title: "Swagger API Documentation"
      description: "OpenAPI documentation showing payment and sale endpoints"
      alt: "Swagger UI for Payment Service"
      category: "screenshot"
    - type: "image"
      url: "/assets/projects/payment-service-state-machine.png"
      thumbnail: "/assets/projects/payment-service-state-machine-thumb.png"
      title: "Payment State Machine"
      description: "Visual representation of Payment and Sale status transitions"
      alt: "Payment Status State Machine"
      category: "diagram"
    - type: "image"
      url: "/assets/projects/payment-service-stripe-flow.png"
      thumbnail: "/assets/projects/payment-service-stripe-flow-thumb.png"
      title: "Stripe Integration Flow"
      description: "PaymentIntent creation to webhook confirmation flow"
      alt: "Stripe Payment Flow"
      category: "diagram"

mediaItems:
  - type: "image"
    url: "/assets/projects/payment-service-api-swagger.png"
    thumbnail: "/assets/projects/payment-service-api-swagger-thumb.png"
    title: "Swagger API Documentation"
    description: "OpenAPI documentation showing payment and sale endpoints"
    alt: "Swagger UI for Payment Service"
    category: "screenshot"
  - type: "image"
    url: "/assets/projects/payment-service-state-machine.png"
    thumbnail: "/assets/projects/payment-service-state-machine-thumb.png"
    title: "Payment State Machine"
    description: "Visual representation of Payment and Sale status transitions"
    alt: "Payment Status State Machine"
    category: "diagram"

metrics:
  - label: "Total Endpoints"
    value: "9"
    description: "Number of REST API endpoints (Payments + Sales + Webhooks)"
    icon: "api"
    unit: "endpoints"
    trend: "stable"
    threshold: null
  - label: "Payment Statuses"
    value: "6"
    description: "Total number of payment status states in the state machine"
    icon: "state"
    unit: "statuses"
    trend: "stable"
    threshold: null
  - label: "Sale Statuses"
    value: "4"
    description: "Total number of sale status states"
    icon: "state"
    unit: "statuses"
    trend: "stable"
    threshold: null
  - label: "Payment Methods"
    value: "6"
    description: "Supported payment methods including Credit Card, Debit, Cash, etc."
    icon: "payment"
    unit: "methods"
    trend: "stable"
    threshold: null
  - label: "Domain Events"
    value: "2+"
    description: "Domain events: PaymentCompletedEvent, PaymentFailedEvent"
    icon: "event"
    unit: "events"
    trend: "stable"
    threshold: null
  - label: "Aggregate Roots"
    value: "2"
    description: "Payment and Sale aggregate roots with separate lifecycles"
    icon: "domain"
    unit: "aggregates"
    trend: "stable"
    threshold: null
---
# Overview
> Payment Service is a core microservice implementing DDD with two aggregate roots (Payment and Sale). It manages the complete payment lifecycle with Stripe integration (planned), webhook handling with signature verification, refund processing, and automatic Sale generation. The service uses PostgreSQL for persistence, Redis for caching, and Spring Events for domain event publishing.

<!--
  OBSERVATIONS FOR ProjectOverview:
  ✅ POSITIVE:
    - Clear problem statement with 8 identified sub-problems
    - Solution uses industry best practices (DDD, dual aggregates)
    - Supports 6 payment methods with extensible enum
    - Webhook handling with Stripe signature verification
    - Strongly-typed value objects for type safety
    - Domain events for loose coupling between Payment and Sale
    - Comprehensive lifecycle documentation for both aggregates

  ⚠️ WARNINGS / MISSING / DANGEROUS:
    - mediaGallery items reference placeholder asset paths (/assets/projects/...) - need actual screenshots
    - coverImage URL is placeholder - no actual cover image uploaded
    - StripeGatewayAdapter IS A STUB - returns null/empty strings (line 11, 16, 21)!
    - "PLANNED" in description but Stripe dependency not in build.gradle (only commented)
    - No SecurityConfig - controllers have "Security placeholder" comments
    - Payment and Sale timeStamps use now() but no timezone configuration mentioned
    - API response time not benchmarked - no performance metrics yet
    - No screenshot of Swagger UI or architecture diagram exists yet
    - demo field is null - no live demo to showcase
    - Refund flow depends on StripeGatewayAdapter which is not implemented
-->
