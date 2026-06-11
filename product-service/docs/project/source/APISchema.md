---
# APISchema
type: "REST"

# ApiEndpoint[]
httpEndpoints:
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
---

# API Schema

## Notes

- Public read endpoints expose full product payloads; confirm no sensitive attributes are included.
- No API version deprecation policy is documented yet for `/api/v2/products`.