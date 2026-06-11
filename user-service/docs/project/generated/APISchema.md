# API Schema

**Protocol:** Primarily **REST** under `/api/**`. **`UserService` gRPC** exists as **Protobuf + Java service class** without a deployed listener (**documented behaviour as of codebase scan — do not expose until wired**).

Authentication: **`Authorization: Bearer <JWT>`** unless endpoint is deliberately public (`/actuator/**`, `/swagger-ui/**`, `/v3/api-docs/**`, etc., per security config evolution).

---

## REST endpoints (canonical)

### User queries — base path `/api/v2/users`

| Method | Path | Auth | Summary |
|--------|------|------|---------|
| GET | `/{id}` | Bearer | Lookup by UUID |
| GET | `/by-email/{email}` | Bearer | Lookup by email (URL-encoding may be required) |
| GET | `/by-phone/{phone}` | Bearer | Lookup by telephone VO |
| GET | `/by-role/{role}` | Bearer | Paged listing by `CUSTOMER \| EMPLOYEE \| ADMIN` |
| GET | `/by-status/{status}` | Bearer | Paged listing by lifecycle status |

**Query params (`by-role`, `by-status`):** Typical pagination sort keys via `libs_kernel.page.PageRequest` (`page`, `size`, sorting fields). Align client contracts with VO implementation (**off-by-one** risk if UI assumes Spring default `page=0` but handlers/tests pass `page=1`).

### Manager — base `/api/v2/users/manager`

| Method | Path | Roles | Summary |
|--------|------|-------|---------|
| POST | `/` | ADMIN, MANAGER | Create **`CUSTOMER`** + credentials |
| PATCH | `/{id}/ban` | ADMIN, MANAGER | Ban flow |
| PATCH | `/{id}/unban` | ADMIN, MANAGER | Unban flow |
| PATCH | `/{id}/activate/code/{activationCode}` | ADMIN, MANAGER | Activation with code segment |
| DELETE | `/{id}` | ADMIN, MANAGER | Delete user record |

Typical statuses: **`201`** on create success; **`403`** forbidden for CUSTOMER callers; **`422`** surfaced in tests for malformed body.

### Profile — base `/api/v2/users/profile`

| Method | Path | Summary |
|--------|------|---------|
| GET | `/me` | Self profile projection |
| PATCH | `/` | Partial update |

Both require authenticated principal; annotated with libs_kernel **`@RateLimit`** (consult controller Javadoc for claimed throughput ceilings).

---

## Operational / docs endpoints

| Path | Auth | Detail |
|------|------|--------|
| `/actuator/**` | **often public in dev YAML** — **risk** | Harden prod profiles (`health`/`prometheus` only, etc.). |
| `/v3/api-docs`, `/swagger-ui.html` | public in ignores | Broken catalog risk until **`packages-to-scan`** fixed to **`io.github.alexisTrejo11.drugstore.users`**. |

---

## gRPC (contract reference — placeholders for tooling)

Fully declared in **`src/main/proto/user_service.proto`** (package `com.microservices.grpc.user` post-generation):

| RPC (sample) | Request | Response | Runtime |
|--------------|---------|----------|---------|
| `IsEmailUnique` | Email | Bool | **not exposed** |
| `ValidateUserCredentials` | Email + password plaintext | credential verdict | **not exposed** |
| `CreateUser` | CreateUserRequest (+ hashed_password field semantics) | UserResponse | **not exposed** |

**Security placeholder:** Intended inter-service networking should eventually specify **mTLS** or bearer metadata — **`GRPC_SECURITY_ENABLED` defaults false** in config commentary.

---

## Rate limiting vocabulary (conceptual buckets)

YAML keys under `app.rate-limit.profiles` include **`standard`, `sensitive`, `public`, `admin`, `customer-read`, `customer-write`**; concrete binding to endpoints lives in libs_kernel decorators — docs list names for architects only.
