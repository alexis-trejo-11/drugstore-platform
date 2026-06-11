---
codeExamples:
  - id: "ex-security-chain"
    title: "Security filter chain — anonymous reads vs role-gated writes"
    description: "Shows CSRF off, JWT filter, and the split between permitAll GET under /api/v2/stores and verb-specific hasAnyRole for mutations."
    category: "security"
    duration: "5 min read"
    views: 0
    tags:
      - "Spring Security"
      - "JWT"
    files:
      - name: "SecurityConfig.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/stores/config/security/SecurityConfig.java"
        language: "java"
        highlighted: true
        content: |
          .requestMatchers(HttpMethod.GET, "/api/v2/stores/**").permitAll()
          .requestMatchers(HttpMethod.POST, "/api/v2/stores/**").hasAnyRole("ADMIN", "MANAGER")
          .requestMatchers(HttpMethod.PUT, "/api/v2/stores/**").hasAnyRole("ADMIN", "MANAGER")
          .requestMatchers(HttpMethod.PATCH, "/api/v2/stores/**").hasAnyRole("ADMIN", "MANAGER")
          .requestMatchers(HttpMethod.DELETE, "/api/v2/stores/**").hasAnyRole("ADMIN", "MANAGER")
        explanation: "Documented in OpenAPI as bearer-secured, but GETs do not require a token — align docs or policy before production."

  - id: "ex-cache-redis"
    title: "Redis CacheManager with Jackson JSON values"
    description: "30-minute TTL, nulls disabled, custom ObjectMapper including JavaTimeModule and default typing activation."
    category: "performance"
    duration: "4 min read"
    views: 0
    tags:
      - "Redis"
      - "Jackson"
    files:
      - name: "CacheConfig.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/stores/config/CacheConfig.java"
        language: "java"
        highlighted: true
        content: |
          RedisCacheConfiguration.defaultCacheConfig()
              .entryTtl(Duration.ofMinutes(30))
              .disableCachingNullValues()
          objectMapper.activateDefaultTyping(
              objectMapper.getPolymorphicTypeValidator(),
              ObjectMapper.DefaultTyping.NON_FINAL);
        explanation: "Default typing can be unsafe if untrusted data reaches Redis; prefer explicit DTO types or disable typing for hardened deployments."

  - id: "ex-usecase-cache-evict"
    title: "Transactional command + broad cache eviction"
    description: "Every mutating use case evicts `stores`, `store_searches`, and `store_status` entirely to avoid stale reads."
    category: "application"
    duration: "3 min read"
    views: 0
    tags:
      - "Spring Cache"
      - "Transactional"
    files:
      - name: "StoreCommandUseCasesImpl.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/stores/application/usecase/StoreCommandUseCasesImpl.java"
        language: "java"
        highlighted: false
        content: |
          @Transactional
          @CacheEvict(value = { "stores", "store_searches", "store_status" }, allEntries = true)
          public CreateStoreResult createStore(CreateStoreCommand command) { ... }
        explanation: "Simple correctness; can increase Redis churn at high write rates — consider finer-grained eviction later."

  - id: "ex-event-stub"
    title: "Outbound messaging adapter (no-op)"
    description: "Implements port but does not call Kafka; logs success even when nothing is sent."
    category: "integration"
    duration: "2 min read"
    views: 0
    tags:
      - "Kafka"
      - "TODO"
    files:
      - name: "StoreEventPublisherImpl.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/stores/infrastructure/outbound/external/messaging/StoreEventPublisherImpl.java"
        language: "java"
        highlighted: true
        content: |
          public void publishStoreStatusChanged(StoreStatusChangedEvent event) {
            try {
              log.info("Publishing StoreStatusChangedEvent for store: {}", event.getStoreId());
              log.info("StoreStatusChangedEvent published successfully for store: {}", event.getStoreId());
            } catch (Exception e) {
              log.error("Failed to publish ...", e);
            }
          }
        explanation: "Misleading log line says 'published successfully' without broker interaction — fix before relying on logs for ops."
---

# CodeShowCase

Curated slices for reviewers: **security matrix**, **Redis serialization choices**, **cache eviction strategy**, and the **stub event publisher** (integration gap).

> [!danger] Observability deception risk  
> Success logs on `StoreEventPublisherImpl` do **not** imply Kafka delivery — do not wire alerts on that string alone.
