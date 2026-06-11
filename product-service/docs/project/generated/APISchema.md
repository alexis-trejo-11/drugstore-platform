# API Schema

Type: REST

Base path: `/api/v2/products`

## Endpoints

### Public read endpoints

- `GET /api/v2/products` - Search products with query filters (`name`, `category`, `manufacturer`, `requiresPrescription`, `onlyAvailable`, `page`, `size`).
- `GET /api/v2/products/categories` - List all category display names.
- `GET /api/v2/products/{productId}` - Get product by ID.
- `GET /api/v2/products/sku/{sku}` - Get product by SKU.
- `GET /api/v2/products/barcode/{barcode}` - Get product by barcode.

### Protected write endpoints (JWT required, roles `ADMIN` or `MANAGER`)

- `POST /api/v2/products` - Create product (`201 Created`).
- `PUT /api/v2/products/{productId}` - Update product (`200 OK`).
- `DELETE /api/v2/products/{productId}` - Soft delete product (`200 OK`).
- `PATCH /api/v2/products/{productId}/restore` - Restore soft-deleted product (`200 OK`).

## Common status behavior

- `200 OK` for successful reads/updates/deletes/restores.
- `201 Created` for successful create.
- `401 Unauthorized` for missing/invalid JWT on protected routes.
- `403 Forbidden` for valid JWT without required role.
- `404 Not Found` when entity lookup fails.
- `422 Unprocessable Entity` for domain validation errors.

## Request Body Highlights

- Create requires at least: `name`, `category`, `price`.
- `barcode` is validated as digits-only by domain rules.
- Update accepts partial mutable fields.

## Notes

- Public read payload exposure should be reviewed periodically to avoid accidental leakage of internal-only fields.
- API deprecation/versioning policy beyond `/api/v2` is not documented yet.