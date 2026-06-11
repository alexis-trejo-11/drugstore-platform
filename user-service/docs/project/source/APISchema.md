---
# APISchema
type: "REST"

# ApiEndpoint[]
httpEndpoints:
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

grpcEndpointsPlaceholder:
  package: "com.microservices.grpc.user.UserService"
  note: "Proto defines IsEmailUnique, ValidateUserCredentials, CreateUser, etc. Server not wired in JVM — documented for contract consumers only."

---

# API Schema

## gRPC (contract only — not exposed)

Protobuf service `UserService` includes **IsEmailUnique**, **ValidateUserCredentials**, **CreateUser**, **GetUserById**, 2FA hooks, etc. **No listener** binds these RPCs today; callers must fall back to REST or wait for server bootstrap.

## OpenAPI caveat

Springdoc **`packages-to-scan`** currently targets **`io.github.alexisTrejo11.drugstore.stores`**, while controllers live under **`...drugstore.users`**. Expect **missing operations** in generated OpenAPI until configuration is corrected.

## Authentication

Provide header `Authorization: Bearer <JWT>`. Roles `ADMIN`, `MANAGER`, `CUSTOMER` (and EMPLOYEE in schema) participate in matchers; principals must encode `ROLE_*` authorities consistently with `JwtAuthenticationFilter`.
