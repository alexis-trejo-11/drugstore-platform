---
codeExamples:
  - id: "controller-endpoints"
    title: "HTTP API endpoint orchestration"
    description: "Controller methods map HTTP requests to use-case commands/queries and wrap responses."
    category: "api"
    duration: "5 min"
    views: 0
    tags:
      - "spring-web"
      - "controller"
      - "validation"
    files:
      - name: "ProductController"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/products/adapter/in/web/controller/ProductController.java"
        language: "java"
        content: "@GetMapping, @PostMapping, @PutMapping, @PatchMapping, @DeleteMapping over /api/v2/products"
        highlighted: false
        explanation: "Defines API surface and delegates business logic to command/query use cases."

  - id: "cache-decorator"
    title: "Use-case decorator for cache-first reads"
    description: "CachingProductQueryUseCases implements cache lookup, fallback execution, and cache put."
    category: "performance"
    duration: "4 min"
    views: 0
    tags:
      - "spring-cache"
      - "redis"
      - "decorator"
    files:
      - name: "CachingProductQueryUseCases"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/products/core/application/usecase/decorator/CachingProductQueryUseCases.java"
        language: "java"
        content: "productById/productBySKU/productByBarcode/productSearch cache access and key construction"
        highlighted: false
        explanation: "Provides transparent read optimization while preserving the same port interface."

  - id: "security-filter-chain"
    title: "Stateless JWT security chain"
    description: "SecurityConfig defines role-based route access and JWT filter insertion."
    category: "security"
    duration: "3 min"
    views: 0
    tags:
      - "spring-security"
      - "jwt"
      - "rbac"
    files:
      - name: "SecurityConfig"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/products/config/security/SecurityConfig.java"
        language: "java"
        content: "permitAll for docs/health + role checks for write endpoints + denyAll fallback"
        highlighted: false
        explanation: "Hardens API access model with explicit allowlist and stateless processing."
---
# CodeShowCase

## Notes

- Audit logs currently show `serviceName` as `address-service` in runtime output, indicating incorrect audit metadata wiring.
- Caching command decorator clears entire SKU/barcode/search caches on writes; acceptable for now but may become expensive at scale.

