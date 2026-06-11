---
# APISchema
type: "REST"

# ApiEndpoint[]
httpEndpoints:
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
---

# API Schema

Base URL (local compose via Nginx): **`https://localhost/`** (self-signed / generated certs) or direct **`http://localhost:${STORE_SERVICE_HOST_PORT:-8080}`** to the app.

> [!warning] Spec vs security  
> Class-level `@SecurityRequirement(bearerAuth)` suggests all operations need JWT, but **GET store routes are permitAll** in `SecurityConfig`. Update Springdoc grouped security requirements or enforce auth consistently.

> [!tip] Try-it-out  
> Docker profile sets `try-it-out-enabled: false` in `application-docker.yml` — enable only in trusted environments.
