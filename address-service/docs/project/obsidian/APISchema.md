---
# APISchema
type: "REST"

# ApiEndpoint[]
httpEndpoints:
  # ========== USER ENDPOINTS (/api/v2/user/addresses) ==========

  - id: "get-my-addresses"
    method: "GET"
    urlPath: "/api/v2/user/addresses"
    summary: "Get my addresses"
    description: "Retrieves all active addresses for the authenticated user"
    authenticated: true
    rateLimit: "STANDARD (60 requests per minute per user)"
    tags:
      - "User Address Management"
      - "GET"
    parameters: []
    requestBody: null
    responses:
      - status: 200
        description: "Successfully retrieved addresses"
        schema:
          type: "object"
          properties:
            success: type: "boolean"
            message: type: "string"
            data: type: "array"
            timestamp: type: "string"
        example:
          success: true
          message: "Operation completed successfully"
          data:
            - id: "123e4567-e89b-12d3-a456-426614174000"
              street: "123 Main St"
              city: "New York"
              country: "US"
              isDefault: true
            - id: "223e4567-e89b-12d3-a456-426614174001"
              street: "456 Oak Avenue"
              city: "Los Angeles"
              country: "US"
              isDefault: false
          timestamp: "2026-02-24T10:30:00Z"
      - status: 401
        description: "Unauthorized - Authentication required"
        schema:
          type: "object"
          properties:
            success: type: "boolean"
            message: type: "string"
            errorCode: type: "string"
            timestamp: type: "string"
        example:
          success: false
          message: "Authentication required"
          errorCode: "UNAUTHORIZED"
          timestamp: "2026-02-24T10:30:00Z"

  - id: "get-address-by-id"
    method: "GET"
    urlPath: "/api/v2/user/addresses/{addressId}"
    summary: "Get address by ID"
    description: "Retrieves a specific address by its ID for the authenticated user"
    authenticated: true
    rateLimit: "STANDARD (60 requests per minute per user)"
    tags:
      - "User Address Management"
      - "GET"
    parameters:
      - name: "addressId"
        in: "path"
        type: "string"
        required: true
        description: "Address ID (UUID)"
        example: "123e4567-e89b-12d3-a456-426614174000"
    requestBody: null
    responses:
      - status: 200
        description: "Successfully retrieved address"
        schema:
          type: "object"
          properties:
            success: type: "boolean"
            message: type: "string"
            data: type: "object"
            timestamp: type: "string"
        example:
          success: true
          message: "Operation completed successfully"
          data:
            id: "123e4567-e89b-12d3-a456-426614174000"
            userId: "usr-123"
            street: "123 Main St"
            city: "New York"
            state: "NY"
            country: "US"
            postalCode: "10001"
            additionalDetails: "Apt 4B"
            isDefault: true
            createdAt: "2026-01-15T10:30:00Z"
            updatedAt: "2026-02-20T14:20:00Z"
          timestamp: "2026-02-24T10:30:00Z"
      - status: 403
        description: "Access denied - Address belongs to another user"
        example:
          success: false
          message: "User usr-456 is not authorized to access address 123e4567-e89b-12d3-a456-426614174000"
          errorCode: "ADDRESS_ACCESS_UNAUTHORIZED"
          timestamp: "2026-02-24T10:30:00Z"
      - status: 404
        description: "Address not found"
        example:
          success: false
          message: "Address not found with id: 999e4567-e89b-12d3-a456-426614174999 for user usr-123"
          errorCode: "NOT_FOUND"
          timestamp: "2026-02-24T10:30:00Z"

  - id: "create-address"
    method: "POST"
    urlPath: "/api/v2/user/addresses"
    summary: "Create new address"
    description: "Creates a new address for the authenticated user. Customers can have up to 5 addresses, employees up to 1."
    authenticated: true
    rateLimit: "SENSITIVE (10 requests per minute per user)"
    tags:
      - "User Address Management"
      - "POST"
    parameters: []
    requestBody:
      contentType: "application/json"
      schema:
        type: "object"
        required: ["street", "city", "state", "country", "postalCode"]
        properties:
          street: type: "string", maxLength: 200
          city: type: "string", maxLength: 100
          state: type: "string", maxLength: 100
          country: type: "string", pattern: "^[A-Z]{2}$"
          postalCode: type: "string", maxLength: 20
          additionalDetails: type: "string", maxLength: 200
          isDefault: type: "boolean"
      example:
        street: "123 Main St"
        city: "New York"
        state: "NY"
        country: "US"
        postalCode: "10001"
        additionalDetails: "Apt 4B"
        isDefault: true
    responses:
      - status: 201
        description: "Address created successfully"
        schema:
          type: "object"
          properties:
            success: type: "boolean"
            message: type: "string"
            data: type: "object"
            timestamp: type: "string"
        example:
          success: true
          message: "Address created successfully"
          data:
            id: "123e4567-e89b-12d3-a456-426614174000"
            userId: "usr-123"
            street: "123 Main St"
            city: "New York"
            state: "NY"
            country: "US"
            postalCode: "10001"
            additionalDetails: "Apt 4B"
            isDefault: true
            createdAt: "2026-02-24T10:30:00Z"
            updatedAt: "2026-02-24T10:30:00Z"
          timestamp: "2026-02-24T10:30:00Z"
      - status: 400
        description: "Invalid input data"
        example:
          success: false
          message: "City is required"
          errorCode: "invalid_address"
          timestamp: "2026-02-24T10:30:00Z"
      - status: 401
        description: "Unauthorized - Authentication required"
        example:
          success: false
          message: "Authentication required"
          errorCode: "UNAUTHORIZED"
          timestamp: "2026-02-24T10:30:00Z"
      - status: 403
        description: "Address limit exceeded"
        example:
          success: false
          message: "User usr-123 of type CUSTOMER has reached the address limit of 5"
          errorCode: "ADDRESS_LIMIT_EXCEEDED"
          timestamp: "2026-02-24T10:30:00Z"
      - status: 422
        description: "Business rule violation (invalid postal code)"
        example:
          success: false
          message: "Invalid postal code format for country: US"
          errorCode: "invalid_address"
          timestamp: "2026-02-24T10:30:00Z"

  - id: "update-address"
    method: "PUT"
    urlPath: "/api/v2/user/addresses/{addressId}"
    summary: "Update address"
    description: "Updates an existing address for the authenticated user"
    authenticated: true
    rateLimit: "SENSITIVE (10 requests per minute per user)"
    tags:
      - "User Address Management"
      - "PUT"
    parameters:
      - name: "addressId"
        in: "path"
        type: "string"
        required: true
        description: "Address ID (UUID)"
        example: "123e4567-e89b-12d3-a456-426614174000"
    requestBody:
      contentType: "application/json"
      schema:
        type: "object"
        required: ["street", "city", "state", "country", "postalCode"]
        properties:
          street: type: "string", maxLength: 200
          city: type: "string", maxLength: 100
          state: type: "string", maxLength: 100
          country: type: "string", pattern: "^[A-Z]{2}$"
          postalCode: type: "string", maxLength: 20
          additionalDetails: type: "string", maxLength: 200
          isDefault: type: "boolean"
      example:
        street: "456 Updated Ave"
        city: "Boston"
        state: "MA"
        country: "US"
        postalCode: "02101"
        additionalDetails: "Suite 200"
        isDefault: false
    responses:
      - status: 200
        description: "Address updated successfully"
        example:
          success: true
          message: "Operation completed successfully"
          data:
            id: "123e4567-e89b-12d3-a456-426614174000"
            userId: "usr-123"
            street: "456 Updated Ave"
            city: "Boston"
            state: "MA"
            country: "US"
            postalCode: "02101"
            additionalDetails: "Suite 200"
            isDefault: false
            createdAt: "2026-01-15T10:30:00Z"
            updatedAt: "2026-02-24T10:30:00Z"
          timestamp: "2026-02-24T10:30:00Z"
      - status: 400
        description: "Invalid input data"
        example:
          success: false
          message: "Street is required"
          errorCode: "invalid_address"
          timestamp: "2026-02-24T10:30:00Z"
      - status: 403
        description: "Access denied - Address belongs to another user"
        example:
          success: false
          message: "User usr-456 is not authorized to access address 123e4567-e89b-12d3-a456-426614174000"
          errorCode: "ADDRESS_ACCESS_UNAUTHORIZED"
          timestamp: "2026-02-24T10:30:00Z"
      - status: 404
        description: "Address not found"
        example:
          success: false
          message: "Address not found with id: 999e4567-e89b-12d3-a456-426614174999"
          errorCode: "NOT_FOUND"
          timestamp: "2026-02-24T10:30:00Z"
      - status: 422
        description: "Invalid postal code format"
        example:
          success: false
          message: "Invalid postal code format for country: US"
          errorCode: "invalid_address"
          timestamp: "2026-02-24T10:30:00Z"

  - id: "delete-address"
    method: "DELETE"
    urlPath: "/api/v2/user/addresses/{addressId}"
    summary: "Delete address"
    description: "Soft deletes an address for the authenticated user"
    authenticated: true
    rateLimit: "SENSITIVE (10 requests per minute per user)"
    tags:
      - "User Address Management"
      - "DELETE"
    parameters:
      - name: "addressId"
        in: "path"
        type: "string"
        required: true
        description: "Address ID (UUID)"
        example: "123e4567-e89b-12d3-a456-426614174000"
    requestBody: null
    responses:
      - status: 200
        description: "Address deleted successfully"
        example:
          success: true
          message: "Address deleted successfully"
          timestamp: "2026-02-24T10:30:00Z"
      - status: 403
        description: "Access denied - Address belongs to another user"
        example:
          success: false
          message: "User usr-456 is not authorized to access address 123e4567-e89b-12d3-a456-426614174000"
          errorCode: "ADDRESS_ACCESS_UNAUTHORIZED"
          timestamp: "2026-02-24T10:30:00Z"
      - status: 404
        description: "Address not found"
        example:
          success: false
          message: "Address not found with id: 999e4567-e89b-12d3-a456-426614174999"
          errorCode: "NOT_FOUND"
          timestamp: "2026-02-24T10:30:00Z"

  - id: "set-default-address"
    method: "PUT"
    urlPath: "/api/v2/user/addresses/{addressId}/set-default"
    summary: "Set address as default"
    description: "Sets a specific address as the default address for the authenticated user. Only one address can be default at a time."
    authenticated: true
    rateLimit: "STANDARD (60 requests per minute per user)"
    tags:
      - "User Address Management"
      - "PUT"
    parameters:
      - name: "addressId"
        in: "path"
        type: "string"
        required: true
        description: "Address ID (UUID)"
        example: "123e4567-e89b-12d3-a456-426614174000"
    requestBody: null
    responses:
      - status: 200
        description: "Address set as default successfully"
        example:
          success: true
          message: "Operation completed successfully"
          data:
            id: "123e4567-e89b-12d3-a456-426614174000"
            userId: "usr-123"
            street: "123 Main St"
            city: "New York"
            state: "NY"
            country: "US"
            postalCode: "10001"
            additionalDetails: "Apt 4B"
            isDefault: true
            createdAt: "2026-01-15T10:30:00Z"
            updatedAt: "2026-02-24T10:30:00Z"
          timestamp: "2026-02-24T10:30:00Z"
      - status: 403
        description: "Access denied - Address belongs to another user"
        example:
          success: false
          message: "User usr-456 is not authorized to access address 123e4567-e89b-12d3-a456-426614174000"
          errorCode: "ADDRESS_ACCESS_UNAUTHORIZED"
          timestamp: "2026-02-24T10:30:00Z"
      - status: 404
        description: "Address not found"
        example:
          success: false
          message: "Address not found with id: 999e4567-e89b-12d3-a456-426614174999 for user usr-123"
          errorCode: "NOT_FOUND"
          timestamp: "2026-02-24T10:30:00Z"

  # ========== ADMIN ENDPOINTS (/api/v2/addresses/admin) ==========

  - id: "get-all-addresses"
    method: "GET"
    urlPath: "/api/v2/addresses/admin"
    summary: "Get all addresses with pagination"
    description: "Retrieves a paginated list of all active addresses in the system (Admin only)"
    authenticated: true
    rateLimit: "STANDARD (60 requests per minute per admin)"
    tags:
      - "Admin Address Management"
      - "GET"
    parameters:
      - name: "page"
        in: "query"
        type: "integer"
        required: false
        description: "Page number (0-indexed)"
        example: 0
      - name: "size"
        in: "query"
        type: "integer"
        required: false
        description: "Page size (default: 20)"
        example: 20
    requestBody: null
    responses:
      - status: 200
        description: "Successfully retrieved addresses"
        schema:
          type: "object"
          properties:
            content: type: "array"
            pageable: type: "object"
            totalElements: type: "integer"
            totalPages: type: "integer"
        example:
          content:
            - id: "123e4567-e89b-12d3-a456-426614174000"
              street: "123 Main St"
              city: "New York"
              country: "US"
              isDefault: true
            - id: "223e4567-e89b-12d3-a456-426614174001"
              street: "456 Oak Avenue"
              city: "Los Angeles"
              country: "US"
              isDefault: false
          pageable:
            pageNumber: 0
            pageSize: 20
            sort:
              sorted: false
              unsorted: true
              empty: true
          totalElements: 150
          totalPages: 8
          last: false
          first: true
          size: 20
          number: 0
      - status: 403
        description: "Access denied - ADMIN role required"
        example:
          success: false
          message: "Access denied - ADMIN role required"
          errorCode: "FORBIDDEN"
          timestamp: "2026-02-24T10:30:00Z"

  - id: "get-address-by-id-admin"
    method: "GET"
    urlPath: "/api/v2/addresses/admin/{id}"
    summary: "Get address by ID (Admin)"
    description: "Retrieves detailed address information by its ID (Admin only)"
    authenticated: true
    rateLimit: "STANDARD (60 requests per minute per admin)"
    tags:
      - "Admin Address Management"
      - "GET"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Address ID (UUID)"
        example: "123e4567-e89b-12d3-a456-426614174000"
    requestBody: null
    responses:
      - status: 200
        description: "Successfully retrieved address"
        example:
          id: "123e4567-e89b-12d3-a456-426614174000"
          userId: "usr-123"
          street: "123 Main St"
          city: "New York"
          state: "NY"
          country: "US"
          postalCode: "10001"
          additionalDetails: "Apt 4B"
          isDefault: true
          createdAt: "2026-01-15T10:30:00Z"
          updatedAt: "2026-02-20T14:20:00Z"
      - status: 403
        description: "Access denied - ADMIN role required"
        example:
          success: false
          message: "Access denied - ADMIN role required"
          errorCode: "FORBIDDEN"
          timestamp: "2026-02-24T10:30:00Z"
      - status: 404
        description: "Address not found"
        example:
          success: false
          message: "Address not found with id: 999e4567-e89b-12d3-a456-426614174999"
          errorCode: "NOT_FOUND"
          timestamp: "2026-02-24T10:30:00Z"

  - id: "get-addresses-by-user-id"
    method: "GET"
    urlPath: "/api/v2/addresses/admin/user/{userId}"
    summary: "Get addresses by user ID"
    description: "Retrieves all addresses for a specific user (Admin only)"
    authenticated: true
    rateLimit: "STANDARD (60 requests per minute per admin)"
    tags:
      - "Admin Address Management"
      - "GET"
    parameters:
      - name: "userId"
        in: "path"
        type: "string"
        required: true
        description: "User ID"
        example: "usr-123"
    requestBody: null
    responses:
      - status: 200
        description: "Successfully retrieved user addresses"
        example:
          - id: "123e4567-e89b-12d3-a456-426614174000"
            userId: "usr-123"
            street: "123 Main St"
            city: "New York"
            state: "NY"
            country: "US"
            postalCode: "10001"
            additionalDetails: "Apt 4B"
            isDefault: true
            createdAt: "2026-01-15T10:30:00Z"
            updatedAt: "2026-02-20T14:20:00Z"
          - id: "223e4567-e89b-12d3-a456-426614174001"
            userId: "usr-123"
            street: "456 Oak Avenue"
            city: "Los Angeles"
            state: "CA"
            country: "US"
            postalCode: "90001"
            additionalDetails: null
            isDefault: false
            createdAt: "2026-02-10T08:15:00Z"
            updatedAt: "2026-02-10T08:15:00Z"
      - status: 403
        description: "Access denied - ADMIN role required"
        example:
          success: false
          message: "Access denied - ADMIN role required"
          errorCode: "FORBIDDEN"
          timestamp: "2026-02-24T10:30:00Z"

  - id: "create-address-for-user"
    method: "POST"
    urlPath: "/api/v2/addresses/admin"
    summary: "Create address for any user"
    description: "Creates a new address for any user in the system (Admin only)"
    authenticated: true
    rateLimit: "STANDARD (60 requests per minute per admin)"
    tags:
      - "Admin Address Management"
      - "POST"
    parameters:
      - name: "userId"
        in: "query"
        type: "string"
        required: true
        description: "User ID to assign the address to"
        example: "usr-456"
    requestBody:
      contentType: "application/json"
      schema:
        type: "object"
        required: ["street", "city", "state", "country", "postalCode"]
        properties:
          street: type: "string", maxLength: 200
          city: type: "string", maxLength: 100
          state: type: "string", maxLength: 100
          country: type: "string", pattern: "^[A-Z]{2}$"
          postalCode: type: "string", maxLength: 20
          additionalDetails: type: "string", maxLength: 200
          isDefault: type: "boolean"
      example:
        street: "789 Broadway"
        city: "Chicago"
        state: "IL"
        country: "US"
        postalCode: "60601"
        additionalDetails: "Floor 5"
        isDefault: true
    responses:
      - status: 201
        description: "Address created successfully"
        example:
          id: "123e4567-e89b-12d3-a456-426614174000"
          userId: "usr-456"
          street: "789 Broadway"
          city: "Chicago"
          state: "IL"
          country: "US"
          postalCode: "60601"
          additionalDetails: "Floor 5"
          isDefault: true
          createdAt: "2026-02-24T10:30:00Z"
          updatedAt: "2026-02-24T10:30:00Z"
      - status: 400
        description: "Invalid input data"
        example:
          success: false
          message: "Postal code is required"
          errorCode: "invalid_address"
          timestamp: "2026-02-24T10:30:00Z"
      - status: 403
        description: "Access denied or address limit exceeded"
        example:
          success: false
          message: "User usr-456 of type EMPLOYEE has reached the address limit of 1"
          errorCode: "ADDRESS_LIMIT_EXCEEDED"
          timestamp: "2026-02-24T10:30:00Z"

  - id: "update-address-admin"
    method: "PUT"
    urlPath: "/api/v2/addresses/admin/{id}"
    summary: "Update any address"
    description: "Updates an existing address by ID (Admin only)"
    authenticated: true
    rateLimit: "STANDARD (60 requests per minute per admin)"
    tags:
      - "Admin Address Management"
      - "PUT"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Address ID (UUID)"
        example: "123e4567-e89b-12d3-a456-426614174000"
    requestBody:
      contentType: "application/json"
      schema:
        type: "object"
        required: ["street", "city", "state", "country", "postalCode"]
        properties:
          street: type: "string", maxLength: 200
          city: type: "string", maxLength: 100
          state: type: "string", maxLength: 100
          country: type: "string", pattern: "^[A-Z]{2}$"
          postalCode: type: "string", maxLength: 20
          additionalDetails: type: "string", maxLength: 200
          isDefault: type: "boolean"
      example:
        street: "999 Updated Street"
        city: "Miami"
        state: "FL"
        country: "US"
        postalCode: "33101"
        additionalDetails: "Building A"
        isDefault: true
    responses:
      - status: 200
        description: "Address updated successfully"
        example:
          id: "123e4567-e89b-12d3-a456-426614174000"
          userId: "usr-123"
          street: "999 Updated Street"
          city: "Miami"
          state: "FL"
          country: "US"
          postalCode: "33101"
          additionalDetails: "Building A"
          isDefault: true
          createdAt: "2026-01-15T10:30:00Z"
          updatedAt: "2026-02-24T10:30:00Z"
      - status: 400
        description: "Invalid input data"
        example:
          success: false
          message: "State is required"
          errorCode: "invalid_address"
          timestamp: "2026-02-24T10:30:00Z"
      - status: 403
        description: "Access denied - ADMIN role required"
        example:
          success: false
          message: "Access denied - ADMIN role required"
          errorCode: "FORBIDDEN"
          timestamp: "2026-02-24T10:30:00Z"
      - status: 404
        description: "Address not found"
        example:
          success: false
          message: "Address not found with id: 999e4567-e89b-12d3-a456-426614174999"
          errorCode: "NOT_FOUND"
          timestamp: "2026-02-24T10:30:00Z"

  - id: "delete-address-admin"
    method: "DELETE"
    urlPath: "/api/v2/addresses/admin/{id}"
    summary: "Delete any address"
    description: "Soft deletes an address by ID (Admin only)"
    authenticated: true
    rateLimit: "STANDARD (60 requests per minute per admin)"
    tags:
      - "Admin Address Management"
      - "DELETE"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Address ID (UUID)"
        example: "123e4567-e89b-12d3-a456-426614174000"
    requestBody: null
    responses:
      - status: 204
        description: "Address deleted successfully"
      - status: 403
        description: "Access denied - ADMIN role required"
        example:
          success: false
          message: "Access denied - ADMIN role required"
          errorCode: "FORBIDDEN"
          timestamp: "2026-02-24T10:30:00Z"
      - status: 404
        description: "Address not found"
        example:
          success: false
          message: "Address not found with id: 999e4567-e89b-12d3-a456-426614174999"
          errorCode: "NOT_FOUND"
          timestamp: "2026-02-24T10:30:00Z"

  - id: "set-default-address-admin"
    method: "PUT"
    urlPath: "/api/v2/addresses/admin/{id}/set-default-for-user/{userId}"
    summary: "Set address as default for a user"
    description: "Sets a specific address as default for any user (Admin only). Only one address can be default per user."
    authenticated: true
    rateLimit: "STANDARD (60 requests per minute per admin)"
    tags:
      - "Admin Address Management"
      - "PUT"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Address ID (UUID)"
        example: "123e4567-e89b-12d3-a456-426614174000"
      - name: "userId"
        in: "path"
        type: "string"
        required: true
        description: "User ID"
        example: "usr-789"
    requestBody: null
    responses:
      - status: 200
        description: "Address set as default successfully"
        example:
          id: "123e4567-e89b-12d3-a456-426614174000"
          userId: "usr-789"
          street: "555 Pine Road"
          city: "Seattle"
          state: "WA"
          country: "US"
          postalCode: "98101"
          additionalDetails: null
          isDefault: true
          createdAt: "2026-01-20T09:00:00Z"
          updatedAt: "2026-02-24T10:30:00Z"
      - status: 403
        description: "Access denied - ADMIN role required"
        example:
          success: false
          message: "Access denied - ADMIN role required"
          errorCode: "FORBIDDEN"
          timestamp: "2026-02-24T10:30:00Z"
      - status: 404
        description: "Address or user not found"
        example:
          success: false
          message: "Address not found with id: 999e4567-e89b-12d3-a456-426614174999 for user usr-789"
          errorCode: "NOT_FOUND"
          timestamp: "2026-02-24T10:30:00Z"
---
# API Schema

> Complete REST API with 10 endpoints (6 user endpoints + 7 admin endpoints, with some overlap). All endpoints require JWT authentication. Rate limiting is implemented via Redis with two profiles: STANDARD (60/min) for reads, SENSITIVE (10/min) for writes. Multi-country postal code validation supported (US, MX, CA, ES, UK). ResponseWrapper pattern used for consistent API responses. Potential improvements: add OpenAPI specification export, implement HATEOAS links, add bulk operations endpoint, and integrate with Kafka for address change events.
