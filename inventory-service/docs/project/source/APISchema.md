---
# APISchema
type: "REST"

# ApiEndpoint[]
httpEndpoints:
  # Inventory Endpoints
  - id: "get-inventory-by-id"
    method: "GET"
    urlPath: "/api/v2/inventories/{id}"
    summary: "Get inventory by ID"
    description: "Retrieves inventory details by inventory ID using CQRS query pattern"
    authenticated: true
    rateLimit: "100 requests per 60 seconds"
    tags:
      - "inventory"
      - "query"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Inventory ID"
        example: "inv-123"
    responses:
      - status: 200
        description: "Inventory found"
        schema: {"InventoryResponse": {"id": "string", "productId": "string", "totalQuantity": "number", "availableQuantity": "number", "reservedQuantity": "number"}}
        example: {"data": {"id": "inv-123", "productId": "prod-456", "totalQuantity": 100, "availableQuantity": 80, "reservedQuantity": 20}}
      - status: 404
        description: "Inventory not found"
        example: {"error": "Inventory not found"}
      - status: 401
        description: "Unauthorized"
        example: {"error": "Unauthorized"}
      - status: 500
        description: "Internal server error"
        example: {"error": "Internal server error"}

  - id: "get-inventory-by-product"
    method: "GET"
    urlPath: "/api/v2/inventories/product/{productId}"
    summary: "Get inventory by product ID"
    description: "Retrieves inventory details by product ID"
    authenticated: true
    rateLimit: "100 requests per 60 seconds"
    tags:
      - "inventory"
      - "query"
    parameters:
      - name: "productId"
        in: "path"
        type: "string"
        required: true
        description: "Product ID"
        example: "prod-456"
    responses:
      - status: 200
        description: "Inventory found"
        schema: {"InventoryResponse": {"id": "string", "productId": "string", "totalQuantity": "number", "availableQuantity": "number"}}
        example: {"data": {"id": "inv-123", "productId": "prod-456", "totalQuantity": 100, "availableQuantity": 80}}
      - status: 404
        description: "Inventory not found for product"
        example: {"error": "Inventory not found"}
      - status: 401
        description: "Unauthorized"
        example: {"error": "Unauthorized"}
      - status: 500
        description: "Internal server error"
        example: {"error": "Internal server error"}

  - id: "get-low-stock"
    method: "GET"
    urlPath: "/api/v2/inventories/low-stock"
    summary: "Get low stock inventories"
    description: "Retrieves paginated list of inventories with low stock levels"
    authenticated: true
    rateLimit: "50 requests per 30 seconds"
    tags:
      - "inventory"
      - "query"
      - "alerts"
    parameters:
      - name: "page"
        in: "query"
        type: "number"
        required: false
        description: "Page number (0-based)"
        example: 0
      - name: "size"
        in: "query"
        type: "number"
        required: false
        description: "Page size"
        example: 20
    responses:
      - status: 200
        description: "Low stock inventories retrieved"
        schema: {"PageResponse": {"content": ["InventoryResponse"], "totalElements": "number", "totalPages": "number"}}
        example: {"data": {"content": [{"id": "inv-123", "productId": "prod-456", "totalQuantity": 10, "availableQuantity": 5}], "totalElements": 1}}
      - status: 401
        description: "Unauthorized"
        example: {"error": "Unauthorized"}
      - status: 500
        description: "Internal server error"
        example: {"error": "Internal server error"}

  - id: "create-inventory"
    method: "POST"
    urlPath: "/api/v2/inventories"
    summary: "Create new inventory"
    description: "Creates a new inventory record for a product"
    authenticated: true
    rateLimit: "10 requests per 60 seconds"
    tags:
      - "inventory"
      - "command"
    requestBody:
      contentType: "application/json"
      schema: {"CreateInventoryRequest": {"productId": "string", "initialQuantity": "number"}}
      example: {"productId": "prod-456", "initialQuantity": 100}
    responses:
      - status: 201
        description: "Inventory created successfully"
        schema: {"InventoryId": {"value": "string"}}
        example: {"data": {"value": "inv-123"}}
      - status: 400
        description: "Invalid request body"
        example: {"error": "Invalid request"}
      - status: 401
        description: "Unauthorized"
        example: {"error": "Unauthorized"}
      - status: 500
        description: "Internal server error"
        example: {"error": "Internal server error"}

  # Reservation Endpoints
  - id: "reserve-stock"
    method: "POST"
    urlPath: "/api/v2/inventories/{inventoryId}/stock/reservations"
    summary: "Reserve stock"
    description: "Creates a stock reservation for order processing"
    authenticated: true
    rateLimit: "50 requests per 30 seconds"
    tags:
      - "reservation"
      - "command"
    parameters:
      - name: "inventoryId"
        in: "path"
        type: "string"
        required: true
        description: "Inventory ID"
        example: "inv-123"
    requestBody:
      contentType: "application/json"
      schema: {"ReserveStockRequest": {"quantity": "number", "reason": "string", "orderId": "string"}}
      example: {"quantity": 10, "reason": "Order fulfillment", "orderId": "order-789"}
    responses:
      - status: 201
        description: "Stock reserved successfully"
        schema: {"ReservationId": {"value": "string"}}
        example: {"data": {"value": "res-123"}}
      - status: 400
        description: "Insufficient stock or invalid request"
        example: {"error": "Insufficient available stock"}
      - status: 401
        description: "Unauthorized"
        example: {"error": "Unauthorized"}
      - status: 500
        description: "Internal server error"
        example: {"error": "Internal server error"}

  - id: "get-active-reservations"
    method: "GET"
    urlPath: "/api/v2/inventories/{inventoryId}/stock/reservations/active"
    summary: "Get active reservations"
    description: "Retrieves all active stock reservations for an inventory"
    authenticated: true
    rateLimit: "100 requests per 60 seconds"
    tags:
      - "reservation"
      - "query"
    parameters:
      - name: "inventoryId"
        in: "path"
        type: "string"
        required: true
        description: "Inventory ID"
        example: "inv-123"
    responses:
      - status: 200
        description: "Active reservations retrieved"
        schema: {"ReservationResponse": {"id": "string", "inventoryId": "string", "quantity": "number", "status": "string"}}
        example: {"data": [{"id": "res-123", "inventoryId": "inv-123", "quantity": 10, "status": "ACTIVE"}]}
      - status: 401
        description: "Unauthorized"
        example: {"error": "Unauthorized"}
      - status: 500
        description: "Internal server error"
        example: {"error": "Internal server error"}

  - id: "confirm-reservation"
    method: "PATCH"
    urlPath: "/api/v2/inventories/stock/reservations/{reservationId}/confirm"
    summary: "Confirm reservation"
    description: "Confirms a stock reservation (typically after order confirmation)"
    authenticated: true
    rateLimit: "50 requests per 30 seconds"
    tags:
      - "reservation"
      - "command"
    parameters:
      - name: "reservationId"
        in: "path"
        type: "string"
        required: true
        description: "Reservation ID"
        example: "res-123"
    responses:
      - status: 200
        description: "Reservation confirmed"
        example: {"message": "Reservation confirmed"}
      - status: 404
        description: "Reservation not found"
        example: {"error": "Reservation not found"}
      - status: 401
        description: "Unauthorized"
        example: {"error": "Unauthorized"}
      - status: 500
        description: "Internal server error"
        example: {"error": "Internal server error"}

  - id: "release-reservation"
    method: "PATCH"
    urlPath: "/api/v2/inventories/reservations/{reservationId}/stock/release"
    summary: "Release reservation"
    description: "Releases a stock reservation back to available inventory"
    authenticated: true
    rateLimit: "50 requests per 30 seconds"
    tags:
      - "reservation"
      - "command"
    parameters:
      - name: "reservationId"
        in: "path"
        type: "string"
        required: true
        description: "Reservation ID"
        example: "res-123"
      - name: "reason"
        in: "query"
        type: "string"
        required: false
        description: "Reason for release"
        example: "Order cancelled"
    responses:
      - status: 200
        description: "Reservation released"
        example: {"message": "Reservation released"}
      - status: 404
        description: "Reservation not found"
        example: {"error": "Reservation not found"}
      - status: 401
        description: "Unauthorized"
        example: {"error": "Unauthorized"}
      - status: 500
        description: "Internal server error"
        example: {"error": "Internal server error"}

  - id: "cancel-reservation"
    method: "DELETE"
    urlPath: "/api/v2/inventories/reservations/{reservationId}"
    summary: "Cancel reservation"
    description: "Cancels a stock reservation"
    authenticated: true
    rateLimit: "50 requests per 30 seconds"
    tags:
      - "reservation"
      - "command"
    parameters:
      - name: "reservationId"
        in: "path"
        type: "string"
        required: true
        description: "Reservation ID"
        example: "res-123"
    responses:
      - status: 200
        description: "Reservation cancelled"
        example: {"message": "Reservation deleted"}
      - status: 404
        description: "Reservation not found"
        example: {"error": "Reservation not found"}
      - status: 401
        description: "Unauthorized"
        example: {"error": "Unauthorized"}
      - status: 500
        description: "Internal server error"
        example: {"error": "Internal server error"}

  # Stock Movement Endpoints
  - id: "adjust-inventory"
    method: "POST"
    urlPath: "/api/v2/inventories/{inventoryId}/stocks-movements/adjust"
    summary: "Adjust inventory stock"
    description: "Adjusts inventory quantity (increase/decrease) with reason tracking"
    authenticated: true
    rateLimit: "50 requests per 30 seconds"
    tags:
      - "movement"
      - "command"
    parameters:
      - name: "inventoryId"
        in: "path"
        type: "string"
        required: true
        description: "Inventory ID"
        example: "inv-123"
    requestBody:
      contentType: "application/json"
      schema: {"AdjustInventoryRequest": {"quantity": "number", "reason": "string", "adjustmentType": "string"}}
      example: {"quantity": -5, "reason": "Damaged goods", "adjustmentType": "DAMAGE"}
    responses:
      - status: 201
        description: "Inventory adjusted successfully"
        schema: {"AdjustmentId": {"value": "string"}}
        example: {"data": {"value": "adj-123"}}
      - status: 400
        description: "Invalid adjustment"
        example: {"error": "Insufficient stock for adjustment"}
      - status: 401
        description: "Unauthorized"
        example: {"error": "Unauthorized"}
      - status: 500
        description: "Internal server error"
        example: {"error": "Internal server error"}

  - id: "transfer-inventory"
    method: "POST"
    urlPath: "/api/v2/inventories/{sourceInventoryId}/stocks/movements/transfer"
    summary: "Transfer inventory between locations"
    description: "Transfers inventory stock from source to destination inventory"
    authenticated: true
    rateLimit: "50 requests per 30 seconds"
    tags:
      - "movement"
      - "transfer"
      - "command"
    parameters:
      - name: "sourceInventoryId"
        in: "path"
        type: "string"
        required: true
        description: "Source Inventory ID"
        example: "inv-123"
    requestBody:
      contentType: "application/json"
      schema: {"TransferInventoryRequest": {"destinationInventoryId": "string", "quantity": "number", "reason": "string"}}
      example: {"destinationInventoryId": "inv-456", "quantity": 20, "reason": "Store transfer"}
    responses:
      - status: 200
        description: "Inventory transferred successfully"
        example: {"message": "Inventory transferred successfully"}
      - status: 400
        description: "Invalid transfer request"
        example: {"error": "Insufficient stock for transfer"}
      - status: 401
        description: "Unauthorized"
        example: {"error": "Unauthorized"}
      - status: 500
        description: "Internal server error"
        example: {"error": "Internal server error"}

  - id: "get-inventory-movements"
    method: "GET"
    urlPath: "/api/v2/inventories/{inventoryId}/stocks/movements"
    summary: "Get inventory movements"
    description: "Retrieves paginated inventory movement history with optional date filtering"
    authenticated: true
    rateLimit: "100 requests per 60 seconds"
    tags:
      - "movement"
      - "query"
    parameters:
      - name: "inventoryId"
        in: "path"
        type: "string"
        required: true
        description: "Inventory ID"
        example: "inv-123"
      - name: "startDate"
        in: "query"
        type: "string"
        required: false
        description: "Start date (ISO format)"
        example: "2026-01-01T00:00:00"
      - name: "endDate"
        in: "query"
        type: "string"
        required: false
        description: "End date (ISO format)"
        example: "2026-04-29T23:59:59"
      - name: "page"
        in: "query"
        type: "number"
        required: false
        description: "Page number"
        example: 0
      - name: "size"
        in: "query"
        type: "number"
        required: false
        description: "Page size"
        example: 20
    responses:
      - status: 200
        description: "Movements retrieved"
        schema: {"PageResponse": {"content": ["MovementResponse"], "totalElements": "number"}}
        example: {"data": {"content": [{"id": "mov-123", "type": "ADJUSTMENT", "quantity": -5}], "totalElements": 1}}
      - status: 401
        description: "Unauthorized"
        example: {"error": "Unauthorized"}
      - status: 500
        description: "Internal server error"
        example: {"error": "Internal server error"}

  # Batch Endpoints
  - id: "add-batch"
    method: "POST"
    urlPath: "/api/v2/inventories/{inventoryId}/batches"
    summary: "Add inventory batch"
    description: "Adds a new batch with lot number, expiration date, and quantity"
    authenticated: true
    rateLimit: "50 requests per 30 seconds"
    tags:
      - "batch"
      - "command"
    parameters:
      - name: "inventoryId"
        in: "path"
        type: "string"
        required: true
        description: "Inventory ID"
        example: "inv-123"
    requestBody:
      contentType: "application/json"
      schema: {"AddInventoryBatchRequest": {"lotNumber": "string", "expirationDate": "string", "quantity": "number"}}
      example: {"lotNumber": "LOT-2026-001", "expirationDate": "2027-12-31", "quantity": 100}
    responses:
      - status: 201
        description: "Batch added successfully"
        schema: {"BatchId": {"value": "string"}}
        example: {"data": {"value": "batch-123"}}
      - status: 400
        description: "Invalid batch data"
        example: {"error": "Invalid request"}
      - status: 401
        description: "Unauthorized"
        example: {"error": "Unauthorized"}
      - status: 500
        description: "Internal server error"
        example: {"error": "Internal server error"}

  - id: "get-inventory-batches"
    method: "GET"
    urlPath: "/api/v2/inventories/{inventoryId}/batches"
    summary: "Get inventory batches"
    description: "Retrieves paginated batches for an inventory, optionally filtering active only"
    authenticated: true
    rateLimit: "100 requests per 60 seconds"
    tags:
      - "batch"
      - "query"
    parameters:
      - name: "inventoryId"
        in: "path"
        type: "string"
        required: true
        description: "Inventory ID"
        example: "inv-123"
      - name: "activeOnly"
        in: "query"
        type: "boolean"
        required: false
        description: "Filter only active batches"
        example: true
      - name: "page"
        in: "query"
        type: "number"
        required: false
        description: "Page number"
        example: 0
      - name: "size"
        in: "query"
        type: "number"
        required: false
        description: "Page size"
        example: 20
    responses:
      - status: 200
        description: "Batches retrieved"
        schema: {"PageResponse": {"content": ["BatchResponse"], "totalElements": "number"}}
        example: {"data": {"content": [{"id": "batch-123", "lotNumber": "LOT-001", "expirationDate": "2027-12-31"}], "totalElements": 1}}
      - status: 401
        description: "Unauthorized"
        example: {"error": "Unauthorized"}
      - status: 500
        description: "Internal server error"
        example: {"error": "Internal server error"}

  - id: "get-expiring-batches"
    method: "GET"
    urlPath: "/api/v2/inventories/batches/expiring"
    summary: "Get expiring batches"
    description: "Retrieves batches near expiration within threshold days (default 30)"
    authenticated: true
    rateLimit: "50 requests per 60 seconds"
    tags:
      - "batch"
      - "alerts"
      - "query"
    parameters:
      - name: "daysThreshold"
        in: "query"
        type: "number"
        required: false
        description: "Days until expiration threshold"
        example: 30
      - name: "expirationDate"
        in: "query"
        type: "string"
        required: false
        description: "Specific expiration date (ISO format)"
        example: "2026-12-31T23:59:59"
      - name: "page"
        in: "query"
        type: "number"
        required: false
        description: "Page number"
        example: 0
      - name: "size"
        in: "query"
        type: "number"
        required: false
        description: "Page size"
        example: 20
    responses:
      - status: 200
        description: "Expiring batches retrieved"
        schema: {"PageResponse": {"content": ["BatchResponse"], "totalElements": "number"}}
        example: {"data": {"content": [{"id": "batch-123", "lotNumber": "LOT-001", "expirationDate": "2026-05-15"}], "totalElements": 1}}
      - status: 401
        description: "Unauthorized"
        example: {"error": "Unauthorized"}
      - status: 500
        description: "Internal server error"
        example: {"error": "Internal server error"}

  - id: "mark-batch-expired"
    method: "PATCH"
    urlPath: "/api/v2/inventories/batches/{batchId}/mark-expired"
    summary: "Mark batch as expired"
    description: "Marks a batch as expired (typically automated via scheduled job)"
    authenticated: true
    rateLimit: "50 requests per 30 seconds"
    tags:
      - "batch"
      - "command"
    parameters:
      - name: "batchId"
        in: "path"
        type: "string"
        required: true
        description: "Batch ID"
        example: "batch-123"
      - name: "performedBy"
        in: "query"
        type: "string"
        required: true
        description: "User performing the action"
        example: "system"
    responses:
      - status: 200
        description: "Batch marked as expired"
        example: {"message": "Batch marked as expired"}
      - status: 404
        description: "Batch not found"
        example: {"error": "Batch not found"}
      - status: 401
        description: "Unauthorized"
        example: {"error": "Unauthorized"}
      - status: 500
        description: "Internal server error"
        example: {"error": "Internal server error"}

  - id: "mark-batch-damaged"
    method: "PATCH"
    urlPath: "/api/v2/inventories/batches/{batchId}/mark-damaged"
    summary: "Mark batch as damaged"
    description: "Marks a batch as damaged, removing quantity from available stock"
    authenticated: true
    rateLimit: "50 requests per 30 seconds"
    tags:
      - "batch"
      - "command"
    parameters:
      - name: "batchId"
        in: "path"
        type: "string"
        required: true
        description: "Batch ID"
        example: "batch-123"
      - name: "performedBy"
        in: "query"
        type: "string"
        required: true
        description: "User performing the action"
        example: "user-123"
    responses:
      - status: 200
        description: "Batch marked as damaged"
        example: {"message": "Batch marked as damaged"}
      - status: 404
        description: "Batch not found"
        example: {"error": "Batch not found"}
      - status: 401
        description: "Unauthorized"
        example: {"error": "Unauthorized"}
      - status: 500
        description: "Internal server error"
        example: {"error": "Internal server error"}

  - id: "quarantine-batch"
    method: "PATCH"
    urlPath: "/api/v2/inventories/batches/{batchId}/quarantine"
    summary: "Quarantine batch"
    description: "Places a batch in quarantine for quality control or investigation"
    authenticated: true
    rateLimit: "50 requests per 30 seconds"
    tags:
      - "batch"
      - "command"
    parameters:
      - name: "batchId"
        in: "path"
        type: "string"
        required: true
        description: "Batch ID"
        example: "batch-123"
      - name: "performedBy"
        in: "query"
        type: "string"
        required: true
        description: "User performing the action"
        example: "user-123"
    responses:
      - status: 200
        description: "Batch quarantined"
        example: {"message": "Batch quarantined"}
      - status: 404
        description: "Batch not found"
        example: {"error": "Batch not found"}
      - status: 401
        description: "Unauthorized"
        example: {"error": "Unauthorized"}
      - status: 500
        description: "Internal server error"
        example: {"error": "Internal server error"}
---
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
