# CodeShowCase

> **OBSERVATIONS:**
> 1. **Batch Tracking**: `InventoryBatchEntity` provides pharmaceutical-grade batch tracking with lot numbers, expiration dates, and status (ACTIVE, EXPIRED, DAMAGED, QUARANTINED). This is critical for drugstore compliance.
> 2. **Reservation Pattern**: The reservation system allows temporary stock reservation during order processing with confirm/release lifecycle - essential for e-commerce scenarios.
> 3. **Stock Movements**: Adjustments track reasons (damage, loss, correction) while transfers move stock between inventory locations. Both create audit trails via `InventoryMovement` entities.
> 4. **RabbitMQ Inconsistency**: Using RabbitMQ while other services use Kafka is a **major architectural inconsistency**. Consider migrating to Kafka for platform uniformity.
> 5. **Missing gRPC**: Unlike address-service, auth-service, and cart-service, inventory-service doesn't expose gRPC endpoints for inter-service communication.
> 6. **CQRS Pattern**: Controllers use command/query separation (e.g., `GetInventoryMovementsQuery`, `AdjustInventoryRequest.toCommand()`).
> 7. **Integration tests**: `InventoryApiIntegrationTest` drives REST APIs through MockMvc under profile `test` (H2, real JWT validation via `IntegrationTestJwtSupport`). See `docs/project/ProjectFeature.md`.
