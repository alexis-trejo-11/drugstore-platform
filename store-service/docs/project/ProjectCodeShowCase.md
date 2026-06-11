# Code showcase

Plain-Markdown companion to `docs/project/obsidian/ProjectCodeShowCase.md`. Paths are relative to the `store-service` module root.

---

## 1. Security: anonymous reads vs JWT writes

**File:** `src/main/java/io/github/alexisTrejo11/drugstore/stores/config/security/SecurityConfig.java`

**Idea:** `HttpMethod.GET` on `/api/v2/stores/**` is `permitAll()`; POST/PUT/PATCH/DELETE require `hasAnyRole("ADMIN", "MANAGER")` (PATCH includes both admin-only routes).

**Why it matters:** Consumers reading OpenAPI may assume **all** routes need `Authorization: Bearer`, but GET works **without** a token.

**Snippet (illustrative):**

```java
.requestMatchers(HttpMethod.GET, "/api/v2/stores/**").permitAll()
.requestMatchers(HttpMethod.POST, "/api/v2/stores/**").hasAnyRole("ADMIN", "MANAGER")
```

---

## 2. Redis `CacheManager` and Jackson typing

**File:** `src/main/java/io/github/alexisTrejo11/drugstore/stores/config/CacheConfig.java`

**Idea:** Serializes cached entries as JSON via `Jackson2JsonRedisSerializer`; registers Java time module; **activates default typing** (`NON_FINAL`).

**Danger:** Classic footgun if Redis can be manipulated by an attacker — prefer explicit value types or a safe whitelist in production hardening passes.

---

## 3. Command use case — transactional + cache eviction

**File:** `src/main/java/io/github/alexisTrejo11/drugstore/stores/application/usecase/StoreCommandUseCasesImpl.java`

**Pattern:**

```java
@Transactional
@CacheEvict(value = { "stores", "store_searches", "store_status" }, allEntries = true)
public CreateStoreResult createStore(CreateStoreCommand command) { ... }
```

**Note:** Broad eviction avoids stale slices but increases load on Redis for frequent updates.

---

## 4. Event publisher stub (integration debt)

**File:** `src/main/java/io/github/alexisTrejo11/drugstore/stores/infrastructure/outbound/external/messaging/StoreEventPublisherImpl.java`

**Current behavior:** Logs “Publishing…” and “published successfully” **without** broker I/O.

**Danger:** Operational **false positives** if log-based alerts assume Kafka success.

---

## Related tests (spot-check quality)

- `StoreCommandUseCasesImplTest` — mocks `StoreEventPublisher`.
- `StoreQueryControllerTest`, `StoreCommandControllerTest`, `SpringDocIntegrationTest` — API and OpenAPI smoke coverage.
