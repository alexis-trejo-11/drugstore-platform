---
# ArchitectureLayer[]
layers:
  - name: "Controllers (Inbound Adapters)"
    description: "REST API layer handling HTTP requests with rate limiting and input validation"
    color: "#4CAF50"
    expanded: true
    components:
      - "LoginController - Login, 2FA login, refresh session endpoints"
      - "RegisterController - Customer/Employee/Admin registration"
      - "PasswordAuthController - Forgot/reset/change password flows"
      - "LogoutController - Logout single session or all sessions"
      - "TwoFactorConfigController - Enable/disable 2FA, send validation codes"
    responsibilities:
      - "Request validation via Jakarta Validation (@Valid)"
      - "Rate limiting with @RateLimit annotation"
      - "DTO mapping (Request -> Command pattern)"
      - "Response wrapping with ResponseWrapper"
    technologies:
      - "Spring Web MVC"
      - "libs-kernel (shared ResponseWrapper)"
      - "Jakarta Validation"

  - name: "Application Layer (Use Cases)"
    description: "DDD Application Services coordinating domain logic and orchestrating use cases"
    color: "#2196F3"
    expanded: true
    components:
      - "UseCasesOrquestrator - Main orchestrator implementing 4 interfaces"
      - "LoginUseCase - Regular and 2FA login logic"
      - "RegisterUseCase - User registration with role assignment"
      - "PasswordUseCases - Forgot/reset/change password flows"
      - "LogoutUseCases - Single and all sessions logout"
      - "TwoFaConfigUseCases - 2FA enable/disable/send code"
      - "TokenUseCases - Refresh access tokens"
      - "ActivateAccountUseCase - Account activation via token"
    responsibilities:
      - "Command object pattern (LoginCommand, SignupCommand, etc.)"
      - "Result objects (SessionPayload, SignUpResult)"
      - "Coordination between domain and infrastructure"
      - "Event publishing to Kafka"
    technologies:
      - "Spring Service"
      - "Domain-Driven Design"
      - "Command Pattern"

  - name: "Domain Layer"
    description: "Core business logic with entities, value objects, and domain events"
    color: "#FF9800"
    expanded: false
    components:
      - "User.java - Core user entity with roles"
      - "Value Objects: Email, Password, UserId, UserRole, Token, SessionId, OAuth2Provider, ExternalIDs, PhoneNumber"
      - "Domain Events: UserRegisteredEvent, UserLoginEvent, PasswordChangedEvent, AccountActivatedEvent, TwoFactorEnabled/DisabledEvent"
      - "JWTSessions - JWT session management model"
    responsibilities:
      - "Business rule enforcement"
      - "Domain event generation"
      - "Value object validation"
    technologies:
      - "DDD Domain Model"
      - "Value Objects Pattern"

  - name: "Output Adapters (Infrastructure)"
    description: "Adapters implementing outbound ports: persistence, messaging, external services"
    color: "#9C27B0"
    expanded: false
    components:
      - "TokenManager - JWT and numeric token management (implements TokenService)"
      - "TokenFactory - Creates ACCESS, REFRESH, ACTIVATION, TWO_FA tokens"
      - "RedisSessionRepository - Refresh token session storage in Redis"
      - "RedisTokenRepository - Non-JWT token persistence"
      - "UserEventProducer - Kafka event publishing (7+ event types)"
      - "NotificationEventProducer - Password reset/activation emails"
      - "UserServiceGrpcClient - gRPC client for user-service"
      - "BCryptPasswordEncoder - Password hashing"
      - "OAuth2AuthenticationSuccessHandler - OAuth2 login handling"
    responsibilities:
      - "Token generation and validation"
      - "Session persistence in Redis"
      - "Kafka event publishing"
      - "gRPC communication with user-service"
      - "OAuth2 authentication flow"
    technologies:
      - "Spring Data Redis"
      - "Spring Kafka"
      - "gRPC/Protobuf"
      - "JJWT 0.11.5"
      - "Spring OAuth2 Client"
      - "BCrypt"

  - name: "Configuration Layer"
    description: "Cross-cutting concerns: security, rate limiting, Kafka, gRPC, CORS"
    color: "#F44336"
    expanded: false
    components:
      - "SecurityConfig - JWT filter chain, OAuth2, session management"
      - "RateLimitAspect - AOP aspect for Redis rate limiting"
      - "RedisRateLimiter - Token bucket rate limiter"
      - "KafkaProducerConfig - Kafka template configuration"
      - "KafkaTopicConfig - Topic definitions"
      - "GrpcConfig - gRPC channel setup"
      - "CORSConfig - Cross-origin resource sharing"
      - "GlobalExceptionHandler - Centralized exception handling"
      - "AuditLoggerConfig - Audit logging setup"
      - "AuthServiceApplication — @ComponentScan(io.github.alexisTrejo11.drugstore.accounts, libs_kernel JWT/rate-limit)"
      - "Integration tests — Testcontainers (Redis, Kafka) + Netty in-process UserService gRPC stub (src/test/.../integration/)"
    responsibilities:
      - "Security filter chain configuration"
      - "Rate limiting enforcement"
      - "Kafka topic management"
      - "gRPC channel configuration"
      - "Global exception handling"
    technologies:
      - "Spring Security"
      - "Spring AOP"
      - "Spring Kafka"
      - "Spring Data Redis"
      - "libs-kernel (shared JWT filter)"

# DesignPattern[]
designPatterns:
  - title: "Orchestrator Pattern"
    emoji: "🎯"
    description: "UseCasesOrquestrator coordinates all authentication use cases, implementing 4 port interfaces (AuthUseCases, LogoutUseCases, PasswordUseCases, RegisterUseCases, TwoFaConfigUseCases)"
    category: "Architectural"
    badge: "DDD"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/auth-service/src/main/java/io/github/alexisTrejo11/drugstore/accounts/auth/core/application/UseCasesOrquestrator.java"

  - title: "Command Pattern"
    emoji: "📋"
    description: "Each action has a Command object (LoginCommand, SignupCommand, ChangePasswordCommand) for clear intent and validation"
    category: "Behavioral"
    badge: "CQRS-like"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/auth-service/src/main/java/io/github/alexisTrejo11/drugstore/accounts/auth/core/application/command"

  - title: "Value Object Pattern"
    emoji: "🧱"
    description: "Domain values wrapped in objects: Email, Password (BCrypt hashed), UserId, UserRole (enum), Token, SessionId, PhoneNumber, OAuth2Provider, ExternalIDs"
    category: "Domain"
    badge: "DDD"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/auth-service/src/main/java/io/github/alexisTrejo11/drugstore/accounts/auth/core/domain/valueobjects"

  - title: "Factory Pattern"
    emoji: "🏭"
    description: "TokenFactory creates different token types (ACCESS, REFRESH JWTs; ACTIVATION, TWO_FA numeric) based on TokenType enum"
    category: "Creational"
    badge: "Factory"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/auth-service/src/main/java/io/github/alexisTrejo11/drugstore/accounts/auth/adapter/output/security/tokens/factory/TokenFactory.java"

  - title: "Domain Events"
    emoji: "📢"
    description: "Events published to Kafka: UserRegisteredEvent, UserCreatedEvent, UserUpdatedEvent, UserDeletedEvent, UserLoginEvent, PasswordChangedEvent, AccountActivatedEvent, TwoFactorEnabled/DisabledEvent"
    category: "Behavioral"
    badge: "Event-Driven"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/auth-service/src/main/java/io/github/alexisTrejo11/drugstore/accounts/auth/core/domain/event"

  - title: "Adapter Pattern"
    emoji: "🔌"
    description: "Output adapters implement port interfaces: TokenManager (TokenService), UserEventProducer (UserEventPublisher), UserServiceGrpcClient (UserServiceClient), RedisSessionRepository (SessionRepository)"
    category: "Structural"
    badge: "Ports & Adapters"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/auth-service/src/main/java/io/github/alexisTrejo11/drugstore/accounts/auth/adapter/output"

# StrategyItem[] - Scalability
scalabilityStrategies:
  - title: "Nginx Load Balancing"
    description: "Nginx upstream block uses `least_conn` and Docker DNS expansion of the auth-service hostname so `docker compose up --scale auth-service=N` requires zero config changes"
  - title: "Stateless JWT Authentication"
    description: "Access tokens are stateless JWTs, allowing horizontal scaling without session replication"
  - title: "Redis for Sessions"
    description: "Refresh token sessions stored in Redis for distributed session management across service instances"
  - title: "Kafka Event Publishing"
    description: "Asynchronous event publishing to Kafka for loosely coupled inter-service communication"
  - title: "gRPC for User Service"
    description: "High-performance gRPC communication with user-service using Protobuf serialization"

# StrategyItem[] - Security
securityStrategies:
  - title: "JWT Access + Refresh Tokens"
    description: "Short-lived access tokens (15min) with long-lived refresh tokens (7 days) for secure session management"
  - title: "Two-Factor Authentication (TOTP)"
    description: "Time-based One-Time Password support via TokenFactory generating 6-digit codes with 5min expiration"
  - title: "OAuth2 Social Login"
    description: "Support for external OAuth2 providers (Google, GitHub, etc.) via Spring OAuth2 Client"
  - title: "BCrypt Password Encoding"
    description: "Passwords hashed with BCrypt adaptive one-way function via BCryptPasswordEncoder"
  - title: "Redis Rate Limiting"
    description: "Distributed rate limiting via Redis with SENSITIVE profile (10req/min) for auth endpoints"
  - title: "Nginx TLS Termination"
    description: "Nginx terminates TLS at the edge (self-signed certs for dev). Internal Docker communication uses HTTPS with `proxy_ssl_verify off` — all external traffic enters only through Nginx on port 443"
  - title: "Token Blacklisting"
    description: "RedisSessionRepository supports session blacklisting for immediate token revocation"

# CacheStrategy[]
cacheStrategies:
  - name: "Redis Session Cache"
    description: "Refresh token sessions stored in Redis with configurable TTL matching refresh token expiration"
    ttl: "7 days (configurable via jwt.refresh-expiration-seconds)"
    coverage: "All refresh token sessions"
  - name: "Redis Rate Limit Cache"
    description: "Rate limit counters in Redis with 1-minute TTL for token bucket algorithm"
    ttl: "1 minute"
    coverage: "All endpoints with @RateLimit annotation"

# ArchitectureFeature[]
architectureFeatures:
  - title: "DDD Bounded Context"
    emoji: "🏰"
    description: "Authentication as a separate bounded context with clear domain model and ports/adapters architecture"
  - title: "Multi-Role Support"
    emoji: "👥"
    description: "Supports CUSTOMER, EMPLOYEE, and ADMIN roles with role-specific registration endpoints"
  - title: "Token Type Flexibility"
    emoji: "🎫"
    description: "Four token types: ACCESS (JWT), REFRESH (JWT), ACTIVATION (numeric), TWO_FA (numeric) via TokenFactory"
  - title: "Event-Driven Architecture"
    emoji: "⚡"
    description: "Publishes 8+ event types to Kafka for asynchronous inter-service communication"
  - title: "Integration test stack"
    emoji: "🧪"
    description: "AuthEndpointsIntegrationTest with Spring Boot, Testcontainers Redis/Kafka, profiles integration-test+test, and InMemoryUserGrpcServer for full HTTP flows without mocking core adapters"

# ArchitectureDiagramModel
architectureDiagram:
  legendItems:
    - type: "client"
      label: "Client"
      color: "#4CAF50"
      icon: "user"
    - type: "gateway"
      label: "Nginx"
      color: "#009688"
      icon: "nginx"
    - type: "service"
      label: "Auth Service"
      color: "#2196F3"
      icon: "spring"
    - type: "database"
      label: "Redis"
      color: "#9C27B0"
      icon: "redis"
    - type: "queue"
      label: "Kafka"
      color: "#FF9800"
      icon: "kafka"
    - type: "gateway"
      label: "User Service (gRPC)"
      color: "#F44336"
      icon: "grpc"
    - type: "monitoring"
      label: "Actuator"
      color: "#607D8B"
      icon: "health"

  nodes:
    - id: "client"
      label: "Frontend/Client"
      type: "client"
      x: 100
      y: 100
      connections: ["nginx"]
      status: "healthy"
      traffic: 150

    - id: "nginx"
      label: "Nginx (TLS + LB)"
      type: "gateway"
      x: 250
      y: 100
      connections: ["auth-service"]
      status: "healthy"
      traffic: 150

    - id: "auth-service"
      label: "Auth Service"
      type: "service"
      x: 400
      y: 100
      connections: ["redis", "kafka", "user-service-grpc", "actuator"]
      status: "healthy"
      traffic: 120

    - id: "redis"
      label: "Redis 7"
      type: "database"
      x: 250
      y: 250
      connections: []
      status: "healthy"
      traffic: 80

    - id: "kafka"
      label: "Kafka"
      type: "queue"
      x: 550
      y: 250
      connections: []
      status: "healthy"
      traffic: 50

    - id: "user-service-grpc"
      label: "User Service"
      type: "gateway"
      x: 400
      y: 250
      connections: []
      status: "healthy"
      traffic: 40

    - id: "actuator"
      label: "Actuator/Health"
      type: "monitoring"
      x: 400
      y: 400
      connections: []
      status: "healthy"
      traffic: 10

  connections:
    - id: "conn1"
      from: "client"
      to: "nginx"
      label: "HTTPS :443"
      protocol: "HTTPS"
      isActive: true
    - id: "conn1b"
      from: "nginx"
      to: "auth-service"
      label: "HTTPS (internal)"
      protocol: "HTTPS"
      isActive: true
    - id: "conn2"
      from: "auth-service"
      to: "redis"
      label: "Sessions + Rate Limit"
      protocol: "RESP"
      isActive: true
    - id: "conn3"
      from: "auth-service"
      to: "kafka"
      label: "Events"
      protocol: "Kafka"
      isActive: true
    - id: "conn4"
      from: "auth-service"
      to: "user-service-grpc"
      label: "gRPC/Protobuf"
      protocol: "HTTP/2"
      isActive: true
    - id: "conn5"
      from: "auth-service"
      to: "actuator"
      label: "Health Checks"
      protocol: "HTTP"
      isActive: true

# DataFlowModel
dataFlow:
  requestFlow:
    - number: 1
      title: "Client Request → Nginx"
      description: "Client sends HTTPS request to Nginx on :443. Nginx terminates TLS and load-balances to an auth-service replica (least_conn)"
      icon: "nginx"
    - number: 2
      title: "Rate Limit Check"
      description: "Request forwarded by Nginx to auth-service. RateLimitAspect checks Redis for rate limit compliance (SENSITIVE: 10/min)"
      icon: "speed"
    - number: 3
      title: "Use Case Orchestration"
      description: "UseCasesOrquestrator delegates to LoginUseCase with LoginCommand"
      icon: "orchestrator"
    - number: 4
      title: "gRPC User Lookup"
      description: "UserServiceGrpcClient retrieves user details from user-service via gRPC"
      icon: "grpc"
    - number: 5
      title: "Token Generation"
      description: "TokenFactory creates ACCESS + REFRESH JWTs via TokenManager"
      icon: "token"
    - number: 6
      title: "Session Storage"
      description: "RedisSessionRepository stores refresh token in Redis"
      icon: "save"
    - number: 7
      title: "Event Publishing"
      description: "UserEventProducer publishes UserLoginEvent to Kafka"
      icon: "kafka"
    - number: 8
      title: "Response"
      description: "SessionResponse with access/refresh tokens returned to client"
      icon: "check"

  eventFlow:
    - number: 1
      title: "User Registered"
      description: "UserRegisteredEvent → Kafka → user-registered topic → other services create user profile"
      icon: "event"
    - number: 2
      title: "User Created in User-Service"
      description: "UserCreatedEvent → Kafka → user.created topic → notification-service sends welcome email"
      icon: "event"
    - number: 3
      title: "Password Changed"
      description: "PasswordChangedEvent → Kafka → auth.password-changed topic → audit log"
      icon: "event"
    - number: 4
      title: "2FA Enabled/Disabled"
      description: "TwoFactorEnabledEvent/TwoFactorDisabledEvent → Kafka → auth.two-factor-* topics"
      icon: "event"

# TechDecisionsModel
techDecisions:
  - title: "DDD Ports & Adapters Architecture"
    problem: "Need clean separation between domain logic and infrastructure concerns in auth service"
    solution: "Implemented Ports & Adapters: core/ports (interfaces) + adapter/output (implementations) + core/application (use cases)"
    outcome: "Testable domain logic, swappable infrastructure (Redis, Kafka, gRPC) without affecting domain"
    icon: "architecture"
    alternatives:
      - "Layered architecture (less isolation)"
      - "Hexagonal architecture (similar but more complex)"
      - "Anemic model with logic in services (less DDD)"

  - title: "JWT + Redis Sessions"
    problem: "Need secure session management with ability to revoke sessions"
    solution: "Short-lived JWT access tokens (stateless) + long-lived refresh tokens stored in Redis (stateful sessions)"
    outcome: "Scalable reads (JWT), revocable sessions (Redis blacklist), 7-day refresh token TTL"
    icon: "security"
    alternatives:
      - "Stateful sessions only (doesn't scale)"
      - "JWT only (can't revoke before expiration)"
      - "OAuth2 authorization server (overkill for internal auth)"

  - title: "gRPC for User-Service Communication"
    problem: "Auth service needs to communicate with user-service for user CRUD operations"
    solution: "Used gRPC with Protobuf for high-performance, type-safe communication between services"
    outcome: "Low latency (~10ms), type safety via Protobuf, HTTP/2 multiplexing"
    icon: "grpc"
    alternatives:
      - "REST API (slower, JSON overhead)"
      - "Shared database (tight coupling)"
      - "Message queue (async, not suitable for synchronous user lookup)"

  - title: "Kafka for Event Publishing"
    problem: "Other services need to react to auth events (user registered, password changed, etc.)"
    solution: "Publish domain events to Kafka topics with CompletableFuture for non-blocking publishing"
    outcome: "Loosely coupled services, async event processing, event sourcing capability"
    icon: "kafka"
    alternatives:
      - "REST calls to each service (tight coupling, cascading failures)"
      - "Direct database writes (shared DB anti-pattern)"
      - "RabbitMQ (Kafka chosen for event streaming)"

  - title: "TokenFactory for Multiple Token Types"
    problem: "Need different token types: access JWT, refresh JWT, activation codes, 2FA codes"
    solution: "TokenFactory creates tokens based on TokenType enum: JWTs for access/refresh, numeric for activation/2FA"
    outcome: "Single factory for all token creation, consistent token interface, configurable expiration"
    icon: "factory"
    alternatives:
      - "Separate factories for each token type (more code)"
      - "Only JWTs (no support for email activation/2FA codes)"
      - "UUID for all tokens (less user-friendly for 2FA)"

  - title: "Integration tests with Testcontainers + gRPC stub"
    problem: "Auth depends on Redis, Kafka, and user-service; tests must validate wiring and real token/session behavior."
    solution: "JUnit @SpringBootTest + Testcontainers for Redis/Kafka; Netty gRPC server with InMemoryUserGrpcServer; application-integration-test.yml; @Testcontainers(disabledWithoutDocker = true) when Docker is absent"
    outcome: "End-to-end HTTP scenarios (register→activate→login, password reset, 2FA) without stubbing the auth domain layer"
    icon: "test-tube"
    alternatives:
      - "Mock every port (misses serialization and Redis key shapes)"
      - "Dedicated always-on user-service in CI (slow, brittle)"
---
# Architecture

> Well-structured DDD auth service with clear bounded context. **Integration tests** exercise Redis, Kafka, and an in-process UserService implementation. Further improvements: Circuit Breaker (Resilience4j) for gRPC to user-service, Micrometer metrics, Kubernetes manifests, CI/CD, refresh token rotation with reuse detection.
