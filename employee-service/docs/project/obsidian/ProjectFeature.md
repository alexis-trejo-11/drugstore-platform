---
# ProjectFeature[]
features:
  - id: "rich-jpa-entity"
    title: "Rich JPA Entity Model"
    description: "EmployeeEntity with embedded objects (ContactInfo, Address), certifications list (OneToMany), workday schedule as JSONB, and comprehensive enums for roles, types, and status."
    icon: "📦"
    category: "database"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/employee-service/src/main/java/io/github/alexisTrejo11/drugstore/employees/adapter/outbound/persistence/entity/EmployeeEntity.java"
    highlights:
      - "EmployeeRoleEnum: 9 roles (PHARMACIST, STORE_MANAGER, CASHIER, etc.)"
      - "EmployeeTypeEnum: 4 types (FULL_TIME, PART_TIME, CONTRACTOR, INTERN, SEASONAL)"
      - "EmployeeStatusEnum: 5 statuses (ACTIVE, INACTIVE, ON_LEAVE, SUSPENDED, TERMINATED)"
      - "ContactInfoEmbeddable and AddressEmbeddable for complex objects"
      - "workdaySchedule as JSONB column for flexible schedule storage"
      - "Certifications list with cascade ALL and orphanRemoval"
      - "5 database indexes: employeeNumber (unique), status, role, storeId, deletedAt"
      - "Audit fields: createdAt, updatedAt, createdBy, lastModifiedBy"
    techStack:
      - "Spring Data JPA"
      - "PostgreSQL 15"
      - "Flyway Migrations 10.17.0"
      - "JSONB (Hibernate @JdbcTypeCode)"
    metrics:
      - label: "Employee Roles"
        value: "9"
        trend: "stable"
        icon: "roles"
      - label: "Employee Types"
        value: "5"
        trend: "stable"
        icon: "types"
      - label: "Statuses"
        value: "5"
        trend: "stable"
        icon: "status"
    codeSnippet:
      language: "java"
      filename: "EmployeeEntity.java"
      code: |
        @Entity
        @Table(name = "employees", indexes = {
            @Index(name = "idx_employee_number", columnList = "employeeNumber", unique = true),
            @Index(name = "idx_employee_status", columnList = "status"),
            @Index(name = "idx_employee_role", columnList = "role")
        })
        public class EmployeeEntity {
            @Enumerated(EnumType.STRING)
            private EmployeeRoleEnum role;

            @JdbcTypeCode(SqlTypes.JSON)
            @Column(columnDefinition = "jsonb")
            private Map<String, Object> workdaySchedule = new HashMap<>();

            @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
            private List<CertificationEntity> certifications = new ArrayList<>();
        }

  - id: "command-query-separation"
    title: "Command/Query Separation (CQS)"
    description: "Separate controllers and services for commands (writes) and queries (reads) with dedicated command/query objects."
    icon: "📋"
    category: "architecture"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/employee-service/src/main/java/io/github/alexisTrejo11/drugstore/employees/adapter/inbound/rest/controller/EmployeeCommandController.java"
    highlights:
      - "EmployeeCommandController: POST, PUT, PATCH, DELETE operations"
      - "EmployeeQueryController: GET operations (by id, by number, search, exists checks)"
      - "Command Objects: CreateEmployeeCommand, UpdateEmployeeCommand, ChangeRoleCommand, ChangeStatusCommand, UpdateCompensationCommand, SuspendEmployeeCommand, ActivateEmployeeCommand, PutOnLeaveCommand, DeleteEmployeeCommand, RestoreEmployeeCommand, AddCertificationCommand"
      - "Query Objects: GetEmployeeByIdQuery, GetEmployeeByNumberQuery, SearchEmployeesQuery, CheckEmployeeExistsByIdQuery, CheckEmployeeExistsByNumberQuery"
      - "All endpoints use @RateLimit with profiles from libs-kernel"
    techStack:
      - "CQS Pattern"
      - "DDD"
      - "Spring MVC"
      - "libs-kernel (shared ResponseWrapper, RateLimit)"
    metrics:
      - label: "Command Endpoints"
        value: "10"
        trend: "stable"
        icon: "command"
      - label: "Query Endpoints"
        value: "6"
        trend: "stable"
        icon: "query"
    codeSnippet:
      language: "java"
      filename: "EmployeeCommandController.java"
      code: |
        @PostMapping
        @RateLimit(profile = RateLimitProfile.SENSITIVE)
        public ResponseEntity<ResponseWrapper<EmployeeId>> createEmployee(
                @Valid @RequestBody CreateEmployeeRequest request) {

            CreateEmployeeCommand command = request.toCommand();
            EmployeeId employeeId = employeeCommandService.createEmployee(command);

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseWrapper.created(employeeId));
        }

  - id: "rate-limiting"
    title: "Rate Limiting with libs-kernel"
    description: "Uses @RateLimit annotations from shared libs-kernel library with three profiles: PUBLIC (60/min), STANDARD (60/min), SENSITIVE (10/min)."
    icon: "🚦"
    category: "security"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/employee-service/src/main/java/io/github/alexisTrejo11/drugstore/employees/adapter/inbound/rest/controller/EmployeeCommandController.java"
    highlights:
      - "PUBLIC profile (60 requests/min): Used for query endpoints (GET)"
      - "STANDARD profile (60 requests/min): Used for standard operations (PUT, PATCH for status/role)"
      - "SENSITIVE profile (10 requests/min): Used for sensitive operations (POST, DELETE, PATCH for suspend/activate)"
      - "Redis-backed rate limiting via libs-kernel RateLimitAspect"
      - "All endpoints annotated (unlike cart-service which has none)"
    techStack:
      - "libs-kernel (shared library)"
      - "Spring AOP"
      - "Redis"
    metrics:
      - label: "PUBLIC Rate"
        value: "60/min"
        trend: "stable"
        icon: "speed"
      - label: "SENSITIVE Rate"
        value: "10/min"
        trend: "stable"
        icon: "lock"
    codeSnippet:
      language: "java"
      filename: "EmployeeCommandController.java"
      code: |
        @PatchMapping("/{id}/suspend")
        @RateLimit(profile = RateLimitProfile.SENSITIVE)
        public ResponseWrapper<Void> suspendEmployee(
                @EmployeeIdPathParameter @PathVariable String id,
                @RequestParam String reason,
                @RequestParam String suspendedBy) {

            SuspendEmployeeCommand command = new SuspendEmployeeCommand(
                EmployeeId.of(id), reason, suspendedBy);
            employeeCommandService.suspendEmployee(command);

            return ResponseWrapper.success("Employee suspended successfully");
        }

  - id: "soft-delete-with-audit"
    title: "Soft Delete with Audit Trail"
    description: "Employees are soft-deleted via deletedAt timestamp, with restore functionality. Complete audit trail with createdBy, lastModifiedBy, createdAt, updatedAt."
    icon: "🗑️"
    category: "database"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/employee-service/src/main/java/io/github/alexisTrejo11/drugstore/employees/adapter/outbound/persistence/entity/EmployeeEntity.java"
    highlights:
      - "deletedAt timestamp for soft delete (null = active)"
      - "markAsDeleted() sets deletedAt and status to TERMINATED"
      - "markAsRestored() clears deletedAt and sets status to ACTIVE"
      - "Audit fields: createdAt (@PrePersist), updatedAt (@PrePersist/@PreUpdate)"
      - "createdBy and lastModifiedBy for audit trail"
      - "Status transitions: ACTIVE → SUSPENDED → TERMINATED"
    techStack:
      - "JPA/Hibernate"
      - "Spring Data JPA"
      - "Audit Fields"
    metrics:
      - label: "Audit Fields"
        value: "4"
        trend: "stable"
        icon: "audit"
    codeSnippet:
      language: "java"
      filename: "EmployeeEntity.java"
      code: |
        public void markAsDeleted() {
            this.deletedAt = LocalDateTime.now();
            this.status = EmployeeStatusEnum.TERMINATED;
        }

        public void markAsRestored() {
            this.deletedAt = null;
            this.status = EmployeeStatusEnum.ACTIVE;
        }

        @PrePersist
        protected void onCreate() {
            if (createdAt == null) {
                createdAt = LocalDateTime.now();
            }
            updatedAt = LocalDateTime.now();
        }

  - id: "enum-types"
    title: "Comprehensive Enum Types"
    description: "Type-safe enums for employee roles (9), types (5), and statuses (5) stored as strings in PostgreSQL using @Enumerated(EnumType.STRING)."
    icon: "🏷️"
    category: "domain"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/employee-service/src/main/java/io/github/alexisTrejo11/drugstore/employees/adapter/outbound/persistence/entity/EmployeeEntity.java"
    highlights:
      - "EmployeeRoleEnum: PHARMACIST, PHARMACY_TECHNICIAN, STORE_MANAGER, ASSISTANT_MANAGER, CASHIER, INVENTORY_CLERK, DELIVERY_DRIVER, CUSTOMER_SERVICE_REP, JANITOR"
      - "EmployeeTypeEnum: FULL_TIME, PART_TIME, CONTRACTOR, INTERN, SEASONAL"
      - "EmployeeStatusEnum: ACTIVE, INACTIVE, ON_LEAVE, SUSPENDED, TERMINATED"
      - "Stored as strings in PostgreSQL for readability"
      - "Easy to add new values without schema changes"
    techStack:
      - "Java Enums"
      - "JPA @Enumerated"
      - "PostgreSQL ENUM (optional)"
    metrics:
      - label: "Total Enums"
        value: "3"
        trend: "stable"
        icon: "enum"
      - label: "Total Values"
        value: "19"
        trend: "up"
        icon: "values"
    codeSnippet:
      language: "java"
      filename: "EmployeeEntity.java"
      code: |
        public enum EmployeeRoleEnum {
            PHARMACIST,
            PHARMACY_TECHNICIAN,
            STORE_MANAGER,
            ASSISTANT_MANAGER,
            CASHIER,
            INVENTORY_CLERK,
            DELIVERY_DRIVER,
            CUSTOMER_SERVICE_REP,
            JANITOR
        }

  - id: "jsonb-workday-schedule"
    title: "JSONB Workday Schedule Storage"
    description: "Flexible workday schedule stored as JSONB in PostgreSQL, allowing complex schedule structures without schema changes."
    icon: "📅"
    category: "database"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/employee-service/src/main/java/io/github/alexisTrejo11/drugstore/employees/adapter/outbound/persistence/entity/EmployeeEntity.java"
    highlights:
      - "Stored as JSONB column using @JdbcTypeCode(SqlTypes.JSON)"
      - "Flexible key-value structure for different schedule formats"
      - "Example: {\"monday\": \"9AM-5PM\", \"tuesday\": \"9AM-5PM\"}"
      - "No schema changes needed for schedule format updates"
      - "Hibernate handles JSONB serialization/deserialization"
    techStack:
      - "PostgreSQL JSONB"
      - "Hibernate @JdbcTypeCode"
      - "Java Map<String, Object>"
    metrics:
      - label: "Column Type"
        value: "JSONB"
        trend: "stable"
        icon: "json"
    codeSnippet:
      language: "java"
      filename: "EmployeeEntity.java"
      code: |
        @JdbcTypeCode(SqlTypes.JSON)
        @Column(columnDefinition = "jsonb")
        private Map<String, Object> workdaySchedule = new HashMap<>();

  - id: "certification-management"
    title: "Certification Management"
    description: "Track employee certifications (pharmacy license, CPR, etc.) with issuance and expiration dates. OneToMany relationship with cascade ALL."
    icon: "📜"
    category: "feature"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/employee-service/src/main/java/io/github/alexisTrejo11/drugstore/employees/adapter/outbound/persistence/entity/CertificationEntity.java"
    highlights:
      - "CertificationEntity with name, issuingOrganization, issueDate, expirationDate"
      - "OneToMany relationship from EmployeeEntity with cascade ALL and orphanRemoval"
      - "REST endpoint: POST /api/v2/employees/{id}/certifications"
      - "Stored in separate table with foreign key to employees"
      - "Supports multiple certifications per employee"
    techStack:
      - "Spring Data JPA"
      - "PostgreSQL"
      - "JPA Relationships"
    metrics:
      - label: "Relationship"
        value: "OneToMany"
        trend: "stable"
        icon: "relationship"
    codeSnippet:
      language: "java"
      filename: "EmployeeEntity.java"
      code: |
        @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
        @JoinColumn(name = "employee_id")
        private List<CertificationEntity> certifications = new ArrayList<>();

  - id: "specification-search"
    title: "Dynamic Search with Specification Pattern"
    description: "EmployeeSpecificationBuilder creates dynamic JPA criteria queries for searching employees with multiple optional filters."
    icon: "🔍"
    category: "database"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/employee-service/src/main/java/io/github/alexisTrejo11/drugstore/employees/adapter/outbound/persistence/specification/EmployeeSpecificationBuilder.java"
    highlights:
      - "Optional filters: firstName, lastName, role, status, employeeType, storeId"
      - "Uses JPA Specification API for type-safe criteria building"
      - "Pagination support via Pageable parameter"
      - "Returns Page<EmployeeEntity> for efficient large result sets"
    techStack:
      - "Spring Data JPA"
      - "JPA Criteria API"
      - "Specification Pattern"
    metrics:
      - label: "Filter Options"
        value: "6+"
        trend: "stable"
        icon: "filter"
    codeSnippet:
      language: "java"
      filename: "PLACEHOLDER: EmployeeSpecificationBuilder.java"
      code: |
        // Builds dynamic JPA Specifications based on search criteria
        // Supports filtering by: firstName, lastName, role, status, etc.

  - id: "cloud-config-client"
    title: "Spring Cloud Config Client"
    description: "Centralized configuration management using Spring Cloud Config Client 2023.0.3, reading config from config-server directory."
    icon: "⚙️"
    category: "devops"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/employee-service/build.gradle"
    highlights:
      - "Reads configuration from ../config-server/.env file"
      - "Spring Cloud Config Client 2023.0.3 integration"
      - "Externalized configuration for different environments"
      - "No hardcoded values in application.yml"
    techStack:
      - "Spring Cloud Config 2023.0.3"
      - "Spring Cloud Bootstrap"
    metrics:
      - label: "Config Version"
        value: "2023.0.3"
        trend: "stable"
        icon: "config"
    codeSnippet:
      language: "groovy"
      filename: "build.gradle"
      code: |
        ext {
            set('springCloudVersion', "2023.0.3")
        }

        dependencies {
            implementation 'org.springframework.cloud:spring-cloud-starter-bootstrap'
            implementation 'org.springframework.cloud:spring-cloud-starter-config'
        }

  - id: "flyway-migrations"
    title: "Database Migrations with Flyway"
    description: "Versioned database schema management using Flyway 10.17.0. V1__create_tables.sql and V2__insert_dummy_data.sql."
    icon: "✈️"
    category: "database"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/employee-service/src/main/resources/db/migration"
    highlights:
      - "Flyway Core 10.17.0 with PostgreSQL support"
      - "V1__create_tables.sql: Creates employees and certifications tables with indexes"
      - "V2__insert_dummy_data.sql: Sample employee data for development"
      - "Automatic migration on application startup"
      - "Indexes on employeeNumber (unique), status, role, storeId, deletedAt"
    techStack:
      - "Flyway 10.17.0"
      - "PostgreSQL 15"
      - "Spring Boot"
    metrics:
      - label: "Migrations"
        value: "2"
        trend: "up"
        icon: "migration"
      - label: "Indexed Columns"
        value: "5"
        trend: "stable"
        icon: "index"
    codeSnippet:
      language: "sql"
      filename: "V1__create_tables.sql"
      code: |
        CREATE TABLE employees (
            id VARCHAR(36) PRIMARY KEY,
            employee_number VARCHAR(20) UNIQUE NOT NULL,
            first_name VARCHAR(100) NOT NULL,
            last_name VARCHAR(100) NOT NULL,
            role VARCHAR(50) NOT NULL,
            status VARCHAR(20) NOT NULL,
            -- ... more columns
        );

        CREATE INDEX idx_employee_status ON employees(status);
        CREATE INDEX idx_employee_role ON employees(role);
---
# Project Features

> 11 comprehensive features documented covering JPA entity model, CQS, rate limiting, soft delete, enums, JSONB, certifications, specifications, cloud config, and Flyway. Uses @RateLimit correctly (unlike cart-service). 
> 
> **Potential Issues & Improvements:**
> - No Dockerfile found in employee-service (unlike address-service and cart-service)
> - No docker-compose.yml found (needs PostgreSQL and Redis)
> - No unit/integration tests found (critical for employee management)
> - No Kafka event publishing (employee.created/updated/deleted events)
> - No Kubernetes manifests for cloud deployment
> - No CI/CD pipeline (GitHub Actions/Jenkins)
> - Java 23 (class version 69) incompatibility with Gradle 8.8
> - Consider adding @Cacheable annotations for frequently accessed employees
> - Add Micrometer metrics for employee operations
> - Implement Circuit Breaker for external service calls
