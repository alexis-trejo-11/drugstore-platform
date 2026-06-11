---
# ProjectFeature[]
features:
  - id: "catalog-crud"
    title: "Product CRUD with soft-delete/restore"
    description: "Create, update, delete (soft), and restore products with domain validation and role-protected writes."
    icon: "package"
    category: "api"
    status: "stable"
    githubExampleUrl: "src/main/java/io/github/alexisTrejo11/drugstore/products/adapter/in/web/controller/ProductController.java"
    highlights:
      - "POST/PUT/PATCH/DELETE require ADMIN or MANAGER role"
      - "GET by id/sku/barcode supports public read access"
      - "Soft-delete avoids physical record removal"
    techStack:
      - "Spring Web"
      - "Spring Security"
      - "JPA"
    
    # FeatureMetric[]
    metrics:
      - label: "Protected write routes"
        value: "4"
        trend: "stable"
        icon: "shield"

    # CodeSnippet
    codeSnippet:
      language: "java"
      filename: "ProductController.java"
      code: "@PostMapping @PutMapping @PatchMapping @DeleteMapping with role-based authorization"

  - id: "cached-queries"
    title: "Caching decorators for query/use-case layer"
    description: "Reads use cache-first strategy for product id/sku/barcode and paginated search."
    icon: "zap"
    category: "performance"
    status: "stable"
    githubExampleUrl: "src/main/java/io/github/alexisTrejo11/drugstore/products/core/application/usecase/decorator/CachingProductQueryUseCases.java"
    highlights:
      - "Cache regions: productById, productBySKU, productByBarcode, productSearch"
      - "Cache eviction triggered after create/update/delete/restore"
    techStack:
      - "Spring Cache"
      - "Redis"
    metrics:
      - label: "Cache regions"
        value: "4"
        trend: "stable"
        icon: "database"
    codeSnippet:
      language: "java"
      filename: "CachingProductQueryUseCases.java"
      code: "Cache lookup -> fallback delegate -> cache put pattern"

  - id: "event-publishing"
    title: "Kafka event publication for product lifecycle"
    description: "Product changes are emitted to Kafka topics for downstream services."
    icon: "activity"
    category: "integration"
    status: "stable"
    githubExampleUrl: "src/main/resources/application.yml"
    highlights:
      - "Topic settings under app.kafka.topics"
      - "DLT topic configured for failed processing flows"
    techStack:
      - "Spring Kafka"
      - "Kafka"
    metrics:
      - label: "Primary product topics"
        value: "2"
        trend: "stable"
        icon: "message-square"
    codeSnippet:
      language: "yaml"
      filename: "application.yml"
      code: "app.kafka.topics.product-events and product-events-dlt"
---
# Project Features

## Notes

- SKU normalization is uppercase in domain value objects; clients should avoid case-sensitive assumptions.
- Barcode validation is strict digits-only, which can reject legacy alphanumeric barcodes if not migrated.

