# API Schema

## API Type
REST

## Base Endpoint Groups
- **User endpoints:** `/api/v2/user/addresses`
- **Admin endpoints:** `/api/v2/addresses/admin`

## Security and Policies
- All endpoints require JWT bearer authentication.
- Rate limiting:
  - **STANDARD** for read-focused endpoints
  - **SENSITIVE** for write-focused endpoints
- Unified response style via `ResponseWrapper`.

## User Endpoints
- `GET /api/v2/user/addresses` - list my active addresses
- `GET /api/v2/user/addresses/{addressId}` - get one address by ID
- `POST /api/v2/user/addresses` - create address
- `PUT /api/v2/user/addresses/{addressId}` - update address
- `DELETE /api/v2/user/addresses/{addressId}` - soft delete address
- `PUT /api/v2/user/addresses/{addressId}/set-default` - set default address

## Admin Endpoints
- `GET /api/v2/addresses/admin` - list all addresses (paginated)
- `GET /api/v2/addresses/admin/{id}` - get address by ID
- `GET /api/v2/addresses/admin/user/{userId}` - list addresses for a user
- `POST /api/v2/addresses/admin?userId=...` - create address for user
- `PUT /api/v2/addresses/admin/{id}` - update address
- `DELETE /api/v2/addresses/admin/{id}` - soft delete address
- `PUT /api/v2/addresses/admin/{id}/set-default-for-user/{userId}` - set default for user

## Request/Response Notes
- Request bodies include address fields such as `street`, `city`, `state`, `country`, `postalCode`, and optional metadata.
- Common responses:
  - `200` / `201` for successful operations
  - `400` for validation input issues
  - `401` for missing/invalid auth
  - `403` for authorization or business-rule restrictions
  - `404` for missing resources
  - `422` for domain-level invalid state (e.g. postal code format)

## Domain Rules Exposed by API
- Multi-country postal validation (US, MX, CA, ES, UK).
- Role-based max address limits.
- Single default address per user.
- Soft delete behavior (inactive records instead of hard delete).
