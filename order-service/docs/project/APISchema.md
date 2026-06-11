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
