---
type: Mixed
httpEndpoints:
  # address-service
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
            success:
              type: "boolean"
            message:
              type: "string"
            data:
              type: "array"
            timestamp:
              type: "string"
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
            success:
              type: "boolean"
            message:
              type: "string"
            errorCode:
              type: "string"
            timestamp:
              type: "string"
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
            success:
              type: "boolean"
            message:
              type: "string"
            data:
              type: "object"
            timestamp:
              type: "string"
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
          street:
            type: "string"
            maxLength: 200
          city:
            type: "string"
            maxLength: 100
          state:
            type: "string"
            maxLength: 100
          country:
            type: "string"
            pattern: "^[A-Z]{2}$"
          postalCode:
            type: "string"
            maxLength: 20
          additionalDetails:
            type: "string"
            maxLength: 200
          isDefault:
            type: "boolean"
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
            success:
              type: "boolean"
            message:
              type: "string"
            data:
              type: "object"
            timestamp:
              type: "string"
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
          street:
            type: "string"
            maxLength: 200
          city:
            type: "string"
            maxLength: 100
          state:
            type: "string"
            maxLength: 100
          country:
            type: "string"
            pattern: "^[A-Z]{2}$"
          postalCode:
            type: "string"
            maxLength: 20
          additionalDetails:
            type: "string"
            maxLength: 200
          isDefault:
            type: "boolean"
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
            content:
              type: "array"
            pageable:
              type: "object"
            totalElements:
              type: "integer"
            totalPages:
              type: "integer"
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
          street:
            type: "string"
            maxLength: 200
          city:
            type: "string"
            maxLength: 100
          state:
            type: "string"
            maxLength: 100
          country:
            type: "string"
            pattern: "^[A-Z]{2}$"
          postalCode:
            type: "string"
            maxLength: 20
          additionalDetails:
            type: "string"
            maxLength: 200
          isDefault:
            type: "boolean"
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
          street:
            type: "string"
            maxLength: 200
          city:
            type: "string"
            maxLength: 100
          state:
            type: "string"
            maxLength: 100
          country:
            type: "string"
            pattern: "^[A-Z]{2}$"
          postalCode:
            type: "string"
            maxLength: 20
          additionalDetails:
            type: "string"
            maxLength: 200
          isDefault:
            type: "boolean"
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
  # auth-service
  # ========== AUTHENTICATION ENDPOINTS (/api/v2/auth) ==========

  - id: "login"
    method: "POST"
    urlPath: "/api/v2/auth/login"
    summary: "User login"
    description: "Authenticates user with email/phone and password, returns JWT access + refresh tokens"
    authenticated: false
    rateLimit: "SENSITIVE (10 requests per minute per IP)"
    tags:
      - "Authentication"
      - "POST"
    parameters: []
    requestBody:
      contentType: "application/json"
      schema:
        type: "object"
        required: ["emailOrPhoneNumber", "password"]
        properties:
          emailOrPhoneNumber:
            type: "string"
          password:
            type: "string"
      example:
        emailOrPhoneNumber: "user@example.com"
        password: "SecurePass123!"
    responses:
      - status: 200
        description: "Login successful"
        schema:
          type: "object"
          properties:
            success:
              type: "boolean"
            message:
              type: "string"
            data:
              type: "object"
            timestamp:
              type: "string"
        example:
          success: true
          message: "Login successfully processed"
          data:
            accessToken: "eyJhbGciOiJIUzI1NiJ9..."
            refreshToken: "eyJhbGciOiJIUzI1NiJ9..."
            userId: "usr-123e4567-e89b"
            role: "CUSTOMER"
            expiresIn: 900000
          timestamp: "2026-04-29T10:30:00Z"
      - status: 401
        description: "Invalid credentials"
        example:
          success: false
          message: "Invalid email/phone or password"
          errorCode: "INVALID_CREDENTIALS"
          timestamp: "2026-04-29T10:30:00Z"
      - status: 423
        description: "Two-factor authentication required"
        example:
          success: false
          message: "Two-factor authentication required"
          errorCode: "TWO_FACTOR_REQUIRED"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "two-factor-login"
    method: "POST"
    urlPath: "/api/v2/auth/login/2fa"
    summary: "Two-factor login"
    description: "Second step of 2FA login with TOTP code"
    authenticated: false
    rateLimit: "SENSITIVE (10 requests per minute per IP)"
    tags:
      - "Authentication"
      - "POST"
    parameters: []
    requestBody:
      contentType: "application/json"
      schema:
        type: "object"
        required: ["email", "code"]
        properties:
          email:
            type: "string"
          code:
            type: "string"
      example:
        email: "user@example.com"
        code: "123456"
    responses:
      - status: 200
        description: "2FA login successful"
        example:
          success: true
          message: "2FA login successfully processed"
          data:
            accessToken: "eyJhbGciOiJIUzI1NiJ9..."
            refreshToken: "eyJhbGciOiJIUzI1NiJ9..."
            userId: "usr-123e4567-e89b"
            role: "CUSTOMER"
            expiresIn: 900000
          timestamp: "2026-04-29T10:30:00Z"
      - status: 401
        description: "Invalid 2FA code"
        example:
          success: false
          message: "Invalid two-factor code"
          errorCode: "INVALID_TWO_FACTOR_CODE"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "refresh-session"
    method: "PATCH"
    urlPath: "/api/v2/auth/refresh-session/{refreshToken}"
    summary: "Refresh access token"
    description: "Uses refresh token to generate a new access token"
    authenticated: false
    rateLimit: "STANDARD (60 requests per minute per IP)"
    tags:
      - "Authentication"
      - "PATCH"
    parameters:
      - name: "refreshToken"
        in: "path"
        type: "string"
        required: true
        description: "Refresh token from previous login"
        example: "eyJhbGciOiJIUzI1NiJ9..."
    requestBody: null
    responses:
      - status: 200
        description: "Token refreshed successfully"
        example:
          success: true
          message: "Access token refreshed successfully"
          data:
            accessToken: "eyJhbGciOiJIUzI1NiJ9..."
            refreshToken: "eyJhbGciOiJIUzI1NiJ9..."
            expiresIn: 900000
          timestamp: "2026-04-29T10:30:00Z"
      - status: 401
        description: "Invalid or expired refresh token"
        example:
          success: false
          message: "Invalid or expired refresh token"
          errorCode: "INVALID_REFRESH_TOKEN"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "logout"
    method: "POST"
    urlPath: "/api/v2/auth/logout/{refreshToken}"
    summary: "Logout single session"
    description: "Invalidates the refresh token (session) for a single device"
    authenticated: false
    rateLimit: "SENSITIVE (10 requests per minute per IP)"
    tags:
      - "Authentication"
      - "POST"
    parameters:
      - name: "refreshToken"
        in: "path"
        type: "string"
        required: true
        description: "Refresh token to invalidate"
        example: "eyJhbGciOiJIUzI1NiJ9..."
    requestBody: null
    responses:
      - status: 200
        description: "Logout successful"
        example:
          success: true
          message: "Logout successfully processed"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "logout-all"
    method: "POST"
    urlPath: "/api/v2/auth/logout-all"
    summary: "Logout all sessions"
    description: "Invalidates all refresh tokens (sessions) for the authenticated user"
    authenticated: true
    rateLimit: "SENSITIVE (10 requests per minute per user)"
    tags:
      - "Authentication"
      - "POST"
    parameters: []
    requestBody: null
    responses:
      - status: 200
        description: "All sessions logged out"
        example:
          success: true
          message: "All sessions logged out successfully"
          timestamp: "2026-04-29T10:30:00Z"

  # ========== REGISTRATION ENDPOINTS ==========

  - id: "register-customer"
    method: "POST"
    urlPath: "/api/v2/auth/register/customer"
    summary: "Register customer"
    description: "Creates a new user with CUSTOMER role"
    authenticated: false
    rateLimit: "SENSITIVE (10 requests per minute per IP)"
    tags:
      - "Registration"
      - "POST"
    parameters: []
    requestBody:
      contentType: "application/json"
      schema:
        type: "object"
        required: ["email", "password", "name", "phoneNumber"]
        properties:
          email:
            type: "string"
            format: "email"
          password:
            type: "string"
            format: "password"
          name:
            type: "string"
          phoneNumber:
            type: "string"
      example:
        email: "customer@example.com"
        password: "SecurePass123!"
        name: "John Doe"
        phoneNumber: "+1234567890"
    responses:
      - status: 201
        description: "Customer registered successfully"
        example:
          success: true
          message: "Customer User"
          data:
            userId: "usr-123e4567-e89b"
            email: "customer@example.com"
            role: "CUSTOMER"
            activated: false
          timestamp: "2026-04-29T10:30:00Z"
      - status: 400
        description: "User already exists"
        example:
          success: false
          message: "User with this email already exists"
          errorCode: "USER_ALREADY_EXISTS"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "register-employee"
    method: "POST"
    urlPath: "/api/v2/auth/register/employee"
    summary: "Register employee"
    description: "Creates a new user with EMPLOYEE role"
    authenticated: false
    rateLimit: "SENSITIVE (10 requests per minute per IP)"
    tags:
      - "Registration"
      - "POST"
    parameters: []
    requestBody:
      contentType: "application/json"
      schema:
        type: "object"
        required: ["email", "password", "name", "phoneNumber"]
      example:
        email: "employee@example.com"
        password: "SecurePass123!"
        name: "Jane Smith"
        phoneNumber: "+1987654321"
    responses:
      - status: 201
        description: "Employee registered successfully"
        example:
          success: true
          message: "Employee User"
          data:
            userId: "usr-223e4567-e89b"
            email: "employee@example.com"
            role: "EMPLOYEE"
            activated: false
          timestamp: "2026-04-29T10:30:00Z"

  - id: "register-admin"
    method: "POST"
    urlPath: "/api/v2/auth/register/admin"
    summary: "Register admin"
    description: "Creates a new user with ADMIN role"
    authenticated: false
    rateLimit: "SENSITIVE (10 requests per minute per IP)"
    tags:
      - "Registration"
      - "POST"
    parameters: []
    requestBody:
      contentType: "application/json"
      schema:
        type: "object"
        required: ["email", "password", "name", "phoneNumber"]
      example:
        email: "admin@example.com"
        password: "AdminPass123!"
        name: "Admin User"
        phoneNumber: "+1123456789"
    responses:
      - status: 201
        description: "Admin registered successfully"
        example:
          success: true
          message: "Admin User"
          data:
            userId: "usr-323e4567-e89b"
            email: "admin@example.com"
            role: "ADMIN"
            activated: false
          timestamp: "2026-04-29T10:30:00Z"

  # ========== PASSWORD MANAGEMENT ENDPOINTS (/api/v2/auth/password) ==========

  - id: "forgot-password"
    method: "POST"
    urlPath: "/api/v2/auth/password/forgot"
    summary: "Forgot password"
    description: "Sends password reset email with token to user's email"
    authenticated: false
    rateLimit: "SENSITIVE (10 requests per minute per IP)"
    tags:
      - "Password"
      - "POST"
    parameters: []
    requestBody:
      contentType: "application/json"
      schema:
        type: "object"
        required: ["email"]
        properties:
          email:
            type: "string"
            format: "email"
      example:
        email: "user@example.com"
    responses:
      - status: 200
        description: "Password reset email sent"
        example:
          success: true
          message: "Password reset email sent successfully"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "validate-reset-token"
    method: "POST"
    urlPath: "/api/v2/auth/password/validate-token"
    summary: "Validate reset token"
    description: "Validates the password reset token without consuming it"
    authenticated: false
    rateLimit: "SENSITIVE (10 requests per minute per IP)"
    tags:
      - "Password"
      - "POST"
    parameters: []
    requestBody:
      contentType: "application/json"
      schema:
        type: "object"
        required: ["token"]
        properties:
          token:
            type: "string"
      example:
        token: "123456"
    responses:
      - status: 200
        description: "Token is valid"
        example:
          success: true
          message: "Reset token is valid"
          timestamp: "2026-04-29T10:30:00Z"
      - status: 400
        description: "Invalid or expired token"
        example:
          success: false
          message: "Invalid or expired reset token"
          errorCode: "INVALID_RESET_TOKEN"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "reset-password"
    method: "POST"
    urlPath: "/api/v2/auth/password/reset"
    summary: "Reset password"
    description: "Resets user password using the token received via email"
    authenticated: false
    rateLimit: "SENSITIVE (10 requests per minute per IP)"
    tags:
      - "Password"
      - "POST"
    parameters: []
    requestBody:
      contentType: "application/json"
      schema:
        type: "object"
        required: ["token", "newPassword"]
        properties:
          token:
            type: "string"
          newPassword:
            type: "string"
            format: "password"
      example:
        token: "123456"
        newPassword: "NewSecurePass123!"
    responses:
      - status: 200
        description: "Password reset successful"
        example:
          success: true
          message: "Password reset successfully"
          timestamp: "2026-04-29T10:30:00Z"
      - status: 400
        description: "Invalid or expired token"
        example:
          success: false
          message: "Invalid or expired reset token"
          errorCode: "INVALID_RESET_TOKEN"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "change-password"
    method: "PUT"
    urlPath: "/api/v2/auth/password/change"
    summary: "Change password"
    description: "Changes password for authenticated user (requires current password)"
    authenticated: true
    rateLimit: "SENSITIVE (10 requests per minute per user)"
    tags:
      - "Password"
      - "PUT"
    parameters: []
    requestBody:
      contentType: "application/json"
      schema:
        type: "object"
        required: ["currentPassword", "newPassword"]
        properties:
          currentPassword:
            type: "string"
            format: "password"
          newPassword:
            type: "string"
            format: "password"
      example:
        currentPassword: "OldPass123!"
        newPassword: "NewSecurePass123!"
    responses:
      - status: 200
        description: "Password changed successfully"
        example:
          success: true
          message: "Password changed successfully"
          timestamp: "2026-04-29T10:30:00Z"
      - status: 401
        description: "Invalid current password"
        example:
          success: false
          message: "Invalid current password"
          errorCode: "INVALID_CREDENTIALS"
          timestamp: "2026-04-29T10:30:00Z"

  # ========== TWO-FACTOR AUTH ENDPOINTS (/api/v1/auth/2fa) ==========

  - id: "enable-2fa"
    method: "POST"
    urlPath: "/api/v1/auth/2fa/{userId}/enable"
    summary: "Enable 2FA"
    description: "Enables two-factor authentication for a user (generates secret)"
    authenticated: true
    rateLimit: "SENSITIVE (10 requests per minute per user)"
    tags:
      - "Two-Factor Auth"
      - "POST"
    parameters:
      - name: "userId"
        in: "path"
        type: "string"
        required: true
        description: "User ID"
        example: "usr-123e4567-e89b"
    requestBody: null
    responses:
      - status: 200
        description: "2FA enabled"
        example:
          success: true
          message: "2FA enabled for user: usr-123e4567-e89b"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "disable-2fa"
    method: "POST"
    urlPath: "/api/v1/auth/2fa/{userId}/disable"
    summary: "Disable 2FA"
    description: "Disables two-factor authentication for a user"
    authenticated: true
    rateLimit: "SENSITIVE (10 requests per minute per user)"
    tags:
      - "Two-Factor Auth"
      - "POST"
    parameters:
      - name: "userId"
        in: "path"
        type: "string"
        required: true
        description: "User ID"
        example: "usr-123e4567-e89b"
    requestBody: null
    responses:
      - status: 200
        description: "2FA disabled"
        example:
          success: true
          message: "2FA disabled for user: usr-123e4567-e89b"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "send-2fa-code"
    method: "POST"
    urlPath: "/api/v1/auth/2fa/{userId}/send-code"
    summary: "Send 2FA code"
    description: "Sends a new 2FA code to the user via notification service"
    authenticated: true
    rateLimit: "SENSITIVE (10 requests per minute per user)"
    tags:
      - "Two-Factor Auth"
      - "POST"
    parameters:
      - name: "userId"
        in: "path"
        type: "string"
        required: true
        description: "User ID"
        example: "usr-123e4567-e89b"
    requestBody: null
    responses:
      - status: 200
        description: "Code sent"
        example:
          success: true
          message: "Validation code sent to user: usr-123e4567-e89b"
          timestamp: "2026-04-29T10:30:00Z"
  # cart-service
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
            success:
              type: "boolean"
            message:
              type: "string"
            data:
              type: "object"
            timestamp:
              type: "string"
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
                productId:
                  type: "string"
                quantity:
                  type: "integer"
                unitPrice:
                  type: "number"
                discountPerUnit:
                  type: "number"
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
  # employee-service

  # ========== COMMAND ENDPOINTS (/api/v2/employees) ==========

  - id: "create-employee"
    method: "POST"
    urlPath: "/api/v2/employees"
    summary: "Create employee"
    description: "Creates a new employee with personal info, role, type, and compensation"
    authenticated: true
    rateLimit: "SENSITIVE (10 requests per minute per user)"
    tags:
      - "Employee Command Operations"
      - "POST"
    parameters: []
    requestBody:
      contentType: "application/json"
      schema:
        type: "object"
        required: ["firstName", "lastName", "employeeNumber", "dateOfBirth", "contactInfo", "role", "employeeType", "status", "hireDate", "hourlyRate", "weeklyHours"]
        properties:
          firstName:
            type: "string"
            maxLength: 100
          lastName:
            type: "string"
            maxLength: 100
          employeeNumber:
            type: "string"
            maxLength: 20
          dateOfBirth:
            type: "string"
            format: "date"
          contactInfo:
            type: "object"
            properties:
              email:
                type: "string"
                format: "email"
              phoneNumber:
                type: "string"
              address:
                type: "object"
                properties:
                  street:
                    type: "string"
                  city:
                    type: "string"
                  state:
                    type: "string"
                  country:
                    type: "string"
                  postalCode:
                    type: "string"
          role:
            type: "string"
            enum: ["PHARMACIST", "PHARMACY_TECHNICIAN", "STORE_MANAGER", "ASSISTANT_MANAGER", "CASHIER", "INVENTORY_CLERK", "DELIVERY_DRIVER", "CUSTOMER_SERVICE_REP", "JANITOR"]
          employeeType:
            type: "string"
            enum: ["FULL_TIME", "PART_TIME", "CONTRACTOR", "INTERN", "SEASONAL"]
          status:
            type: "string"
            enum: ["ACTIVE", "INACTIVE", "ON_LEAVE", "SUSPENDED", "TERMINATED"]
          department:
            type: "string"
            maxLength: 100
          storeId:
            type: "string"
            maxLength: 50
          hireDate:
            type: "string"
            format: "date"
          hourlyRate:
            type: "number"
            format: "double"
          weeklyHours:
            type: "integer"
          workdaySchedule:
            type: "object"
            description: "JSONB column for flexible schedule"
      example:
        firstName: "John"
        lastName: "Doe"
        employeeNumber: "EMP-001"
        dateOfBirth: "1985-05-15"
        contactInfo:
          email: "john.doe@drugstore.com"
          phoneNumber: "+1234567890"
          address:
            street: "123 Main St"
            city: "New York"
            state: "NY"
            country: "US"
            postalCode: "10001"
        role: "PHARMACIST"
        employeeType: "FULL_TIME"
        status: "ACTIVE"
        department: "Pharmacy"
        storeId: "store-001"
        hireDate: "2024-01-15"
        hourlyRate: 45.50
        weeklyHours: 40
        workdaySchedule: {"monday": "9AM-5PM", "tuesday": "9AM-5PM"}
    responses:
      - status: 201
        description: "Employee created successfully"
        example:
          success: true
          message: "Employee created successfully"
          data: "123e4567-e89b-12d3-a456-426614174000"
          timestamp: "2026-04-29T10:30:00Z"
      - status: 400
        description: "Invalid input data"
        example:
          success: false
          message: "First name is required"
          errorCode: "VALIDATION_ERROR"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "update-employee"
    method: "PUT"
    urlPath: "/api/v2/employees/{id}"
    summary: "Update employee"
    description: "Updates an existing employee's information"
    authenticated: true
    rateLimit: "STANDARD (60 requests per minute per user)"
    tags:
      - "Employee Command Operations"
      - "PUT"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Employee ID (UUID)"
        example: "123e4567-e89b-12d3-a456-426614174000"
    requestBody:
      contentType: "application/json"
      schema:
        type: "object"
        required: ["firstName", "lastName", "contactInfo", "role", "employeeType", "status"]
      example:
        firstName: "John"
        lastName: "Doe"
        contactInfo:
          email: "john.doe@drugstore.com"
          phoneNumber: "+1234567890"
        role: "PHARMACIST"
        employeeType: "FULL_TIME"
        status: "ACTIVE"
    responses:
      - status: 200
        description: "Employee updated successfully"
        example:
          success: true
          message: "Employee updated successfully"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "add-certification"
    method: "POST"
    urlPath: "/api/v2/employees/{id}/certifications"
    summary: "Add certification"
    description: "Adds a certification to an employee (e.g., Pharmacy License, CPR Certification)"
    authenticated: true
    rateLimit: "STANDARD (60 requests per minute per user)"
    tags:
      - "Employee Command Operations"
      - "POST"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Employee ID (UUID)"
    requestBody:
      contentType: "application/json"
      schema:
        type: "object"
        required: ["name", "issuingOrganization", "issueDate"]
        properties:
          name:
            type: "string"
            maxLength: 200
          issuingOrganization:
            type: "string"
            maxLength: 200
          issueDate:
            type: "string"
            format: "date"
          expirationDate:
            type: "string"
            format: "date"
      example:
        name: "Pharmacy License"
        issuingOrganization: "State Board of Pharmacy"
        issueDate: "2024-01-15"
        expirationDate: "2026-01-15"
    responses:
      - status: 200
        description: "Certification added successfully"
        example:
          success: true
          message: "Certification added successfully"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "change-role"
    method: "PATCH"
    urlPath: "/api/v2/employees/{id}/role"
    summary: "Change employee role"
    description: "Changes the role of an employee (e.g., from CASHIER to STORE_MANAGER)"
    authenticated: true
    rateLimit: "SENSITIVE (10 requests per minute per user)"
    tags:
      - "Employee Command Operations"
      - "PATCH"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Employee ID (UUID)"
    requestBody:
      contentType: "application/json"
      schema:
        type: "object"
        required: ["role"]
        properties:
          role:
            type: "string"
            enum: ["PHARMACIST", "PHARMACY_TECHNICIAN", "STORE_MANAGER", "ASSISTANT_MANAGER", "CASHIER", "INVENTORY_CLERK", "DELIVERY_DRIVER", "CUSTOMER_SERVICE_REP", "JANITOR"]
      example:
        role: "STORE_MANAGER"
    responses:
      - status: 200
        description: "Role changed successfully"
        example:
          success: true
          message: "Employee role changed successfully"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "change-status"
    method: "PATCH"
    urlPath: "/api/v2/employees/{id}/status"
    summary: "Change employee status"
    description: "Changes employment status (ACTIVE → SUSPENDED → TERMINATED)"
    authenticated: true
    rateLimit: "STANDARD (60 requests per minute per user)"
    tags:
      - "Employee Command Operations"
      - "PATCH"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Employee ID (UUID)"
    requestBody:
      contentType: "application/json"
      schema:
        type: "object"
        required: ["status"]
        properties:
          status:
            type: "string"
            enum: ["ACTIVE", "INACTIVE", "ON_LEAVE", "SUSPENDED", "TERMINATED"]
      example:
        status: "SUSPENDED"
    responses:
      - status: 200
        description: "Status changed successfully"
        example:
          success: true
          message: "Employee status changed successfully"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "update-compensation"
    method: "PATCH"
    urlPath: "/api/v2/employees/{id}/compensation"
    summary: "Update compensation"
    description: "Updates employee's hourly rate and/or weekly hours"
    authenticated: true
    rateLimit: "SENSITIVE (10 requests per minute per user)"
    tags:
      - "Employee Command Operations"
      - "PATCH"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Employee ID (UUID)"
    requestBody:
      contentType: "application/json"
      schema:
        type: "object"
        properties:
          hourlyRate:
            type: "number"
            format: "double"
          weeklyHours:
            type: "integer"
      example:
        hourlyRate: 50.00
        weeklyHours: 40
    responses:
      - status: 200
        description: "Compensation updated successfully"
        example:
          success: true
          message: "Compensation updated successfully"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "suspend-employee"
    method: "PATCH"
    urlPath: "/api/v2/employees/{id}/suspend"
    summary: "Suspend employee"
    description: "Suspends an employee with a reason and who suspended them"
    authenticated: true
    rateLimit: "SENSITIVE (10 requests per minute per user)"
    tags:
      - "Employee Command Operations"
      - "PATCH"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Employee ID (UUID)"
      - name: "reason"
        in: "query"
        type: "string"
        required: true
        description: "Reason for suspension"
        example: "Policy violation"
      - name: "suspendedBy"
        in: "query"
        type: "string"
        required: true
        description: "User ID who suspended the employee"
        example: "usr-admin-001"
    requestBody: null
    responses:
      - status: 200
        description: "Employee suspended successfully"
        example:
          success: true
          message: "Employee suspended successfully"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "activate-employee"
    method: "PATCH"
    urlPath: "/api/v2/employees/{id}/activate"
    summary: "Activate employee"
    description: "Activates a previously suspended or inactive employee"
    authenticated: true
    rateLimit: "STANDARD (60 requests per minute per user)"
    tags:
      - "Employee Command Operations"
      - "PATCH"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Employee ID (UUID)"
      - name: "reason"
        in: "query"
        type: "string"
        required: true
        description: "Reason for activation"
        example: "Reinstated after review"
      - name: "activatedBy"
        in: "query"
        type: "string"
        required: true
        description: "User ID who activated the employee"
        example: "usr-admin-001"
    requestBody: null
    responses:
      - status: 200
        description: "Employee activated successfully"
        example:
          success: true
          message: "Employee activated successfully"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "put-on-leave"
    method: "PATCH"
    urlPath: "/api/v2/employees/{id}/on-leave"
    summary: "Put employee on leave"
    description: "Places an employee on leave (e.g., medical, personal)"
    authenticated: true
    rateLimit: "STANDARD (60 requests per minute per user)"
    tags:
      - "Employee Command Operations"
      - "PATCH"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Employee ID (UUID)"
      - name: "reason"
        in: "query"
        type: "string"
        required: true
        description: "Reason for leave"
        example: "Medical leave"
      - name: "approvedBy"
        in: "query"
        type: "string"
        required: true
        description: "User ID who approved the leave"
        example: "usr-manager-001"
    requestBody: null
    responses:
      - status: 200
        description: "Employee put on leave successfully"
        example:
          success: true
          message: "Employee put on leave successfully"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "delete-employee"
    method: "DELETE"
    urlPath: "/api/v2/employees/{id}"
    summary: "Delete employee"
    description: "Soft deletes an employee (sets deletedAt timestamp)"
    authenticated: true
    rateLimit: "SENSITIVE (10 requests per minute per user)"
    tags:
      - "Employee Command Operations"
      - "DELETE"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Employee ID (UUID)"
      - name: "deletedBy"
        in: "query"
        type: "string"
        required: true
        description: "User ID who deleted the employee"
        example: "usr-admin-001"
    requestBody: null
    responses:
      - status: 200
        description: "Employee deleted successfully"
        example:
          success: true
          message: "Employee deleted successfully"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "restore-employee"
    method: "PATCH"
    urlPath: "/api/v2/employees/{id}/restore"
    summary: "Restore employee"
    description: "Restores a previously deleted (soft-deleted) employee"
    authenticated: true
    rateLimit: "SENSITIVE (10 requests per minute per user)"
    tags:
      - "Employee Command Operations"
      - "PATCH"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Employee ID (UUID)"
      - name: "restoredBy"
        in: "query"
        type: "string"
        required: true
        description: "User ID who restored the employee"
        example: "usr-admin-001"
    requestBody: null
    responses:
      - status: 200
        description: "Employee restored successfully"
        example:
          success: true
          message: "Employee restored successfully"
          timestamp: "2026-04-29T10:30:00Z"

  # ========== QUERY ENDPOINTS (/api/v2/employees) ==========

  - id: "get-employee-by-id"
    method: "GET"
    urlPath: "/api/v2/employees/{id}"
    summary: "Get employee by ID"
    description: "Retrieves an employee by their unique ID"
    authenticated: true
    rateLimit: "PUBLIC (60 requests per minute per user)"
    tags:
      - "Employee Query Operations"
      - "GET"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Employee ID (UUID)"
        example: "123e4567-e89b-12d3-a456-426614174000"
    requestBody: null
    responses:
      - status: 200
        description: "Employee found"
        example:
          success: true
          message: "Employee found"
          data:
            id: "123e4567-e89b-12d3-a456-426614174000"
            employeeNumber: "EMP-001"
            firstName: "John"
            lastName: "Doe"
            role: "PHARMACIST"
            employeeType: "FULL_TIME"
            status: "ACTIVE"
            hireDate: "2024-01-15"
            hourlyRate: 45.50
            weeklyHours: 40
          timestamp: "2026-04-29T10:30:00Z"
      - status: 404
        description: "Employee not found"
        example:
          success: false
          message: "Employee not found with id: 999e4567-e89b-12d3-a456-426614174999"
          errorCode: "NOT_FOUND"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "get-employee-by-number"
    method: "GET"
    urlPath: "/api/v2/employees/by-number/{employeeNumber}"
    summary: "Get employee by employee number"
    description: "Retrieves an employee by their unique employee number"
    authenticated: true
    rateLimit: "PUBLIC (60 requests per minute per user)"
    tags:
      - "Employee Query Operations"
      - "GET"
    parameters:
      - name: "employeeNumber"
        in: "path"
        type: "string"
        required: true
        description: "Employee number (unique)"
        example: "EMP-001"
    requestBody: null
    responses:
      - status: 200
        description: "Employee found"
        example:
          success: true
          message: "Employee found"
          data:
            id: "123e4567-e89b-12d3-a456-426614174000"
            employeeNumber: "EMP-001"
            firstName: "John"
          timestamp: "2026-04-29T10:30:00Z"
      - status: 404
        description: "Employee not found"
        example:
          success: false
          message: "Employee not found with number: EMP-999"
          errorCode: "NOT_FOUND"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "search-employees"
    method: "GET"
    urlPath: "/api/v2/employees"
    summary: "Search employees"
    description: "Searches employees with filters and pagination"
    authenticated: true
    rateLimit: "PUBLIC (60 requests per minute per user)"
    tags:
      - "Employee Query Operations"
      - "GET"
    parameters:
      - name: "firstName"
        in: "query"
        type: "string"
        required: false
        description: "Filter by first name"
      - name: "lastName"
        in: "query"
        type: "string"
        required: false
        description: "Filter by last name"
      - name: "role"
        in: "query"
        type: "string"
        required: false
        description: "Filter by role"
      - name: "status"
        in: "query"
        type: "string"
        required: false
        description: "Filter by status"
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
          message: "Employees found"
          data:
            content:
              - id: "123e4567-e89b-12d3-a456-426614174000"
                firstName: "John"
                lastName: "Doe"
                role: "PHARMACIST"
            pageable:
              pageNumber: 0
              pageSize: 20
            totalElements: 50
            totalPages: 3
          timestamp: "2026-04-29T10:30:00Z"

  - id: "check-exists-by-id"
    method: "GET"
    urlPath: "/api/v2/employees/exists/id/{id}"
    summary: "Check if employee exists by ID"
    description: "Returns true/false if an employee exists with the given ID"
    authenticated: true
    rateLimit: "PUBLIC (60 requests per minute per user)"
    tags:
      - "Employee Query Operations"
      - "GET"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Employee ID to check"
        example: "123e4567-e89b-12d3-a456-426614174000"
    requestBody: null
    responses:
      - status: 200
        description: "Check completed"
        example:
          success: true
          message: "Employee with ID 123e4567-e89b-12d3-a456-426614174000 exists: true"
          data: true
          timestamp: "2026-04-29T10:30:00Z"

  - id: "check-exists-by-number"
    method: "GET"
    urlPath: "/api/v2/employees/exists/number/{employeeNumber}"
    summary: "Check if employee exists by number"
    description: "Returns true/false if an employee exists with the given employee number"
    authenticated: true
    rateLimit: "PUBLIC (60 requests per minute per user)"
    tags:
      - "Employee Query Operations"
      - "GET"
    parameters:
      - name: "employeeNumber"
        in: "path"
        type: "string"
        required: true
        description: "Employee number to check"
        example: "EMP-001"
    requestBody: null
    responses:
      - status: 200
        description: "Check completed"
        example:
          success: true
          message: "Employee with number EMP-001 exists: true"
          data: true
          timestamp: "2026-04-29T10:30:00Z"
  # inventory-service
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
  # notification-service
  - id: ""
    method: "GET"
    urlPath: ""
    summary: ""
    description: ""
    authenticated: false
    rateLimit: ""
    tags:
      - ""

    # ApiParameter[]
    parameters:
      - name: ""
        in: "query"
        type: "string"
        required: false
        description: ""
        example: ""

    # ApiRequestBody
    requestBody:
      contentType: "application/json"
      schema: {}
      example: {}

    # ApiResponse[]
    responses:
      - status: 200
        description: ""
        schema: {}
        example: ""
      - status: 400
        description: ""
        example: ""
      - status: 401
        description: ""
        example: ""
      - status: 500
        description: ""
        example: ""
  # order-service
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
  # payment-service
  - id: ""
    method: "GET"
    urlPath: ""
    summary: ""
    description: ""
    authenticated: false
    rateLimit: ""
    tags:
      - ""

    # ApiParameter[]
    parameters:
      - name: ""
        in: "query"
        type: "string"
        required: false
        description: ""
        example: ""

    # ApiRequestBody
    requestBody:
      contentType: "application/json"
      schema: {}
      example: {}

    # ApiResponse[]
    responses:
      - status: 200
        description: ""
        schema: {}
        example: ""
      - status: 400
        description: ""
        example: ""
      - status: 401
        description: ""
        example: ""
      - status: 500
        description: ""
        example: ""
  # product-service
  - id: "search-products"
    method: "GET"
    urlPath: "/api/v2/products"
    summary: "Search products"
    description: "Searches products by filters with pagination."
    authenticated: false
    rateLimit: "profile=public"
    tags:
      - "products"

    # ApiParameter[]
    parameters:
      - name: "name"
        in: "query"
        type: "string"
        required: false
        description: "Partial product name."
        example: "Paracetamol"
      - name: "category"
        in: "query"
        type: "string"
        required: false
        description: "ProductCategory enum value."
        example: "ANALGESICS"
      - name: "manufacturer"
        in: "query"
        type: "string"
        required: false
        description: "Manufacturer name."
        example: "Test Labs"
      - name: "page"
        in: "query"
        type: "integer"
        required: false
        description: "Page number (0-based)."
        example: "0"
      - name: "size"
        in: "query"
        type: "integer"
        required: false
        description: "Page size."
        example: "10"

    # ApiRequestBody
    requestBody:
      contentType: "application/json"
      schema: {}
      example: {}

    # ApiResponse[]
    responses:
      - status: 200
        description: "Page of product items."
        schema: {}
        example: "ResponseWrapper<PageResponse<ProductResponse>>"
      - status: 400
        description: "Invalid query params."
        example: "Bad request"
      - status: 401
        description: "Not used for this public endpoint."
        example: "N/A"
      - status: 500
        description: "Internal error"
        example: "N/A"

  - id: "get-product-by-id"
    method: "GET"
    urlPath: "/api/v2/products/{productId}"
    summary: "Get product by ID"
    description: "Returns one product by UUID-like id."
    authenticated: false
    rateLimit: "profile=public"
    tags:
      - "products"
    parameters:
      - name: "productId"
        in: "path"
        type: "string"
        required: true
        description: "Product identifier."
        example: "fccf6d4b-c266-4004-ada1-77e69787d22e"
    requestBody:
      contentType: "application/json"
      schema: {}
      example: {}
    responses:
      - status: 200
        description: "Product found."
        schema: {}
        example: "ResponseWrapper<ProductResponse>"
      - status: 404
        description: "Product not found."
        example: "ENTITY-404"
      - status: 500
        description: "Internal error"
        example: "N/A"

  - id: "get-product-by-sku"
    method: "GET"
    urlPath: "/api/v2/products/sku/{sku}"
    summary: "Get product by SKU"
    description: "Lookup product by normalized SKU."
    authenticated: false
    rateLimit: "profile=public"
    tags:
      - "products"
    parameters:
      - name: "sku"
        in: "path"
        type: "string"
        required: true
        description: "SKU code."
        example: "MED-T4578EAC1"
    requestBody:
      contentType: "application/json"
      schema: {}
      example: {}
    responses:
      - status: 200
        description: "Product found."
        schema: {}
        example: "ResponseWrapper<ProductResponse>"
      - status: 404
        description: "SKU not found."
        example: "ENTITY-404"
      - status: 500
        description: "Internal error"
        example: "N/A"

  - id: "get-product-by-barcode"
    method: "GET"
    urlPath: "/api/v2/products/barcode/{barcode}"
    summary: "Get product by barcode"
    description: "Lookup product by barcode."
    authenticated: false
    rateLimit: "profile=public"
    tags:
      - "products"
    parameters:
      - name: "barcode"
        in: "path"
        type: "string"
        required: true
        description: "Numeric barcode."
        example: "90000123456"
    requestBody:
      contentType: "application/json"
      schema: {}
      example: {}
    responses:
      - status: 200
        description: "Product found."
        schema: {}
        example: "ResponseWrapper<ProductResponse>"
      - status: 404
        description: "Barcode not found."
        example: "ENTITY-404"
      - status: 500
        description: "Internal error"
        example: "N/A"

  - id: "list-categories"
    method: "GET"
    urlPath: "/api/v2/products/categories"
    summary: "List product categories"
    description: "Returns all available category display names."
    authenticated: false
    rateLimit: "none"
    tags:
      - "products"
    parameters: []
    requestBody:
      contentType: "application/json"
      schema: {}
      example: {}
    responses:
      - status: 200
        description: "Categories list."
        schema: {}
        example: "ResponseWrapper<List<String>>"
      - status: 500
        description: "Internal error"
        example: "N/A"

  - id: "create-product"
    method: "POST"
    urlPath: "/api/v2/products"
    summary: "Create product"
    description: "Creates a new product aggregate."
    authenticated: true
    rateLimit: "profile=sensitive"
    tags:
      - "products"
    parameters: []
    requestBody:
      contentType: "application/json"
      schema:
        required: ["name", "category", "price"]
      example:
        sku: "MED-T4578EAC1"
        name: "Integration Test Product"
        category: "ANALGESICS"
        type: "MEDICATION"
        price: 29.90
        barcode: "90000123456"
    responses:
      - status: 201
        description: "Created"
        schema: {}
        example: "ResponseWrapper<ProductID>"
      - status: 401
        description: "Missing/invalid JWT"
        example: "UNAUTHORIZED"
      - status: 403
        description: "Role not allowed"
        example: "FORBIDDEN"
      - status: 422
        description: "Validation/domain error"
        example: "PRODUCT_VALIDATION_EXCEPTION"

  - id: "update-product"
    method: "PUT"
    urlPath: "/api/v2/products/{productId}"
    summary: "Update product"
    description: "Updates mutable fields for an existing product."
    authenticated: true
    rateLimit: "profile=sensitive"
    tags:
      - "products"
    parameters:
      - name: "productId"
        in: "path"
        type: "string"
        required: true
        description: "Product identifier."
        example: "fccf6d4b-c266-4004-ada1-77e69787d22e"
    requestBody:
      contentType: "application/json"
      schema: {}
      example:
        name: "Updated Product Name"
        price: 49.90
        description: "Updated description"
    responses:
      - status: 200
        description: "Updated"
        schema: {}
        example: "ResponseWrapper<Void>"
      - status: 401
        description: "Missing/invalid JWT"
        example: "N/A"
      - status: 403
        description: "Role not allowed"
        example: "N/A"
      - status: 404
        description: "Product not found"
        example: "N/A"

  - id: "delete-product"
    method: "DELETE"
    urlPath: "/api/v2/products/{productId}"
    summary: "Soft delete product"
    description: "Marks product as deleted."
    authenticated: true
    rateLimit: "profile=sensitive"
    tags:
      - "products"
    parameters:
      - name: "productId"
        in: "path"
        type: "string"
        required: true
        description: "Product identifier."
        example: "fccf6d4b-c266-4004-ada1-77e69787d22e"
    requestBody:
      contentType: "application/json"
      schema: {}
      example: {}
    responses:
      - status: 200
        description: "Deleted"
        schema: {}
        example: "ResponseWrapper<Void>"
      - status: 401
        description: "Missing/invalid JWT"
        example: "N/A"
      - status: 403
        description: "Role not allowed"
        example: "N/A"

  - id: "restore-product"
    method: "PATCH"
    urlPath: "/api/v2/products/{productId}/restore"
    summary: "Restore product"
    description: "Restores a previously soft-deleted product."
    authenticated: true
    rateLimit: "profile=sensitive"
    tags:
      - "products"
    parameters:
      - name: "productId"
        in: "path"
        type: "string"
        required: true
        description: "Product identifier."
        example: "fccf6d4b-c266-4004-ada1-77e69787d22e"
    requestBody:
      contentType: "application/json"
      schema: {}
      example: {}
    responses:
      - status: 200
        description: "Restored"
        schema: {}
        example: "ResponseWrapper<Void>"
      - status: 401
        description: "Missing/invalid JWT"
        example: "N/A"
      - status: 403
        description: "Role not allowed"
        example: "N/A"
  # store-service
  - id: "store-create"
    method: "POST"
    urlPath: "/api/v2/stores"
    summary: "Create a new store"
    description: "Requires ADMIN or MANAGER. Rate limit SENSITIVE. Returns wrapped Store ID. Bearer JWT HS256 expected."
    authenticated: true
    rateLimit: "SENSITIVE (e.g. 5 req / 300s dev YAML; docker: 7/60s overridable)"
    tags:
      - "Store Command Operations"
    parameters: []
    requestBody:
      contentType: "application/json"
      schema:
        type: object
        required: ["code", "name", "status", "contactInfo", "address", "schedule", "geolocation"]
      example:
        code: "STR-001"
        name: "Central Pharmacy"
        status: "ACTIVE"
        contactInfo:
          phone: "+1-555-0100"
          email: "central@example.com"
        address:
          country: "USA"
          state: "CA"
          city: "San Francisco"
          neighborhood: "Downtown"
          street: "Market St"
          number: "100"
          zipCode: "94103"
        schedule:
          mondayOpen: "09:00"
          mondayClose: "18:00"
        geolocation:
          latitude: -12.046374
          longitude: -77.042793
    responses:
      - status: 201
        description: "Created — ResponseWrapper with store id payload"
        schema: {}
        example: '{"success":true,"data":"uuid-here"}'
      - status: 400
        description: "Validation failure / business rule"
        example: '{"success":false,"message":"..."}'
      - status: 401
        description: "Missing or invalid JWT for mutating caller"
        example: '{"success":false,"message":"Unauthorized"}'
      - status: 403
        description: "Insufficient role"
        example: '{"success":false,"message":"Forbidden"}'
      - status: 500
        description: "Unexpected server error"
        example: '{"success":false,"message":"Internal error"}'

  - id: "store-update-location"
    method: "PUT"
    urlPath: "/api/v2/stores/{id}/location"
    summary: "Update store geolocation & address"
    description: "ADMIN/MANAGER. STANDARD rate limit."
    authenticated: true
    rateLimit: "STANDARD (e.g. 100/60s dev; docker 40/60s)"
    tags:
      - "Store Command Operations"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Store UUID"
        example: "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
    requestBody:
      contentType: "application/json"
      schema: { type: object }
      example:
        geolocation:
          latitude: -12.05
          longitude: -77.04
        address:
          country: "USA"
          state: "CA"
          city: "LA"
          neighborhood: "Arts District"
          street: "Main"
          number: "42"
          zipCode: "90013"
    responses:
      - status: 200
        description: "Success wrapper"
        schema: {}
        example: '{"success":true}'
      - status: 404
        description: "Store not found"
        example: ""
      - status: 401
        description: "Unauthorized"
        example: ""

  - id: "store-update-schedule"
    method: "PUT"
    urlPath: "/api/v2/stores/{id}/schedule"
    summary: "Update opening hours JSON-derived schedule"
    description: "ADMIN/MANAGER. STANDARD rate limit."
    authenticated: true
    rateLimit: "STANDARD"
    tags:
      - "Store Command Operations"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Store UUID"
        example: "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
    requestBody:
      contentType: "application/json"
      schema: { type: object }
      example: {}
    responses:
      - status: 200
        description: "Success with optional message in wrapper"
        schema: {}
        example: ""
      - status: 401
        description: "Unauthorized"
        example: ""

  - id: "store-maint"
    method: "PATCH"
    urlPath: "/api/v2/stores/{id}/under-maintenance"
    summary: "Set UNDER_MAINTENANCE status"
    description: "ADMIN/MANAGER. STANDARD rate limit."
    authenticated: true
    rateLimit: "STANDARD"
    tags:
      - "Store Command Operations"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Store UUID"
        example: "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
    requestBody:
      contentType: "application/json"
      schema: {}
      example: {}
    responses:
      - status: 200
        description: "Success"
        schema: {}
        example: ""

  - id: "store-temp-close"
    method: "PATCH"
    urlPath: "/api/v2/stores/{id}/temporary-closure"
    summary: "Set TEMPORARILY_CLOSED status"
    description: "ADMIN/MANAGER. **No @RateLimit on controller — gap.**"
    authenticated: true
    rateLimit: "none (missing annotation)"
    tags:
      - "Store Command Operations"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Store UUID"
        example: "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
    requestBody:
      contentType: "application/json"
      schema: {}
      example: {}
    responses:
      - status: 200
        description: "Success"
        schema: {}
        example: ""

  - id: "store-activate"
    method: "PATCH"
    urlPath: "/api/v2/stores/{id}/activate"
    summary: "Activate store"
    description: "ADMIN/MANAGER. STANDARD rate limit."
    authenticated: true
    rateLimit: "STANDARD"
    tags:
      - "Store Command Operations"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Store UUID"
        example: "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
    requestBody:
      contentType: "application/json"
      schema: {}
      example: {}
    responses:
      - status: 200
        description: "Success"
        schema: {}
        example: ""

  - id: "store-deactivate"
    method: "PATCH"
    urlPath: "/api/v2/stores/{id}/deactivate"
    summary: "Deactivate store"
    description: "ADMIN/MANAGER. **No @RateLimit — gap.**"
    authenticated: true
    rateLimit: "none (missing annotation)"
    tags:
      - "Store Command Operations"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Store UUID"
        example: "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
    requestBody:
      contentType: "application/json"
      schema: {}
      example: {}
    responses:
      - status: 200
        description: "Success"
        schema: {}
        example: ""

  - id: "store-delete"
    method: "DELETE"
    urlPath: "/api/v2/stores/{id}"
    summary: "Delete store"
    description: "ADMIN/MANAGER. SENSITIVE rate limit."
    authenticated: true
    rateLimit: "SENSITIVE"
    tags:
      - "Store Command Operations"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Store UUID"
        example: "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
    requestBody:
      contentType: "application/json"
      schema: {}
      example: {}
    responses:
      - status: 200
        description: "Success"
        schema: {}
        example: ""
      - status: 401
        description: "Unauthorized"
        example: ""

  - id: "store-get-id"
    method: "GET"
    urlPath: "/api/v2/stores/{id}"
    summary: "Get store by internal id"
    description: "**permitAll** — JWT optional. PUBLIC rate profile. OpenAPI shows bearer anyway."
    authenticated: false
    rateLimit: "PUBLIC"
    tags:
      - "Store Query Operations"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Store UUID"
        example: "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
    requestBody:
      contentType: "application/json"
      schema: {}
      example: {}
    responses:
      - status: 200
        description: "StoreResponse in ResponseWrapper"
        schema: {}
        example: '{"success":true,"data":{"id":"...","code":"STR-001",...}}'
      - status: 404
        description: "Not found"
        example: ""

  - id: "store-get-code"
    method: "GET"
    urlPath: "/api/v2/stores/by-code/{code}"
    summary: "Get store by business code"
    authenticated: false
    rateLimit: "PUBLIC"
    tags:
      - "Store Query Operations"
    parameters:
      - name: "code"
        in: "path"
        type: "string"
        required: true
        description: "Unique store code"
        example: "STR-001"
    requestBody:
      contentType: "application/json"
      schema: {}
      example: {}
    responses:
      - status: 200
        description: "Found"
        schema: {}
        example: ""
      - status: 404
        description: "Unknown code"
        example: ""

  - id: "store-search"
    method: "GET"
    urlPath: "/api/v2/stores"
    summary: "Search stores by criteria (model attribute binding)"
    description: "Query params map to SearchStoreRequest + nested filters + PageRequest.page/size."
    authenticated: false
    rateLimit: "PUBLIC"
    tags:
      - "Store Query Operations"
    parameters:
      - name: "nameLike"
        in: "query"
        type: "string"
        required: false
        description: "Partial name"
        example: "Pharma"
      - name: "exactCode"
        in: "query"
        type: "string"
        required: false
        description: "Exact business code filter"
        example: "STR-001"
      - name: "page"
        in: "query"
        type: "integer"
        required: false
        description: "Page number (see PageRequest)"
        example: "1"
      - name: "size"
        in: "query"
        type: "integer"
        required: false
        description: "Page size"
        example: "10"
    requestBody:
      contentType: "application/json"
      schema: {}
      example: {}
    responses:
      - status: 200
        description: "Page of StoreResponse"
        schema: {}
        example: ""

  - id: "store-by-status"
    method: "GET"
    urlPath: "/api/v2/stores/status/{status}"
    summary: "List stores filtered by enumerated status"
    authenticated: false
    rateLimit: "PUBLIC"
    tags:
      - "Store Query Operations"
    parameters:
      - name: "status"
        in: "path"
        type: "string"
        required: true
        description: "StoreStatus enum"
        example: "ACTIVE"
      - name: "page"
        in: "query"
        type: "integer"
        required: false
        description: "Pagination"
        example: "1"
      - name: "size"
        in: "query"
        type: "integer"
        required: false
        description: "Page size"
        example: "20"
    requestBody:
      contentType: "application/json"
      schema: {}
      example: {}
    responses:
      - status: 200
        description: "Paged stores"
        schema: {}
        example: ""

  - id: "actuator-health"
    method: "GET"
    urlPath: "/actuator/health"
    summary: "Liveness/readiness probes"
    description: "PermitAll; used by Docker healthcheck and Nginx internal checks."
    authenticated: false
    rateLimit: "n/a"
    tags:
      - "Actuator"
    parameters: []
    requestBody:
      contentType: "application/json"
      schema: {}
      example: {}
    responses:
      - status: 200
        description: "UP/DOWN components"
        schema: {}
        example: '{"status":"UP"}'

  - id: "openapi-json"
    method: "GET"
    urlPath: "/v3/api-docs"
    summary: "OpenAPI 3 document"
    description: "PermitAll when springdoc enabled (SWAGGER_ENABLED)."
    authenticated: false
    rateLimit: "n/a"
    tags:
      - "Documentation"
    parameters: []
    requestBody:
      contentType: "application/json"
      schema: {}
      example: {}
    responses:
      - status: 200
        description: "OpenAPI JSON"
        schema: {}
        example: ""
  # user-service
  - id: "get-user-by-id"
    method: "GET"
    urlPath: "/api/v2/users/{id}"
    summary: "Get user by UUID"
    description: "Returns single user envelope; authenticated caller required."
    authenticated: true
    rateLimit: "app.rate-limit.profiles.customer-read (see application.yml)"
    tags:
      - "User Query"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "User UUID"
        example: "550e8400-e29b-41d4-a716-446655440000"
    requestBody: null
    responses:
      - status: 200
        description: "ResponseWrapper<UserHTTPResponse>"
        schema: {}
        example: '{"success":true,"data":{"id":"...","email":"user@example.com"}}'
      - status: 401
        description: "Missing or invalid JWT"
        example: ""
      - status: 404
        description: "User not found (if handler maps)"
        example: ""

  - id: "get-user-by-email"
    method: "GET"
    urlPath: "/api/v2/users/by-email/{email}"
    summary: "Get user by email"
    description: "Path variable raw email encoding may need URL-encoding for '+' etc."
    authenticated: true
    rateLimit: "customer-read profile"
    tags:
      - "User Query"
    parameters:
      - name: "email"
        in: "path"
        type: "string"
        required: true
        description: "Email address"
        example: "john.doe@example.com"
    requestBody: null
    responses:
      - status: 200
        description: "User payload"
        schema: {}
        example: ""

  - id: "get-user-by-phone"
    method: "GET"
    urlPath: "/api/v2/users/by-phone/{phone}"
    summary: "Get user by phone"
    description: "Phone matches domain PhoneNumber VO rules (+country, etc.)."
    authenticated: true
    rateLimit: "customer-read profile"
    tags:
      - "User Query"
    parameters:
      - name: "phone"
        in: "path"
        type: "string"
        required: true
        description: "E.164 or configured format"
        example: "+15550010003"
    requestBody: null
    responses:
      - status: 200
        description: "User payload"
        schema: {}
        example: ""

  - id: "get-users-by-role"
    method: "GET"
    urlPath: "/api/v2/users/by-role/{role}"
    summary: "Paginated users filtered by role"
    description: "role ∈ {CUSTOMER, EMPLOYEE, ADMIN}; pagination via libs_kernel PageRequest model attributes."
    authenticated: true
    rateLimit: "customer-read profile"
    tags:
      - "User Query"
    parameters:
      - name: "role"
        in: "path"
        type: "string"
        required: true
        description: "UserRole enum"
        example: "ADMIN"
      - name: "page"
        in: "query"
        type: "integer"
        required: false
        description: "Page index (test uses page=1 — confirm convention)"
        example: "1"
      - name: "size"
        in: "query"
        type: "integer"
        required: false
        description: "Page size"
        example: "10"
    requestBody: null
    responses:
      - status: 200
        description: "PageResponse<UserHTTPResponse> inside ResponseWrapper"
        schema: {}
        example: ""

  - id: "get-users-by-status"
    method: "GET"
    urlPath: "/api/v2/users/by-status/{status}"
    summary: "Paginated users filtered by lifecycle status"
    description: "status ∈ {PENDING, ACTIVE, INACTIVE, SUSPENDED, DELETED}"
    authenticated: true
    rateLimit: "customer-read profile"
    tags:
      - "User Query"
    parameters:
      - name: "status"
        in: "path"
        type: "string"
        required: true
        description: "UserStatus"
        example: "PENDING"
      - name: "page"
        in: "query"
        type: "integer"
        required: false
        description: "Page index"
        example: "1"
      - name: "size"
        in: "query"
        type: "integer"
        required: false
        description: "Page size"
        example: "10"
    requestBody: null
    responses:
      - status: 200
        description: "Paged list"
        schema: {}
        example: ""

  - id: "manager-create-user"
    method: "POST"
    urlPath: "/api/v2/users/manager/"
    summary: "Create customer user (staff)"
    description: "**Privileged** — ADMIN or MANAGER JWT; password validated via UserRequest constraints."
    authenticated: true
    rateLimit: "admin profile (low ceiling)"
    tags:
      - "User Management"
    parameters: []
    requestBody:
      contentType: "application/json"
      schema:
        type: "object"
        properties:
          email:
            type: "string"
          password:
            type: "string"
      example:
        email: "new.user@example.com"
        password: "SecureP@ss123"
    responses:
      - status: 201
        description: "Wrapped command result payload"
        schema: {}
        example: ""
      - status: 403
        description: "Insufficient role"
        example: ""
      - status: 422
        description: "Validation failure (integration test asserts this code for bad email/password)"
        example: ""

  - id: "manager-ban"
    method: "PATCH"
    urlPath: "/api/v2/users/manager/{id}/ban"
    summary: "Ban user"
    description: "Dispatches UpdateUserStatusCommand.ban"
    authenticated: true
    rateLimit: "admin profile"
    tags:
      - "User Management"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "User UUID"
        example: "550e8400-e29b-41d4-a716-446655440000"
    requestBody: null
    responses:
      - status: 200
        description: "Success wrapper"
        example: ""

  - id: "manager-unban"
    method: "PATCH"
    urlPath: "/api/v2/users/manager/{id}/unban"
    summary: "Unban user"
    authenticated: true
    rateLimit: "admin profile"
    tags:
      - "User Management"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "User UUID"
        example: "550e8400-e29b-41d4-a716-446655440000"
    requestBody: null
    responses:
      - status: 200
        description: "Success wrapper"
        example: ""

  - id: "manager-activate"
    method: "PATCH"
    urlPath: "/api/v2/users/manager/{id}/activate/code/{activationCode}"
    summary: "Activate pending user using code"
    description: "Combines activation code path param with UpdateUserStatusCommand.activate"
    authenticated: true
    rateLimit: "sensitive profile"
    tags:
      - "User Management"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "User UUID"
        example: ""
      - name: "activationCode"
        in: "path"
        type: "string"
        required: true
        description: "Token / code"
        example: "123456"
    requestBody: null
    responses:
      - status: 200
        description: "Success"
        example: ""

  - id: "manager-delete"
    method: "DELETE"
    urlPath: "/api/v2/users/manager/{id}"
    summary: "Delete user"
    description: "**Destructive** — CommandBus DeleteUserCommand; verify soft-delete expectations in domain."
    authenticated: true
    rateLimit: "admin profile"
    tags:
      - "User Management"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "User UUID"
        example: ""
    requestBody: null
    responses:
      - status: 200
        description: "Success wrapper"
        example: ""

  - id: "profile-me"
    method: "GET"
    urlPath: "/api/v2/users/profile/me"
    summary: "Current JWT subject profile"
    description: "@RequestAttribute userId populated by JWT filter pipeline."
    authenticated: true
    rateLimit: "@RateLimit (libs_kernel) — 100 RPM mentioned in controller Javadoc"
    tags:
      - "Profile Management"
    parameters: []
    requestBody: null
    responses:
      - status: 200
        description: "ProfileResponse"
        schema: {}
        example: ""

  - id: "profile-patch"
    method: "PATCH"
    urlPath: "/api/v2/users/profile"
    summary: "Update profile fields"
    description: "Partial update JSON → ProfileUpdateCommand"
    authenticated: true
    rateLimit: "@RateLimit"
    tags:
      - "Profile Management"
    parameters: []
    requestBody:
      contentType: "application/json"
      schema:
        type: "object"
        additionalProperties: true
      example:
        firstName: "Alice"
        lastName: "Smith"
        bio: "Updated bio"
    responses:
      - status: 200
        description: "Updated ProfileResponse"
        schema: {}
        example: ""

  - id: "actuator-health"
    method: "GET"
    urlPath: "/actuator/health"
    summary: "Liveness/readiness probes"
    description: "**Warning (dev YAML):** full actuator exposure incl. env/detail — lock down externally."
    authenticated: false
    rateLimit: "none"
    tags:
      - "Operations"
    parameters: []
    requestBody: null
    responses:
      - status: 200
        description: "Health composite"
        example: ""

---

# API Schema

> Auto-generated by `scripts/merge_service_sources.py`. Edit service-level `docs/project/source/*.md` files, then regenerate.

<!-- BEGIN address-service -->
<!-- Source: address-service/docs/project/source/APISchema.md -->
# API Schema

> Complete REST API with 10 endpoints (6 user endpoints + 7 admin endpoints, with some overlap). All endpoints require JWT authentication. Rate limiting is implemented via Redis with two profiles: STANDARD (60/min) for reads, SENSITIVE (10/min) for writes. Multi-country postal code validation supported (US, MX, CA, ES, UK). ResponseWrapper pattern used for consistent API responses. Potential improvements: add OpenAPI specification export, implement HATEOAS links, add bulk operations endpoint, and integrate with Kafka for address change events.

<!-- END address-service -->

<!-- BEGIN auth-service -->
<!-- Source: auth-service/docs/project/source/APISchema.md -->
# API Schema

> 15 REST endpoints documented across 5 controllers. All endpoints use ResponseWrapper pattern. Rate limiting: SENSITIVE (10/min) for auth operations, STANDARD (60/min) for token refresh. JWT tokens (access + refresh) with configurable expiration. **Integration tests** (`AuthEndpointsIntegrationTest`, profiles `integration-test` + `test`) hit these routes with Testcontainers Redis/Kafka and a gRPC UserService stub. Gaps: OpenAPI detail vs. some other services, OAuth2 browser flow docs. Potential: bulk operations, public OpenAPI hardening.

<!-- END auth-service -->

<!-- BEGIN cart-service -->
<!-- Source: cart-service/docs/project/source/APISchema.md -->
# API Schema

> 7 REST endpoints (4 user + 3 admin) and 2 gRPC methods documented. PLACEHOLDER issues: No @RateLimit annotations on any REST controllers (unlike address-service), CartPurchasedEvent defined but not published to Kafka. The service uses @PreAuthorize("hasRole('ADMIN')") on CartManagerController. Potential: Add OpenAPI annotations to all endpoints, implement rate limiting, publish cart events to Kafka.

<!-- END cart-service -->

<!-- BEGIN employee-service -->
<!-- Source: employee-service/docs/project/source/APISchema.md -->
# API Schema

> 16 REST endpoints documented (10 command + 6 query) with full request/response examples. Properly uses @RateLimit from libs-kernel with 3 profiles (PUBLIC, STANDARD, SENSITIVE). PLACEHOLDER issues: No Dockerfile/docker-compose.yml found, no Kafka event publishing yet. Potential improvements: Add Kafka events (employee.created/updated/deleted, employee.status-changed), implement caching for employee lookups, add Kubernetes manifests, set up CI/CD pipeline.

<!-- END employee-service -->

<!-- BEGIN inventory-service -->
<!-- Source: inventory-service/docs/project/source/APISchema.md -->
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

<!-- END inventory-service -->

<!-- BEGIN notification-service -->
<!-- Source: notification-service/docs/project/source/APISchema.md -->
# API Schema

> Notes goes here....

<!-- END notification-service -->

<!-- BEGIN order-service -->
<!-- Source: order-service/docs/project/source/APISchema.md -->
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

<!-- END order-service -->

<!-- BEGIN payment-service -->
<!-- Source: payment-service/docs/project/source/APISchema.md -->
# API Schema

> Notes goes here....

<!-- END payment-service -->

<!-- BEGIN product-service -->
<!-- Source: product-service/docs/project/source/APISchema.md -->
# API Schema

## Notes

- Public read endpoints expose full product payloads; confirm no sensitive attributes are included.
- No API version deprecation policy is documented yet for `/api/v2/products`.

<!-- END product-service -->

<!-- BEGIN store-service -->
<!-- Source: store-service/docs/project/source/APISchema.md -->
# API Schema

Base URL (local compose via Nginx): **`https://localhost/`** (self-signed / generated certs) or direct **`http://localhost:${STORE_SERVICE_HOST_PORT:-8080}`** to the app.

> [!warning] Spec vs security  
> Class-level `@SecurityRequirement(bearerAuth)` suggests all operations need JWT, but **GET store routes are permitAll** in `SecurityConfig`. Update Springdoc grouped security requirements or enforce auth consistently.

> [!tip] Try-it-out  
> Docker profile sets `try-it-out-enabled: false` in `application-docker.yml` — enable only in trusted environments.

<!-- END store-service -->

<!-- BEGIN user-service -->
<!-- Source: user-service/docs/project/source/APISchema.md -->
# API Schema

## gRPC (contract only — not exposed)

Protobuf service `UserService` includes **IsEmailUnique**, **ValidateUserCredentials**, **CreateUser**, **GetUserById**, 2FA hooks, etc. **No listener** binds these RPCs today; callers must fall back to REST or wait for server bootstrap.

## OpenAPI caveat

Springdoc **`packages-to-scan`** currently targets **`io.github.alexisTrejo11.drugstore.stores`**, while controllers live under **`...drugstore.users`**. Expect **missing operations** in generated OpenAPI until configuration is corrected.

## Authentication

Provide header `Authorization: Bearer <JWT>`. Roles `ADMIN`, `MANAGER`, `CUSTOMER` (and EMPLOYEE in schema) participate in matchers; principals must encode `ROLE_*` authorities consistently with `JwtAuthenticationFilter`.

<!-- END user-service -->
