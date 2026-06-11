# Project Features

## 1) Product CRUD with soft-delete/restore

- **Description:** Create, update, delete (soft), and restore products with domain validation.
- **Category:** API
- **Status:** stable
- **Highlights:**
  - Write routes require `ADMIN` or `MANAGER`.
  - Public reads by id/sku/barcode.
  - Soft delete supports recovery and auditability.
- **Tech:** Spring Web, Spring Security, JPA

## 2) Cache-accelerated query flow

- **Description:** Query use-cases use cache-first lookup then delegate fallback.
- **Category:** performance
- **Status:** stable
- **Highlights:**
  - Regions: `productById`, `productBySKU`, `productByBarcode`, `productSearch`.
  - Write operations evict affected caches.
- **Tech:** Spring Cache, Redis

## 3) Kafka lifecycle events

- **Description:** Product changes are published for downstream consumers.
- **Category:** integration
- **Status:** stable
- **Highlights:**
  - Topic config under `app.kafka.topics`.
  - DLT topic naming included.
- **Tech:** Spring Kafka, Kafka

## Notes

- SKU normalization is uppercase in domain value objects; consumers should avoid case-sensitive assumptions.
- Barcode validation is digits-only, which can reject legacy alphanumeric barcodes unless migrated.

