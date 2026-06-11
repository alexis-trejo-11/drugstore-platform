---
# APISchema
type: "REST"

# ApiEndpoint[]
httpEndpoints:
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
          emailOrPhoneNumber: type: "string"
          password: type: "string"
      example:
        emailOrPhoneNumber: "user@example.com"
        password: "SecurePass123!"
    responses:
      - status: 200
        description: "Login successful"
        schema:
          type: "object"
          properties:
            success: type: "boolean"
            message: type: "string"
            data: type: "object"
            timestamp: type: "string"
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
          email: type: "string"
          code: type: "string"
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
          email: type: "string" format: "email"
          password: type: "string" format: "password"
          name: type: "string"
          phoneNumber: type: "string"
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
          email: type: "string" format: "email"
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
          token: type: "string"
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
          token: type: "string"
          newPassword: type: "string" format: "password"
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
          currentPassword: type: "string" format: "password"
          newPassword: type: "string" format: "password"
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
---
# API Schema

> 15 REST endpoints documented across 5 controllers. All endpoints use ResponseWrapper pattern. Rate limiting: SENSITIVE (10/min) for auth operations, STANDARD (60/min) for token refresh. JWT tokens (access + refresh) with configurable expiration. **Integration tests** (`AuthEndpointsIntegrationTest`, profiles `integration-test` + `test`) hit these routes with Testcontainers Redis/Kafka and a gRPC UserService stub. Gaps: OpenAPI detail vs. some other services, OAuth2 browser flow docs. Potential: bulk operations, public OpenAPI hardening.
