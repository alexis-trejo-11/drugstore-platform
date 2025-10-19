# Store Service - Swagger API Documentation

## 📋 Overview

This document explains how to access and use the Swagger/OpenAPI documentation for the Store Service API.

## 🚀 Accessing Swagger UI

Once the application is running, you can access the interactive API documentation at:

```
http://localhost:8080/swagger-ui/index.html
```

Alternative URLs:
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`
- **OpenAPI YAML**: `http://localhost:8080/v3/api-docs.yaml`

## 🔐 Authentication

### JWT Bearer Token Authentication

All endpoints in this API require JWT authentication with **ADMIN role**.

### How to Authenticate in Swagger UI:

1. **Obtain a JWT Token**:
   - First, get a valid JWT token from your authentication service
   - The token should contain the ADMIN role claim

2. **Authorize in Swagger UI**:
   - Click the **Authorize** button (🔓 icon) at the top-right of the Swagger UI page
   - In the dialog that appears, enter your token in this format:
     ```
     Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
     ```
   - Click **Authorize** to apply
   - Click **Close**

3. **Make Authenticated Requests**:
   - All subsequent requests will automatically include the Authorization header
   - You can now test any endpoint using the "Try it out" button

### Token Format

```
Authorization: Bearer <your-jwt-token>
```

**Important Notes**:
- ⚠️ Keep your tokens secure and never share them
- ✅ Tokens expire after a configured time period
- 🔒 Use HTTPS in production environments
- 👤 Ensure your token has the required ADMIN role

## 📚 API Documentation Structure

### Store Command Operations
Endpoints for creating, updating, and managing store entities:
- **POST** `/api/v2/stores` - Create a new store
- **PUT** `/api/v2/stores/{id}/location` - Update store location
- **PUT** `/api/v2/stores/{id}/schedule` - Update store schedule
- **PATCH** `/api/v2/stores/{id}/activate` - Activate store
- **PATCH** `/api/v2/stores/{id}/deactivate` - Deactivate store
- **PATCH** `/api/v2/stores/{id}/under-maintenance` - Set store under maintenance
- **PATCH** `/api/v2/stores/{id}/temporary-closure` - Set temporary closure
- **DELETE** `/api/v2/stores/{id}` - Delete store

### Store Query Operations
Endpoints for querying and retrieving store information:
- **GET** `/api/v2/stores/{id}` - Get store by ID
- **GET** `/api/v2/stores/{code}/code` - Get store by code
- **GET** `/api/v2/stores` - Search stores by specifications
- **GET** `/api/v2/stores/status/{status}` - Get stores by status

## 🎯 Features

### Comprehensive Documentation
- ✅ Detailed descriptions for all endpoints
- ✅ Request/response examples with real data
- ✅ Parameter documentation with types and constraints
- ✅ Error response examples for all possible scenarios
- ✅ Authentication requirements clearly specified

### Interactive Testing
- 🧪 Test endpoints directly from the browser
- 📝 Pre-filled example requests
- 🔍 View actual responses in real-time
- ⚙️ Modify parameters and request bodies

### Error Handling Documentation
All endpoints document standard HTTP status codes:
- **200** - Success
- **201** - Created
- **400** - Bad Request (validation errors)
- **401** - Unauthorized (invalid/missing token)
- **403** - Forbidden (insufficient permissions - ADMIN role required)
- **404** - Not Found
- **409** - Conflict (duplicate resources)
- **500** - Internal Server Error

## 📦 Response Format

All endpoints return responses in a standardized format:

```json
{
  "isSuccess": true,
  "message": "Operation completed successfully",
  "data": {
    // Response data here
  },
  "timestamp": "2025-10-19T14:25:30"
}
```

### Error Response Format

```json
{
  "isSuccess": false,
  "message": "Error description",
  "timestamp": "2025-10-19T14:25:30",
  "errorDetails": {
    "errorCode": "ERROR_CODE",
    "message": "Detailed error message",
    "validationErrors": {
      "field": "Error for this field"
    }
  }
}
```

## 🔄 Pagination

List endpoints support pagination with query parameters:

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | integer | 0 | Page number (0-based) |
| `size` | integer | 10 | Items per page (max: 100) |
| `sort` | string | - | Sort field and direction (e.g., "name,asc") |

Example:
```
GET /api/v2/stores?page=0&size=20&sort=name,asc
```

## 🛠️ Development

### Running the Application

```bash
./gradlew bootRun
```

### Building the Application

```bash
./gradlew build
```

### Testing with Swagger

1. Start the application
2. Open `http://localhost:8080/swagger-ui/index.html`
3. Authenticate using your JWT token
4. Select an endpoint and click "Try it out"
5. Fill in the parameters
6. Click "Execute"
7. View the response

## 🎨 Example Requests

### Creating a Store

```json
{
  "code": "STR-001",
  "name": "Central Pharmacy",
  "phone": "+1-555-0123",
  "email": "central@pharmacy.com",
  "address": {
    "street": "123 Main St",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "country": "USA"
  },
  "latitude": 40.7128,
  "longitude": -74.0060,
  "schedule": {
    "monday": { "openTime": "08:00", "closeTime": "20:00" },
    "tuesday": { "openTime": "08:00", "closeTime": "20:00" },
    "wednesday": { "openTime": "08:00", "closeTime": "20:00" },
    "thursday": { "openTime": "08:00", "closeTime": "20:00" },
    "friday": { "openTime": "08:00", "closeTime": "20:00" },
    "saturday": { "openTime": "09:00", "closeTime": "18:00" },
    "sunday": { "openTime": "10:00", "closeTime": "16:00" }
  }
}
```

## 📞 Support

For issues or questions regarding the API documentation:
- 📧 Email: support@drugstore-api.com
- 🐛 GitHub Issues: [Report an issue](https://github.com/drugstore-api/issues)

## 📄 License

This API is licensed under the MIT License.

---

**Note**: This documentation is automatically generated from code annotations and kept in sync with the actual API implementation.

