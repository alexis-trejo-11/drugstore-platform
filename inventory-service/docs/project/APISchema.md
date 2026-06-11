# API Schema

> **NOTES:**
> 1. **Total Endpoints**: 18 REST endpoints across 4 controllers (Inventory, Reservation, Movement, Batch).
> 2. **CQRS Pattern**: Inventory queries use command/query separation (GetInventoryByIdQuery, GetLowStockInventoriesQuery, etc.).
> 3. **ResponseWrapper**: All endpoints use `libs_kernel.response.ResponseWrapper` for consistent response format.
> 4. **Rate Limiting**: Global rate limit is 1000 requests/hour, default endpoints 100 requests/minute. Auth endpoints have 10 requests/minute limit applied globally.
> 5. **Missing Features**: No gRPC endpoints found (unlike address-service, auth-service, cart-service which have gRPC). No Kafka integration (uses RabbitMQ instead).
> 6. **Pagination**: Low-stock, batches, movements, and expiring-batches endpoints support pagination via `PageRequest` with `page` and `size` parameters.
> 7. **Date Parameters**: Movement queries and expiration checks use ISO DateTime format (yyyy-MM-dd'T'HH:mm:ss).
> 8. **Integration tests**: Inventory REST flows are exercised under Spring profile `test` with `Authorization: Bearer <JWT>` so security matches production validation (`JwtAuthenticationFilter`). Low-stock listing uses `page`/`size` (`libs_kernel.page.PageRequest`, **1-based page**). See `ProjectFeature.md`.
