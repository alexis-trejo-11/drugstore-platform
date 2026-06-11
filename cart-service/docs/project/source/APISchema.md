---
# APISchema
type: "REST + gRPC"

# ApiEndpoint[]
httpEndpoints:
  # ========== USER ENDPOINTS (/api/v2/carts/users) ==========

  - id: "get-my-cart"
    method: "GET"
    urlPath: "/api/v2/carts/users/my-cart"
    summary: "Get my cart"
    description: "Retrieves the shopping cart for the authenticated customer with items and afterwards"
    authenticated: true
    rateLimit: "PLACEHOLDER: Not annotated with @RateLimit"
    tags:
      - "Cart Management"
      - "GET"
    parameters: []
    requestBody: null
    responses:
      - status: 200
        description: "Cart retrieved successfully"
        schema:
          type: "object"
          properties:
            success: type: "boolean"
            message: type: "string"
            data: type: "object"
            timestamp: type: "string"
        example:
          success: true
          message: "Cart found"
          data:
            id: "123e4567-e89b-12d3-a456-426614174000"
            customerId: "usr-123e4567-e89b"
            items:
              - id: "223e4567-e89b-12d3-a456-426614174001"
                productId: "prod-001"
                productName: "Aspirin 500mg"
                quantity: 2
                unitPrice: 9.99
                discountPerUnit: 0
              - id: "323e4567-e89b-12d3-a456-426614174002"
                productId: "prod-002"
                productName: "Vitamin C 1000mg"
                quantity: 1
                unitPrice: 15.50
                discountPerUnit: 2.00
            afterwardsItems: []
            createdAt: "2026-04-01T10:00:00Z"
            updatedAt: "2026-04-29T09:30:00Z"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "update-my-cart-items"
    method: "PUT"
    urlPath: "/api/v2/carts/users/items/{userId}"
    summary: "Update cart items"
    description: "Updates items in the authenticated user's cart (add/update/remove items)"
    authenticated: true
    rateLimit: "PLACEHOLDER: Not annotated with @RateLimit"
    tags:
      - "Cart Management"
      - "PUT"
    parameters:
      - name: "userId"
        in: "path"
        type: "string"
        required: true
        description: "User ID from JWT token"
        example: "usr-123e4567-e89b"
    requestBody:
      contentType: "application/json"
      schema:
        type: "object"
        required: ["items"]
        properties:
          items:
            type: "array"
            items:
              type: "object"
              properties:
                productId: type: "string"
                quantity: type: "integer"
                unitPrice: type: "number"
                discountPerUnit: type: "number"
      example:
        items:
          - productId: "prod-001"
            quantity: 3
            unitPrice: 9.99
            discountPerUnit: 0
    responses:
      - status: 200
        description: "Cart items updated successfully"
        example:
          success: true
          message: "Product Items successfully updated in cart"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "move-to-afterwards"
    method: "POST"
    urlPath: "/api/v2/carts/users/items/move-to-afterwards"
    summary: "Move items to afterwards"
    description: "Moves selected items from cart to afterwards (save-for-later) list"
    authenticated: true
    rateLimit: "PLACEHOLDER: Not annotated with @RateLimit"
    tags:
      - "Afterwards"
      - "POST"
    parameters:
      - name: "userId"
        in: "requestAttribute"
        type: "string"
        required: true
        description: "User ID from JWT token"
    requestBody:
      contentType: "application/json"
      schema:
        type: "object"
        required: ["productIds"]
        properties:
          productIds:
            type: "array"
            items:
              type: "string"
      example:
        productIds:
          - "prod-001"
          - "prod-002"
    responses:
      - status: 200
        description: "Items moved to afterwards"
        example:
          success: true
          message: "Moved to Afterwards"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "restore-from-afterwards"
    method: "PUT"
    urlPath: "/api/v2/carts/users/items/restore-from-afterwards"
    summary: "Restore items from afterwards"
    description: "Restores items from afterwards list back to active cart"
    authenticated: true
    rateLimit: "PLACEHOLDER: Not annotated with @RateLimit"
    tags:
      - "Afterwards"
      - "PUT"
    parameters:
      - name: "userId"
        in: "requestAttribute"
        type: "string"
        required: true
        description: "User ID from JWT token"
    requestBody:
      contentType: "application/json"
      schema:
        type: "object"
        required: ["productIds"]
        properties:
          productIds:
            type: "array"
            items:
              type: "string"
      example:
        productIds:
          - "prod-001"
    responses:
      - status: 200
        description: "Items restored to cart"
        example:
          success: true
          message: "Returned to Cart"
          timestamp: "2026-04-29T10:30:00Z"

  # ========== ADMIN ENDPOINTS (/api/v2/carts/admin) ==========

  - id: "get-customer-cart"
    method: "GET"
    urlPath: "/api/v2/carts/admin/customers/{customerId}"
    summary: "Get customer cart"
    description: "Retrieves the cart for a specific customer (Admin only)"
    authenticated: true
    rateLimit: "PLACEHOLDER: Not annotated with @RateLimit"
    tags:
      - "Admin Cart Management"
      - "GET"
    parameters:
      - name: "customerId"
        in: "path"
        type: "string"
        required: true
        description: "Customer ID"
        example: "usr-123e4567-e89b"
    requestBody: null
    responses:
      - status: 200
        description: "Customer cart retrieved"
        example:
          success: true
          message: "Customer Cart found"
          data:
            id: "123e4567-e89b-12d3-a456-426614174000"
            customerId: "usr-123e4567-e89b"
            items: []
            afterwardsItems: []
          timestamp: "2026-04-29T10:30:00Z"
      - status: 403
        description: "Access denied - ADMIN role required"
        example:
          success: false
          message: "Access denied - ADMIN role required"
          errorCode: "FORBIDDEN"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "get-cart-by-id"
    method: "GET"
    urlPath: "/api/v2/carts/admin/{cartId}"
    summary: "Get cart by ID"
    description: "Retrieves a specific cart by its ID (Admin only) with optional items/afterwards inclusion"
    authenticated: true
    rateLimit: "PLACEHOLDER: Not annotated with @RateLimit"
    tags:
      - "Admin Cart Management"
      - "GET"
    parameters:
      - name: "cartId"
        in: "path"
        type: "string"
        required: true
        description: "Cart ID (UUID)"
        example: "123e4567-e89b-12d3-a456-426614174000"
      - name: "includeItems"
        in: "query"
        type: "boolean"
        required: false
        description: "Include cart items in response"
        example: true
      - name: "includeAfterwards"
        in: "query"
        type: "boolean"
        required: false
        description: "Include afterwards items in response"
        example: true
    requestBody: null
    responses:
      - status: 200
        description: "Cart retrieved"
        example:
          success: true
          message: "Cart found"
          data:
            id: "123e4567-e89b-12d3-a456-426614174000"
            customerId: "usr-123e4567-e89b"
            items: []
            afterwardsItems: []
          timestamp: "2026-04-29T10:30:00Z"
      - status: 404
        description: "Cart not found"
        example:
          success: false
          message: "Cart not found with id: 999e4567-e89b-12d3-a456-426614174999"
          errorCode: "NOT_FOUND"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "search-carts"
    method: "GET"
    urlPath: "/api/v2/carts/admin/search"
    summary: "Search carts"
    description: "Searches carts with filters and pagination (Admin only)"
    authenticated: true
    rateLimit: "PLACEHOLDER: Not annotated with @RateLimit"
    tags:
      - "Admin Cart Management"
      - "GET"
    parameters:
      - name: "customerId"
        in: "query"
        type: "string"
        required: false
        description: "Filter by customer ID"
      - name: "page"
        in: "query"
        type: "integer"
        required: false
        description: "Page number (0-indexed)"
      - name: "size"
        in: "query"
        type: "integer"
        required: false
        description: "Page size (default 20)"
    requestBody: null
    responses:
      - status: 200
        description: "Search results"
        example:
          success: true
          message: "Search Carts found"
          data:
            content:
              - id: "123e4567-e89b-12d3-a456-426614174000"
                customerId: "usr-123e4567-e89b"
                items: []
            pageable:
              pageNumber: 0
              pageSize: 20
            totalElements: 45
            totalPages: 3
          timestamp: "2026-04-29T10:30:00Z"

# GrpcEndpoint[]
grpcEndpoints:
  - id: "get-user-cart"
    service: "CartService"
    method: "GetUserCart"
    description: "gRPC method for order-service to retrieve user's cart during checkout"
    requestType: "GetUserCartRequest"
    responseType: "CartResponse"
    example:
      request:
        userId: "usr-123e4567-e89b"
      response:
        id: "123e4567-e89b-12d3-a456-426614174000"
        customerId: "usr-123e4567-e89b"
        items:
          - productId: "prod-001"
            productName: "Aspirin 500mg"
            quantity: 2
            unitPrice: 9.99
            discountPerUnit: 0

  - id: "clear-cart"
    service: "CartService"
    method: "ClearCart"
    description: "gRPC method for order-service to clear cart after successful order"
    requestType: "ClearCartRequest"
    responseType: "ClearCartResponse"
    example:
      request:
        userId: "usr-123e4567-e89b"
        productIdsToExclude: ["prod-002"]
      response:
        success: true
        message: "Cart cleared successfully"
---
# API Schema

> 7 REST endpoints (4 user + 3 admin) and 2 gRPC methods documented. PLACEHOLDER issues: No @RateLimit annotations on any REST controllers (unlike address-service), CartPurchasedEvent defined but not published to Kafka. The service uses @PreAuthorize("hasRole('ADMIN')") on CartManagerController. Potential: Add OpenAPI annotations to all endpoints, implement rate limiting, publish cart events to Kafka.
