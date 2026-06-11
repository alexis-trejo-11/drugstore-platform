# CodeShowCase

## API orchestration

- **File:** `src/main/java/io/github/alexisTrejo11/drugstore/products/adapter/in/web/controller/ProductController.java`
- **Why it matters:** Central HTTP contract with clear mapping from request DTOs to use-cases.

## Cache decorator pattern

- **File:** `src/main/java/io/github/alexisTrejo11/drugstore/products/core/application/usecase/decorator/CachingProductQueryUseCases.java`
- **Why it matters:** Keeps caching concern separate from controller/business orchestration.

## Stateless JWT security chain

- **File:** `src/main/java/io/github/alexisTrejo11/drugstore/products/config/security/SecurityConfig.java`
- **Why it matters:** Explicit route-level RBAC, deny-all fallback, and stateless API behavior.

## Notes

- Audit logs currently show `serviceName=address-service` during Product Service operations, suggesting wrong audit metadata source.
- Cache invalidation currently clears entire SKU/barcode/search caches after writes, which may be expensive at high throughput.

