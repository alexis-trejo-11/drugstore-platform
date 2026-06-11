---
# ProjectFeature[]
features:
  - id: "feat-rest-crud"
    title: "REST API v2 — store lifecycle"
    description: "Create store with nested address/contact/schedule/geo; update location & schedule; status patches (maintenance, temp closure, activate/deactivate); soft-delete style flows where implemented."
    icon: "rest"
    category: "api"
    status: "stable"
    githubExampleUrl: ""
    highlights:
      - "Command controller groups mutating verbs; query controller handles search + pagination via libs_kernel PageRequest/PageResponse."
      - "@Valid Jakarta validation on DTO graphs."
      - "**Gap:** two PATCH handlers lack `@RateLimit` annotations."
    techStack:
      - "Spring MVC"
      - "Swagger annotations"
    metrics:
      - label: "Base path"
        value: "/api/v2/stores"
        trend: "stable"
        icon: "link"
      - label: "Role for writes"
        value: "ADMIN | MANAGER"
        trend: "stable"
        icon: "shield"
    codeSnippet:
      language: "java"
      filename: "StoreCommandController.java"
      code: |
        @PostMapping
        @RateLimit(profile = RateLimitProfile.SENSITIVE)
        public ResponseEntity<ResponseWrapper<StoreID>> createStore(@Valid @RequestBody CreateStoreRequest request) {
          ...
        }

  - id: "feat-query-cache"
    title: "Search, filters & Redis caching"
    description: "Dynamic search (`SearchStoreRequest` + specifications), lookup by UUID or business code, list by StoreStatus with pageable defaults; caches `stores`, `store_searches`, `store_status` with TTL 30m."
    icon: "search"
    category: "performance"
    status: "stable"
    githubExampleUrl: ""
    highlights:
      - "Writers `@CacheEvict` allEntries on mutation methods."
      - "**Risk:** search cache keys depend on Query#toString — brittle."
    techStack:
      - "Spring Cache"
      - "Spring Data Redis"
    metrics:
      - label: "Default page size"
        value: "10"
        trend: "stable"
        icon: "layers"
    codeSnippet:
      language: "java"
      filename: "StoreQueryUseCasesImpl.java"
      code: |
        @Cacheable(value = "stores", key = "#query.id.value()")
        public Store getStoreByID(GetStoreByIDQuery query) { ... }

  - id: "feat-security-jwt"
    title: "JWT + method security matrix"
    description: "Stateless Spring Security; JwtAuthenticationFilter; GET store APIs permit anonymous access; POST/PUT/PATCH/DELETE require ADMIN or MANAGER."
    icon: "lock"
    category: "security"
    status: "stable"
    githubExampleUrl: ""
    highlights:
      - "CSRF disabled for API usage."
      - "**Mismatch:** OpenAPI still documents bearer auth for read operations."
    techStack:
      - "Spring Security"
      - "jjwt 0.11.5"
    metrics:
      - label: "Token signing"
        value: "HS256 (secret from env)"
        trend: "stable"
        icon: "key"
    codeSnippet:
      language: "java"
      filename: "SecurityConfig.java"
      code: |
        .requestMatchers(HttpMethod.GET, "/api/v2/stores/**").permitAll()

  - id: "feat-obs-docker"
    title: "Docker Compose observability bundle"
    description: "Prometheus, Loki, Grafana wired on shared network; Micrometer Prometheus registry in app; Loki Logback appender."
    icon: "chart"
    category: "ops"
    status: "develop"
    githubExampleUrl: ""
    highlights:
      - "Healthchecks on app, nginx, Postgres, Redis."
      - "**Replace default Grafana credentials** before sharing."
    techStack:
      - "Micrometer Prometheus"
      - "Grafana stack"
    metrics:
      - label: "Compose services"
        value: "7"
        trend: "stable"
        icon: "docker"
    codeSnippet:
      language: "yaml"
      filename: "docker-compose.yml (excerpt)"
      code: |
        store-service:
          depends_on:
            prometheus: { condition: service_started }
            grafana: { condition: service_started }

  - id: "feat-events-stub"
    title: "Domain events → Kafka (stub)"
    description: "Port `StoreEventPublisher` exists; implementation only logs — no KafkaTemplate send, no topic config in code path."
    icon: "kafka"
    category: "integration"
    status: "planned"
    githubExampleUrl: ""
    highlights:
      - "Compose still requires bootstrap servers — operational friction."
      - "TODO comment mentions retries not implemented."
    techStack:
      - "Spring Kafka (dependency landscape only)"
    metrics:
      - label: "Events delivered"
        value: "0 (stub)"
        trend: "stable"
        icon: "alert"
    codeSnippet:
      language: "java"
      filename: "StoreEventPublisherImpl.java"
      code: |
        public void publishStoreStatusChanged(StoreStatusChangedEvent event) {
          log.info("Publishing ... {}", event.getStoreId());
          // no broker call
        }
---

# Project Features

> [!missing] Rate limit coverage  
> Apply `@RateLimit` to **`PATCH .../temporary-closure`** and **`PATCH .../deactivate`** for parity with other state-changing endpoints.

> [!danger] Public read API  
> **All GET /api/v2/stores/** are **anonymous** — ensure no PII exposure and consider API gateway auth if catalog should be private.
