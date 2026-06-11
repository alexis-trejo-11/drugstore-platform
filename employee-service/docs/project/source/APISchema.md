---
# APISchema
type: "REST"

# ApiEndpoint[]
httpEndpoints:

  # ========== COMMAND ENDPOINTS (/api/v2/employees) ==========

  - id: "create-employee"
    method: "POST"
    urlPath: "/api/v2/employees"
    summary: "Create employee"
    description: "Creates a new employee with personal info, role, type, and compensation"
    authenticated: true
    rateLimit: "SENSITIVE (10 requests per minute per user)"
    tags:
      - "Employee Command Operations"
      - "POST"
    parameters: []
    requestBody:
      contentType: "application/json"
      schema:
        type: "object"
        required: ["firstName", "lastName", "employeeNumber", "dateOfBirth", "contactInfo", "role", "employeeType", "status", "hireDate", "hourlyRate", "weeklyHours"]
        properties:
          firstName: type: "string" maxLength: 100
          lastName: type: "string" maxLength: 100
          employeeNumber: type: "string" maxLength: 20
          dateOfBirth: type: "string" format: "date"
          contactInfo:
            type: "object"
            properties:
              email: type: "string" format: "email"
              phoneNumber: type: "string"
              address:
                type: "object"
                properties:
                  street: type: "string"
                  city: type: "string"
                  state: type: "string"
                  country: type: "string"
                  postalCode: type: "string"
          role: type: "string" enum: ["PHARMACIST", "PHARMACY_TECHNICIAN", "STORE_MANAGER", "ASSISTANT_MANAGER", "CASHIER", "INVENTORY_CLERK", "DELIVERY_DRIVER", "CUSTOMER_SERVICE_REP", "JANITOR"]
          employeeType: type: "string" enum: ["FULL_TIME", "PART_TIME", "CONTRACTOR", "INTERN", "SEASONAL"]
          status: type: "string" enum: ["ACTIVE", "INACTIVE", "ON_LEAVE", "SUSPENDED", "TERMINATED"]
          department: type: "string" maxLength: 100
          storeId: type: "string" maxLength: 50
          hireDate: type: "string" format: "date"
          hourlyRate: type: "number" format: "double"
          weeklyHours: type: "integer"
          workdaySchedule: type: "object" description: "JSONB column for flexible schedule"
      example:
        firstName: "John"
        lastName: "Doe"
        employeeNumber: "EMP-001"
        dateOfBirth: "1985-05-15"
        contactInfo:
          email: "john.doe@drugstore.com"
          phoneNumber: "+1234567890"
          address:
            street: "123 Main St"
            city: "New York"
            state: "NY"
            country: "US"
            postalCode: "10001"
        role: "PHARMACIST"
        employeeType: "FULL_TIME"
        status: "ACTIVE"
        department: "Pharmacy"
        storeId: "store-001"
        hireDate: "2024-01-15"
        hourlyRate: 45.50
        weeklyHours: 40
        workdaySchedule: {"monday": "9AM-5PM", "tuesday": "9AM-5PM"}
    responses:
      - status: 201
        description: "Employee created successfully"
        example:
          success: true
          message: "Employee created successfully"
          data: "123e4567-e89b-12d3-a456-426614174000"
          timestamp: "2026-04-29T10:30:00Z"
      - status: 400
        description: "Invalid input data"
        example:
          success: false
          message: "First name is required"
          errorCode: "VALIDATION_ERROR"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "update-employee"
    method: "PUT"
    urlPath: "/api/v2/employees/{id}"
    summary: "Update employee"
    description: "Updates an existing employee's information"
    authenticated: true
    rateLimit: "STANDARD (60 requests per minute per user)"
    tags:
      - "Employee Command Operations"
      - "PUT"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Employee ID (UUID)"
        example: "123e4567-e89b-12d3-a456-426614174000"
    requestBody:
      contentType: "application/json"
      schema:
        type: "object"
        required: ["firstName", "lastName", "contactInfo", "role", "employeeType", "status"]
      example:
        firstName: "John"
        lastName: "Doe"
        contactInfo:
          email: "john.doe@drugstore.com"
          phoneNumber: "+1234567890"
        role: "PHARMACIST"
        employeeType: "FULL_TIME"
        status: "ACTIVE"
    responses:
      - status: 200
        description: "Employee updated successfully"
        example:
          success: true
          message: "Employee updated successfully"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "add-certification"
    method: "POST"
    urlPath: "/api/v2/employees/{id}/certifications"
    summary: "Add certification"
    description: "Adds a certification to an employee (e.g., Pharmacy License, CPR Certification)"
    authenticated: true
    rateLimit: "STANDARD (60 requests per minute per user)"
    tags:
      - "Employee Command Operations"
      - "POST"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Employee ID (UUID)"
    requestBody:
      contentType: "application/json"
      schema:
        type: "object"
        required: ["name", "issuingOrganization", "issueDate"]
        properties:
          name: type: "string" maxLength: 200
          issuingOrganization: type: "string" maxLength: 200
          issueDate: type: "string" format: "date"
          expirationDate: type: "string" format: "date"
      example:
        name: "Pharmacy License"
        issuingOrganization: "State Board of Pharmacy"
        issueDate: "2024-01-15"
        expirationDate: "2026-01-15"
    responses:
      - status: 200
        description: "Certification added successfully"
        example:
          success: true
          message: "Certification added successfully"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "change-role"
    method: "PATCH"
    urlPath: "/api/v2/employees/{id}/role"
    summary: "Change employee role"
    description: "Changes the role of an employee (e.g., from CASHIER to STORE_MANAGER)"
    authenticated: true
    rateLimit: "SENSITIVE (10 requests per minute per user)"
    tags:
      - "Employee Command Operations"
      - "PATCH"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Employee ID (UUID)"
    requestBody:
      contentType: "application/json"
      schema:
        type: "object"
        required: ["role"]
        properties:
          role: type: "string" enum: ["PHARMACIST", "PHARMACY_TECHNICIAN", "STORE_MANAGER", "ASSISTANT_MANAGER", "CASHIER", "INVENTORY_CLERK", "DELIVERY_DRIVER", "CUSTOMER_SERVICE_REP", "JANITOR"]
      example:
        role: "STORE_MANAGER"
    responses:
      - status: 200
        description: "Role changed successfully"
        example:
          success: true
          message: "Employee role changed successfully"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "change-status"
    method: "PATCH"
    urlPath: "/api/v2/employees/{id}/status"
    summary: "Change employee status"
    description: "Changes employment status (ACTIVE → SUSPENDED → TERMINATED)"
    authenticated: true
    rateLimit: "STANDARD (60 requests per minute per user)"
    tags:
      - "Employee Command Operations"
      - "PATCH"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Employee ID (UUID)"
    requestBody:
      contentType: "application/json"
      schema:
        type: "object"
        required: ["status"]
        properties:
          status: type: "string" enum: ["ACTIVE", "INACTIVE", "ON_LEAVE", "SUSPENDED", "TERMINATED"]
      example:
        status: "SUSPENDED"
    responses:
      - status: 200
        description: "Status changed successfully"
        example:
          success: true
          message: "Employee status changed successfully"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "update-compensation"
    method: "PATCH"
    urlPath: "/api/v2/employees/{id}/compensation"
    summary: "Update compensation"
    description: "Updates employee's hourly rate and/or weekly hours"
    authenticated: true
    rateLimit: "SENSITIVE (10 requests per minute per user)"
    tags:
      - "Employee Command Operations"
      - "PATCH"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Employee ID (UUID)"
    requestBody:
      contentType: "application/json"
      schema:
        type: "object"
        properties:
          hourlyRate: type: "number" format: "double"
          weeklyHours: type: "integer"
      example:
        hourlyRate: 50.00
        weeklyHours: 40
    responses:
      - status: 200
        description: "Compensation updated successfully"
        example:
          success: true
          message: "Compensation updated successfully"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "suspend-employee"
    method: "PATCH"
    urlPath: "/api/v2/employees/{id}/suspend"
    summary: "Suspend employee"
    description: "Suspends an employee with a reason and who suspended them"
    authenticated: true
    rateLimit: "SENSITIVE (10 requests per minute per user)"
    tags:
      - "Employee Command Operations"
      - "PATCH"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Employee ID (UUID)"
      - name: "reason"
        in: "query"
        type: "string"
        required: true
        description: "Reason for suspension"
        example: "Policy violation"
      - name: "suspendedBy"
        in: "query"
        type: "string"
        required: true
        description: "User ID who suspended the employee"
        example: "usr-admin-001"
    requestBody: null
    responses:
      - status: 200
        description: "Employee suspended successfully"
        example:
          success: true
          message: "Employee suspended successfully"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "activate-employee"
    method: "PATCH"
    urlPath: "/api/v2/employees/{id}/activate"
    summary: "Activate employee"
    description: "Activates a previously suspended or inactive employee"
    authenticated: true
    rateLimit: "STANDARD (60 requests per minute per user)"
    tags:
      - "Employee Command Operations"
      - "PATCH"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Employee ID (UUID)"
      - name: "reason"
        in: "query"
        type: "string"
        required: true
        description: "Reason for activation"
        example: "Reinstated after review"
      - name: "activatedBy"
        in: "query"
        type: "string"
        required: true
        description: "User ID who activated the employee"
        example: "usr-admin-001"
    requestBody: null
    responses:
      - status: 200
        description: "Employee activated successfully"
        example:
          success: true
          message: "Employee activated successfully"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "put-on-leave"
    method: "PATCH"
    urlPath: "/api/v2/employees/{id}/on-leave"
    summary: "Put employee on leave"
    description: "Places an employee on leave (e.g., medical, personal)"
    authenticated: true
    rateLimit: "STANDARD (60 requests per minute per user)"
    tags:
      - "Employee Command Operations"
      - "PATCH"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Employee ID (UUID)"
      - name: "reason"
        in: "query"
        type: "string"
        required: true
        description: "Reason for leave"
        example: "Medical leave"
      - name: "approvedBy"
        in: "query"
        type: "string"
        required: true
        description: "User ID who approved the leave"
        example: "usr-manager-001"
    requestBody: null
    responses:
      - status: 200
        description: "Employee put on leave successfully"
        example:
          success: true
          message: "Employee put on leave successfully"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "delete-employee"
    method: "DELETE"
    urlPath: "/api/v2/employees/{id}"
    summary: "Delete employee"
    description: "Soft deletes an employee (sets deletedAt timestamp)"
    authenticated: true
    rateLimit: "SENSITIVE (10 requests per minute per user)"
    tags:
      - "Employee Command Operations"
      - "DELETE"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Employee ID (UUID)"
      - name: "deletedBy"
        in: "query"
        type: "string"
        required: true
        description: "User ID who deleted the employee"
        example: "usr-admin-001"
    requestBody: null
    responses:
      - status: 200
        description: "Employee deleted successfully"
        example:
          success: true
          message: "Employee deleted successfully"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "restore-employee"
    method: "PATCH"
    urlPath: "/api/v2/employees/{id}/restore"
    summary: "Restore employee"
    description: "Restores a previously deleted (soft-deleted) employee"
    authenticated: true
    rateLimit: "SENSITIVE (10 requests per minute per user)"
    tags:
      - "Employee Command Operations"
      - "PATCH"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Employee ID (UUID)"
      - name: "restoredBy"
        in: "query"
        type: "string"
        required: true
        description: "User ID who restored the employee"
        example: "usr-admin-001"
    requestBody: null
    responses:
      - status: 200
        description: "Employee restored successfully"
        example:
          success: true
          message: "Employee restored successfully"
          timestamp: "2026-04-29T10:30:00Z"

  # ========== QUERY ENDPOINTS (/api/v2/employees) ==========

  - id: "get-employee-by-id"
    method: "GET"
    urlPath: "/api/v2/employees/{id}"
    summary: "Get employee by ID"
    description: "Retrieves an employee by their unique ID"
    authenticated: true
    rateLimit: "PUBLIC (60 requests per minute per user)"
    tags:
      - "Employee Query Operations"
      - "GET"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Employee ID (UUID)"
        example: "123e4567-e89b-12d3-a456-426614174000"
    requestBody: null
    responses:
      - status: 200
        description: "Employee found"
        example:
          success: true
          message: "Employee found"
          data:
            id: "123e4567-e89b-12d3-a456-426614174000"
            employeeNumber: "EMP-001"
            firstName: "John"
            lastName: "Doe"
            role: "PHARMACIST"
            employeeType: "FULL_TIME"
            status: "ACTIVE"
            hireDate: "2024-01-15"
            hourlyRate: 45.50
            weeklyHours: 40
          timestamp: "2026-04-29T10:30:00Z"
      - status: 404
        description: "Employee not found"
        example:
          success: false
          message: "Employee not found with id: 999e4567-e89b-12d3-a456-426614174999"
          errorCode: "NOT_FOUND"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "get-employee-by-number"
    method: "GET"
    urlPath: "/api/v2/employees/by-number/{employeeNumber}"
    summary: "Get employee by employee number"
    description: "Retrieves an employee by their unique employee number"
    authenticated: true
    rateLimit: "PUBLIC (60 requests per minute per user)"
    tags:
      - "Employee Query Operations"
      - "GET"
    parameters:
      - name: "employeeNumber"
        in: "path"
        type: "string"
        required: true
        description: "Employee number (unique)"
        example: "EMP-001"
    requestBody: null
    responses:
      - status: 200
        description: "Employee found"
        example:
          success: true
          message: "Employee found"
          data:
            id: "123e4567-e89b-12d3-a456-426614174000"
            employeeNumber: "EMP-001"
            firstName: "John"
          timestamp: "2026-04-29T10:30:00Z"
      - status: 404
        description: "Employee not found"
        example:
          success: false
          message: "Employee not found with number: EMP-999"
          errorCode: "NOT_FOUND"
          timestamp: "2026-04-29T10:30:00Z"

  - id: "search-employees"
    method: "GET"
    urlPath: "/api/v2/employees"
    summary: "Search employees"
    description: "Searches employees with filters and pagination"
    authenticated: true
    rateLimit: "PUBLIC (60 requests per minute per user)"
    tags:
      - "Employee Query Operations"
      - "GET"
    parameters:
      - name: "firstName"
        in: "query"
        type: "string"
        required: false
        description: "Filter by first name"
      - name: "lastName"
        in: "query"
        type: "string"
        required: false
        description: "Filter by last name"
      - name: "role"
        in: "query"
        type: "string"
        required: false
        description: "Filter by role"
      - name: "status"
        in: "query"
        type: "string"
        required: false
        description: "Filter by status"
      - name: "page"
        in: "query"
        type: "integer"
        required: false
        description: "Page number (0-indexed)"
      - name: "size"
        in: "query"
        type: "integer"
        required: false
        description: "Page size (default 20)"
    requestBody: null
    responses:
      - status: 200
        description: "Search results"
        example:
          success: true
          message: "Employees found"
          data:
            content:
              - id: "123e4567-e89b-12d3-a456-426614174000"
                firstName: "John"
                lastName: "Doe"
                role: "PHARMACIST"
            pageable:
              pageNumber: 0
              pageSize: 20
            totalElements: 50
            totalPages: 3
          timestamp: "2026-04-29T10:30:00Z"

  - id: "check-exists-by-id"
    method: "GET"
    urlPath: "/api/v2/employees/exists/id/{id}"
    summary: "Check if employee exists by ID"
    description: "Returns true/false if an employee exists with the given ID"
    authenticated: true
    rateLimit: "PUBLIC (60 requests per minute per user)"
    tags:
      - "Employee Query Operations"
      - "GET"
    parameters:
      - name: "id"
        in: "path"
        type: "string"
        required: true
        description: "Employee ID to check"
        example: "123e4567-e89b-12d3-a456-426614174000"
    requestBody: null
    responses:
      - status: 200
        description: "Check completed"
        example:
          success: true
          message: "Employee with ID 123e4567-e89b-12d3-a456-426614174000 exists: true"
          data: true
          timestamp: "2026-04-29T10:30:00Z"

  - id: "check-exists-by-number"
    method: "GET"
    urlPath: "/api/v2/employees/exists/number/{employeeNumber}"
    summary: "Check if employee exists by number"
    description: "Returns true/false if an employee exists with the given employee number"
    authenticated: true
    rateLimit: "PUBLIC (60 requests per minute per user)"
    tags:
      - "Employee Query Operations"
      - "GET"
    parameters:
      - name: "employeeNumber"
        in: "path"
        type: "string"
        required: true
        description: "Employee number to check"
        example: "EMP-001"
    requestBody: null
    responses:
      - status: 200
        description: "Check completed"
        example:
          success: true
          message: "Employee with number EMP-001 exists: true"
          data: true
          timestamp: "2026-04-29T10:30:00Z"
---
# API Schema

> 16 REST endpoints documented (10 command + 6 query) with full request/response examples. Properly uses @RateLimit from libs-kernel with 3 profiles (PUBLIC, STANDARD, SENSITIVE). PLACEHOLDER issues: No Dockerfile/docker-compose.yml found, no Kafka event publishing yet. Potential improvements: Add Kafka events (employee.created/updated/deleted, employee.status-changed), implement caching for employee lookups, add Kubernetes manifests, set up CI/CD pipeline.
