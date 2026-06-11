---
# ProjectFeature[]
features:
  - id: "user-query-api"
    title: "Authenticated user reads"
    description: "Lookup by UUID, email, phone; paginated listings by UserRole or UserStatus."
    icon: "search"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/PLACEHOLDER/drugstore-platform/blob/main/user-service/src/main/java/io/github/alexisTrejo11/drugstore/users/user/adapter/input/rest/UserQueryManagerController.java"
    highlights:
      - "Returns libs_kernel ResponseWrapper wrapping UserHTTPResponse or page content"
      - "Requires JWT for /api/** (see SecurityConfig)"
    techStack:
      - "Spring MVC"
      - "Swagger annotations"
      - "Jakarta Validation"
    metrics:
      - label: "Test coverage signal"
        value: "E2E MockMvc (UserServiceE2EIntegrationTest)"
        trend: "stable"
        icon: "test"
    codeSnippet:
      language: ""
      filename: ""
      code: ""

  - id: "user-manager-api"
    title: "Manager / admin lifecycle"
    description: "Create customer users, activate with code, ban, unban, hard delete via CommandBus."
    icon: "shield"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/PLACEHOLDER/drugstore-platform/blob/main/user-service/src/main/java/io/github/alexisTrejo11/drugstore/users/user/adapter/input/rest/UserManagerController.java"
    highlights:
      - "POST create returns 201 Created"
      - "ROLE ADMIN or MANAGER enforced on /api/v2/users/manager/**"
    techStack:
      - "Spring Security"
      - "CommandBus"
    metrics:
      - label: "Authorization surface"
        value: "Privileged — audit recommended"
        trend: "stable"
        icon: "alert"
    codeSnippet:
      language: ""
      filename: ""
      code: ""

  - id: "profile-api"
    title: "Current user profile"
    description: "GET /profile/me and PATCH profile with rate limiting hooks (libs_kernel)."
    icon: "user"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/PLACEHOLDER/drugstore-platform/blob/main/user-service/src/main/java/io/github/alexisTrejo11/drugstore/users/profile/infrastructure/web/rest/ProfileController.java"
    highlights:
      - "@RequestAttribute userId injected post-JWT filter"
      - "@SecurityRequirement bearerAuth"
    techStack:
      - "Spring MVC"
      - "RateLimit annotation"
    metrics:
      - label: "PII sensitivity"
        value: "High"
        trend: "stable"
        icon: "lock"
    codeSnippet:
      language: ""
      filename: ""
      code: ""

  - id: "kafka-consumer"
    title: "User domain event consumption"
    description: "Subscribes to configurable topics for created, updated, deleted events with manual acknowledgement."
    icon: "kafka"
    category: "messaging"
    status: "beta"
    githubExampleUrl: "https://github.com/PLACEHOLDER/drugstore-platform/blob/main/user-service/src/main/java/io/github/alexisTrejo11/drugstore/users/user/adapter/output/messaging/kafka/consumer/UserEventConsumer.java"
    highlights:
      - "**Missing DLQ / retry budget** — throws on deserialization or handler failures"
      - "**Trusted packages '*`** — widen attack surface if poison messages arrive"
    techStack:
      - "Spring Kafka"
      - "Jackson"
    metrics:
      - label: "Reliability"
        value: "At-least-once (manual ack)"
        trend: "unstable-without-DLQ"
        icon: "warning"
    codeSnippet:
      language: ""
      filename: ""
      code: ""

  - id: "grpc-contract"
    title: "gRPC protobuf contract + server class"
    description: "user_service.proto defines uniqueness checks, credential validation, CRUD-style RPCs."
    icon: "rpc"
    category: "integration"
    status: "not-wired"
    githubExampleUrl: "https://github.com/PLACEHOLDER/drugstore-platform/blob/main/user-service/src/main/proto/user_service.proto"
    highlights:
      - "**Dangerous assumption**: GRPC_SECURITY_ENABLED:false with no alternate auth"
      - "**Implementation gap**: no Netty server registration;GrpcServerConfig is empty"
    techStack:
      - "grpc-java"
      - "protobuf"
    metrics:
      - label: "Operational readiness"
        value: "0% listener"
        trend: "down"
        icon: "error"
    codeSnippet:
      language: ""
      filename: ""
      code: ""

  - id: "observability-stack"
    title: "Metrics, logs, dashboards (local)"
    description: "Prometheus scrape, Grafana + Loki in docker-compose; Micrometer Prometheus + Loki logback appender in app."
    icon: "chart"
    category: "ops"
    status: "stable-local"
    githubExampleUrl: ""
    highlights:
      - "Grafana default credentials are weak (dev only)"
    techStack:
      - "Prometheus"
      - "Grafana"
      - "Loki"
    metrics:
      - label: "Prod parity"
        value: "Placeholder — map to cloud APM (dummy: DD_SERVICE=user-service)"
        trend: "stable"
        icon: "cloud"
    codeSnippet:
      language: ""
      filename: ""
      code: ""
---

# Project Features

## Summary

Features span **REST** (query, manager, profile), **Kafka** ingestion, **gRPC contract** (not yet served), and **local observability** stack. Prioritize fixing **OpenAPI package scan** and **docker profile YAML** before treating docs or contracts as release artifacts.
