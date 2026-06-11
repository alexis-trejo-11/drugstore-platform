# API schema

REST API for **auth-service**. Twin of `docs/project/source/APISchema.md`: that file carries structured YAML for tooling; this file is human-readable tables and notes.

---

## Global

| Item | Value |
|------|--------|
| **Type** | REST |
| **Base paths** | `/api/v2/auth`, `/api/v2/auth/password`, `/api/v1/auth/2fa` |
| **Response envelope** | `libs_kernel.response.ResponseWrapper` |
| **Auth model** | JWT access + refresh; refresh in path for some routes |
| **Integration tests** | `AuthEndpointsIntegrationTest` (profiles `integration-test` + `test`) |

### Rate limiting

| Profile | Typical limit | Used for |
|---------|---------------|----------|
| **SENSITIVE** | 10 req/min per IP (or per user when authenticated) | Login, register, password, logout, 2FA config |
| **STANDARD** | 60 req/min per IP | Refresh session |

---

## Authentication (`/api/v2/auth`)

| Method | Path | Summary | Auth | Rate limit |
|--------|------|---------|------|------------|
| POST | `/api/v2/auth/login` | Login with email/phone + password; returns access + refresh JWTs | No | SENSITIVE |
| POST | `/api/v2/auth/login/2fa` | Second step 2FA with email + TOTP code | No | SENSITIVE |
| PATCH | `/api/v2/auth/refresh-session/{refreshToken}` | New access token from refresh token | No | STANDARD |
| POST | `/api/v2/auth/logout/{refreshToken}` | Invalidate single session | No | SENSITIVE |
| POST | `/api/v2/auth/logout-all` | Invalidate all sessions for user | Yes | SENSITIVE |

### Login request

```json
{
  "emailOrPhoneNumber": "user@example.com",
  "password": "SecurePass123!"
}
```

### Login success (200)

```json
{
  "success": true,
  "message": "Login successfully processed",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": "usr-123e4567-e89b",
    "role": "CUSTOMER",
    "expiresIn": 900000
  },
  "timestamp": "2026-04-29T10:30:00Z"
}
```

**Notable errors:** `401` invalid credentials; `423` two-factor required (`TWO_FACTOR_REQUIRED`).

---

## Registration

| Method | Path | Summary | Role assigned | Rate limit |
|--------|------|---------|---------------|------------|
| POST | `/api/v2/auth/register/customer` | Register customer | CUSTOMER | SENSITIVE |
| POST | `/api/v2/auth/register/employee` | Register employee | EMPLOYEE | SENSITIVE |
| POST | `/api/v2/auth/register/admin` | Register admin | ADMIN | SENSITIVE |

**Body (all roles):** `email`, `password`, `name`, `phoneNumber` (required).

**Success (201):** `userId`, `email`, `role`, `activated: false` — account activation via email token still required.

**Error (400):** `USER_ALREADY_EXISTS` when email is taken.

---

## Password management (`/api/v2/auth/password`)

| Method | Path | Summary | Auth | Rate limit |
|--------|------|---------|------|------------|
| POST | `/api/v2/auth/password/forgot` | Send reset email with token | No | SENSITIVE |
| POST | `/api/v2/auth/password/validate-token` | Validate reset token without consuming | No | SENSITIVE |
| POST | `/api/v2/auth/password/reset` | Reset password with token + `newPassword` | No | SENSITIVE |
| PUT | `/api/v2/auth/password/change` | Change password (`currentPassword`, `newPassword`) | Yes | SENSITIVE |

**Forgot body:** `{ "email": "user@example.com" }`

**Reset body:** `{ "token": "123456", "newPassword": "NewSecurePass123!" }`

**Errors:** `INVALID_RESET_TOKEN`, `INVALID_CREDENTIALS` on change.

---

## Two-factor auth (`/api/v1/auth/2fa`)

| Method | Path | Summary | Auth | Rate limit |
|--------|------|---------|------|------------|
| POST | `/api/v1/auth/2fa/{userId}/enable` | Enable 2FA for user | Yes | SENSITIVE |
| POST | `/api/v1/auth/2fa/{userId}/disable` | Disable 2FA | Yes | SENSITIVE |
| POST | `/api/v1/auth/2fa/{userId}/send-code` | Send validation code via notification service | Yes | SENSITIVE |

> **Note:** 2FA routes use API **v1** prefix while most auth routes use **v2**.

---

## Infrastructure

| Method | Path | Notes |
|--------|------|--------|
| GET | `/actuator/health` | Health check (HTTPS on 8443 internally) |
| GET | `/actuator/prometheus` | Metrics when enabled |

OAuth2 browser login flows are handled by Spring Security (not fully tabulated in source schema).

---

## Example cURL

```bash
# Login
curl -sS -X POST "https://localhost/api/v2/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"emailOrPhoneNumber":"user@example.com","password":"SecurePass123!"}'

# Refresh access token
curl -sS -X PATCH "https://localhost/api/v2/auth/refresh-session/<refreshToken>"

# Register customer
curl -sS -X POST "https://localhost/api/v2/auth/register/customer" \
  -H "Content-Type: application/json" \
  -d '{"email":"customer@example.com","password":"SecurePass123!","name":"John Doe","phoneNumber":"+1234567890"}'
```

*(Replace host with Nginx `:443` in Docker Compose; auth-service `:8443` is internal only.)*

---

## Gaps & improvements

- OpenAPI annotations are lighter than some sibling services — enrich Swagger for OAuth2 callbacks.
- **15** REST endpoints documented across 5 controllers; bulk operations not defined.
- Integration tests cover major flows; expand public OpenAPI hardening for production.
