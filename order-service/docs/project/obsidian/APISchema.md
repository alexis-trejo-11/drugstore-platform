---
# APISchema
type: "REST"

# ApiEndpoint[]
httpEndpoints:
  # === SaleOrderController (Admin/Employee endpoints) ===
  - id: "search-orders"
    method: "GET"
    urlPath: "/api/v2/sale-orders/search"
    summary: "Search orders with pagination and filters"
    description: "Performs a paginated search of orders applying dynamic filters such as status, deliveryMethod, date range, and user ID. Returns a standard ResponseWrapper with PageResponse."
    authenticated: true
    rateLimit: "100 requests per 60 seconds (standard profile)"
    tags:
      - "Orders"
      - "Search"
      - "Admin"
    parameters:
      - name: "status"
        in: "query"
        type: "string"
        required: false
        description: "Filter by order status (PENDING, CONFIRMED, PREPARING, READY_FOR_PICKUP, OUT_FOR_DELIVERY, DELIVERED, PICKED_UP, CANCELLED, RETURNED)"
        example: "PENDING"
      - name: "deliveryMethod"
        in: "query"
        type: "string"
        required: false
        description: "Filter by delivery method (STORE_PICKUP, EXPRESS_DELIVERY, STANDARD_DELIVERY)"
        example: "EXPRESS_DELIVERY"
      - name: "dateFrom"
        in: "query"
        type: "string"
        required: false
        description: "Filter orders created from this date (ISO format)"
        example: "2025-01-01T00:00:00"
      - name: "dateTo"
        in: "query"
        type: "string"
        required: false
        description: "Filter orders created until this date (ISO format)"
        example: "2025-12-31T23:59:59"
      - name: "userId"
        in: "query"
        type: "string"
        required: false
        description: "Filter by user ID"
        example: "USER-987654321"
      - name: "page"
        in: "query"
        type: "integer"
        required: false
        description: "Page number (0-based)"
        example: 0
      - name: "size"
        in: "query"
        type: "integer"
        required: false
        description: "Page size"
        example: 20
      - name: "sort"
        in: "query"
        type: "string"
        required: false
        description: "Sort criteria (field,direction)"
        example: "createdAt,DESC"
    responses:
      - status: 200
        description: "Orders found successfully"
        schema:
          type: "object"
          properties:
            success: { type: "boolean" }
            code: { type: "integer" }
            message: { type: "string" }
            data: { type: "object", description: "PageResponse with content array" }
        example:
          success: true
          code: 200
          message: "Orders found successfully"
          data:
            content:
              - orderId: "c1d2e3f4-1111-2222-3333-abcdefabcdef"
                status: "PENDING"
                totalAmount: "150.75"
                totalItems: 5
                deliveryMethod: "EXPRESS_DELIVERY"
                createdAt: "2025-01-15T10:15:30"
            page: 0
            size: 20
            totalElements: 1
            totalPages: 1
      - status: 400
        description: "Invalid filter parameters"
        example:
          success: false
          code: 400
          message: "Invalid search criteria"
          errors: { status: "Unsupported status value" }
      - status: 401
        description: "Unauthorized - Invalid or missing Bearer token"
        example:
          success: false
          code: 401
          message: "Unauthorized"
      - status: 500
        description: "Internal server error"
        example:
          success: false
          code: 500
          message: "Internal server error"

  - id: "get-order-by-id"
    method: "GET"
    urlPath: "/api/v2/sale-orders/{id}"
    summary: "Get order by ID"
    description: "Retrieves order information by its unique identifier. Includes order summary with status, total amount, and item count."
    authenticated: true
    rateLimit: "10 requests per 60 seconds"
    tags:
      - "Orders"
      - "Query"
      - "Admin"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Order unique identifier"
        example: "c1d2e3f4-1111-2222-3333-abcdefabcdef"
    responses:
      - status: 200
        description: "Order found successfully"
        schema:
          type: "object"
          properties:
            success: { type: "boolean" }
            data: { type: "object", description: "OrderResponse" }
        example:
          success: true
          code: 200
          message: "PurchaseOrder"
          data:
            orderId: "c1d2e3f4-1111-2222-3333-abcdefabcdef"
            userID: "USER-987654321"
            status: "PENDING"
            totalAmount: "150.75"
            totalItems: 5
            deliveryMethod: "EXPRESS_DELIVERY"
            createdAt: "2025-01-15T10:15:30"
      - status: 400
        description: "Order not found"
        example:
          success: false
          code: 400
          message: "Order not found"
      - status: 401
        description: "Unauthorized"
        example:
          success: false
          code: 401
          message: "Unauthorized"

  - id: "get-order-detail-by-id"
    method: "GET"
    urlPath: "/api/v2/sale-orders/{id}/detail"
    summary: "Get order detail by ID"
    description: "Retrieves detailed order information including items, delivery/pickup info, status history, and timestamps."
    authenticated: true
    rateLimit: "10 requests per 60 seconds"
    tags:
      - "Orders"
      - "Detail"
      - "Admin"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Order unique identifier"
        example: "c1d2e3f4-1111-2222-3333-abcdefabcdef"
    responses:
      - status: 200
        description: "Order detail found successfully"
        example:
          success: true
          code: 200
          message: "PurchaseOrder Detail"
          data:
            orderId: "c1d2e3f4-1111-2222-3333-abcdefabcdef"
            userID: "USER-987654321"
            status: "PENDING"
            totalAmount: "150.75"
            items:
              - productID: "PROD-123"
                productName: "Aspirin 100mg"
                quantity: 2
                subtotal: "50.00"
                isPrescriptionRequired: false
            deliveryInfo:
              address: "123 Main St, Springfield"
              shippingCost: "25.50"
              estimatedDeliveryDate: "2025-01-20T14:00:00"
      - status: 401
        description: "Unauthorized"
      - status: 404
        description: "Order not found"

  - id: "create-order"
    method: "POST"
    urlPath: "/api/v2/sale-orders"
    summary: "Create a new order"
    description: "Creates a new order with items and delivery/pickup information. Validates input and publishes OrderCreatedEvent. Supports both delivery and pickup orders."
    authenticated: true
    rateLimit: "100 requests per 60 seconds (standard profile)"
    tags:
      - "Orders"
      - "Create"
      - "Admin"
    parameters: []
    requestBody:
      contentType: "application/json"
      schema:
        type: "object"
        properties:
          userID: { type: "string", description: "User unique identifier" }
          deliveryMethod: { type: "string", enum: ["STORE_PICKUP", "EXPRESS_DELIVERY", "STANDARD_DELIVERY"] }
          notes: { type: "string", description: "Optional order notes" }
          currency: { type: "string", description: "Currency code (e.g., MXN, USD)" }
          items: { type: "array", description: "List of order items" }
          deliveryInfo: { type: "object", description: "Required for delivery orders" }
          pickupInfo: { type: "object", description: "Required for pickup orders" }
      example:
        userID: "USER-987654321"
        deliveryMethod: "EXPRESS_DELIVERY"
        notes: "Please deliver before 5 PM"
        currency: "MXN"
        items:
          - productID: "PROD-123"
            productName: "Aspirin 100mg"
            subtotal: "50.00"
            quantity: 2
            isPrescriptionRequired: false
        deliveryInfo:
          addressID: "ADDR-456"
    responses:
      - status: 201
        description: "Order created successfully"
        example:
          success: true
          code: 201
          message: "PurchaseOrder"
          data:
            purchaseOrderId: "c1d2e3f4-1111-2222-3333-abcdefabcdef"
            status: "PENDING"
      - status: 400
        description: "Invalid payload or validation error"
      - status: 409
        description: "Conflict (duplicate or other rule violation)"
      - status: 422
        description: "Semantic validation error"
      - status: 401
        description: "Unauthorized"

  - id: "delete-order"
    method: "DELETE"
    urlPath: "/api/v2/sale-orders/{id}"
    summary: "Delete an order"
    description: "Deletes an order. Supports soft delete (default) which preserves data for audit, or hard delete for complete removal."
    authenticated: true
    rateLimit: "50 requests per 60 seconds (admin profile)"
    tags:
      - "Orders"
      - "Delete"
      - "Admin"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Order unique identifier"
        example: "c1d2e3f4-1111-2222-3333-abcdefabcdef"
      - name: "isHard"
        in: "query"
        type: "boolean"
        required: false
        description: "If true, performs hard delete; otherwise soft delete"
        example: false
    responses:
      - status: 200
        description: "Order successfully deleted"
        example:
          success: true
          code: 200
          message: "PurchaseOrder Successfully Deleted"
      - status: 401
        description: "Unauthorized"
      - status: 404
        description: "Order not found"

  # === SaleOrderStatusController (Status transitions) ===
  - id: "confirm-order"
    method: "PATCH"
    urlPath: "/api/v2/sale-orders/{id}/confirm"
    summary: "Confirm an order"
    description: "Confirms a pending order with payment information. Transitions order from PENDING to CONFIRMED state."
    authenticated: true
    rateLimit: "50 requests per 60 seconds (admin profile)"
    tags:
      - "Order Status"
      - "Confirm"
      - "Admin"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Order unique identifier"
    requestBody:
      contentType: "application/json"
      schema:
        type: "object"
        properties:
          paymentID: { type: "string", description: "Payment identifier" }
          estimatedDeliveryDate: { type: "string", description: "ISO datetime for estimated delivery (delivery orders only)" }
      example:
        paymentID: "PAY-789"
        estimatedDeliveryDate: "2025-01-20T14:00:00"
    responses:
      - status: 200
        description: "Order successfully confirmed"
      - status: 400
        description: "Invalid state transition or validation error"
      - status: 401
        description: "Unauthorized"

  - id: "start-preparing-order"
    method: "PATCH"
    urlPath: "/api/v2/sale-orders/{id}/start-preparing"
    summary: "Start preparing order"
    description: "Marks an order as being prepared. Transitions from CONFIRMED to PREPARING state."
    authenticated: true
    rateLimit: "50 requests per 60 seconds (admin profile)"
    tags:
      - "Order Status"
      - "Prepare"
      - "Admin"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Order unique identifier"
    responses:
      - status: 200
        description: "Order successfully marked as preparing"
      - status: 400
        description: "Invalid state transition"
      - status: 401
        description: "Unauthorized"

  - id: "ship-order"
    method: "PATCH"
    urlPath: "/api/v2/sale-orders/{id}/ship/track_number/{trackNumber}"
    summary: "Ship order"
    description: "Marks an order as shipped with tracking number. Transitions from PREPARING to OUT_FOR_DELIVERY state."
    authenticated: true
    rateLimit: "50 requests per 60 seconds (admin profile)"
    tags:
      - "Order Status"
      - "Ship"
      - "Admin"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Order unique identifier"
      - name: "trackNumber"
        in: "path"
        type: "string"
        required: true
        description: "Shipping tracking number"
        example: "TRACK-123456789"
    responses:
      - status: 200
        description: "Order successfully shipped"
      - status: 400
        description: "Invalid state transition"
      - status: 401
        description: "Unauthorized"

  - id: "return-order"
    method: "PATCH"
    urlPath: "/api/v2/sale-orders/{id}/return"
    summary: "Return order"
    description: "Marks an order as returned with a reason. Increments delivery attempts. Transitions to RETURNED state."
    authenticated: true
    rateLimit: "50 requests per 60 seconds (admin profile)"
    tags:
      - "Order Status"
      - "Return"
      - "Admin"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Order unique identifier"
      - name: "reason"
        in: "query"
        type: "string"
        required: true
        description: "Return reason (CustomerNotAvailable, TimeoutWindowMissed, etc.)"
        example: "CustomerNotAvailable"
    responses:
      - status: 200
        description: "Order successfully marked as returned"
      - status: 400
        description: "Invalid state transition or reason"
      - status: 401
        description: "Unauthorized"

  - id: "ready-for-pickup"
    method: "PATCH"
    urlPath: "/api/v2/sale-orders/{id}/ready-pickup"
    summary: "Mark order ready for pickup"
    description: "Marks a pickup order as ready for customer pickup. Transitions from PREPARING to READY_FOR_PICKUP state."
    authenticated: true
    rateLimit: "50 requests per 60 seconds (admin profile)"
    tags:
      - "Order Status"
      - "Pickup"
      - "Admin"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Order unique identifier"
    responses:
      - status: 200
        description: "Order successfully marked as ready for pickup"
      - status: 400
        description: "Invalid state transition (only for pickup orders)"
      - status: 401
        description: "Unauthorized"

  - id: "complete-order"
    method: "PATCH"
    urlPath: "/api/v2/sale-orders/{id}/complete"
    summary: "Complete order"
    description: "Marks an order as completed. For delivery orders transitions to DELIVERED, for pickup orders to PICKED_UP."
    authenticated: true
    rateLimit: "50 requests per 60 seconds (admin profile)"
    tags:
      - "Order Status"
      - "Complete"
      - "Admin"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Order unique identifier"
    responses:
      - status: 200
        description: "Order successfully completed"
      - status: 400
        description: "Invalid state transition"
      - status: 401
        description: "Unauthorized"

  - id: "cancel-order"
    method: "PUT"
    urlPath: "/api/v2/sale-orders/{id}/cancel"
    summary: "Cancel order"
    description: "Cancels an order with a reason. Can only cancel non-terminal orders (PENDING, CONFIRMED, PREPARING, READY_FOR_PICKUP, OUT_FOR_DELIVERY)."
    authenticated: true
    rateLimit: "50 requests per 60 seconds (admin profile)"
    tags:
      - "Order Status"
      - "Cancel"
      - "Admin"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Order unique identifier"
      - name: "reason"
        in: "query"
        type: "string"
        required: true
        description: "Cancellation reason"
        example: "Customer requested cancellation"
    responses:
      - status: 200
        description: "Order successfully canceled"
        example:
          success: true
          code: 200
          message: "Order Successfully Canceled"
          data:
            orderId: "c1d2e3f4-1111-2222-3333-abcdefabcdef"
            status: "CANCELLED"
      - status: 400
        description: "Cannot cancel terminal order"
      - status: 401
        description: "Unauthorized"

  # === UserOrderController (Customer endpoints) ===
  - id: "get-user-orders"
    method: "GET"
    urlPath: "/api/v2/customers/orders/{userID}"
    summary: "Get customer orders"
    description: "Retrieves paginated list of orders for a specific customer. Customers can only access their own orders."
    authenticated: true
    rateLimit: "100 requests per 60 seconds (standard profile)"
    tags:
      - "Customer Orders"
      - "Query"
      - "Customer"
    parameters:
      - name: "userID"
        in: "path"
        type: "string"
        required: true
        description: "Customer unique identifier (must match authenticated user)"
      - name: "page"
        in: "query"
        type: "integer"
        required: false
        description: "Page number (0-based)"
        example: 0
      - name: "size"
        in: "query"
        type: "integer"
        required: false
        description: "Page size"
        example: 20
      - name: "status"
        in: "query"
        type: "string"
        required: false
        description: "Filter by status"
        example: "PENDING"
    responses:
      - status: 200
        description: "Orders found successfully"
        example:
          success: true
          code: 200
          message: "Orders"
          data:
            content:
              - orderId: "c1d2e3f4-1111-2222-3333-abcdefabcdef"
                status: "PENDING"
                totalAmount: "150.75"
                totalItems: 5
                deliveryMethod: "EXPRESS_DELIVERY"
            page: 0
            size: 20
            totalElements: 1
      - status: 401
        description: "Unauthorized"
      - status: 403
        description: "Forbidden - Can only access own orders"

  - id: "get-user-order-detail"
    method: "GET"
    urlPath: "/api/v2/customers/orders/{orderID}/{userID}"
    summary: "Get customer order detail"
    description: "Retrieves detailed information for a specific customer order. Customers can only access their own orders."
    authenticated: true
    rateLimit: "10 requests per 60 seconds"
    tags:
      - "Customer Orders"
      - "Detail"
      - "Customer"
    parameters:
      - name: "orderID"
        in: "path"
        type: "string"
        required: true
        description: "Order unique identifier"
      - name: "userID"
        in: "path"
        type: "string"
        required: true
        description: "Customer unique identifier (must match authenticated user)"
    responses:
      - status: 200
        description: "Order detail found successfully"
      - status: 401
        description: "Unauthorized"
      - status: 403
        description: "Forbidden - Can only access own orders"
      - status: 404
        description: "Order not found"
---
# API Schema
> Order Service exposes 15 REST API endpoints across three controllers: SaleOrderController (CRUD operations), SaleOrderStatusController (status transitions), and UserOrderController (customer-specific access). All endpoints require Bearer token authentication with role-based access control. API documentation available via Swagger UI at /swagger-ui.html.

<!--
  OBSERVATIONS FOR APISchema:
  ✅ POSITIVE:
    - 15 well-documented REST endpoints with proper HTTP methods
    - Comprehensive request/response examples for all endpoints
    - OpenAPI annotations with @Operation and @ApiResponse
    - Role-based access control (CUSTOMER, EMPLOYEE, ADMIN)
    - Rate limiting configured per endpoint
    - Proper use of HTTP status codes (200, 201, 400, 401, 403, 404, 409, 422, 500)
    - Pagination support for all list endpoints
    - Request validation with @Valid and Bean Validation

  ⚠️ WARNINGS / MISSING / DANGEROUS:
    - UserOrderController endpoint "/api/v2/customers/orders/{orderID}/{userID}" has confusing path variable order (orderID before userID)
    - Same controller "/api/v2/customers/orders/{userID}" uses "customerId" param name but path variable is "userID" - INCONSISTENT
    - No API versioning strategy beyond URL path ("/api/v2") - breaking changes affect all clients
    - Rate limit values in docs are from application.yml but actual enforcement needs verification
    - No bulk operations endpoints (e.g., bulk status update, bulk cancel)
    - DELETE endpoint supports hard delete - dangerous operation, should be admin-only with audit
    - No API documentation for AddressController and UserController (external packages)
    - Some endpoints use PATCH, others use PUT for status changes - INCONSISTENT (cancel uses PUT)
    - No HATEOAS links in responses for navigation
    - ResponseWrapper schema examples may not match actual shared-kernel implementation
-->
