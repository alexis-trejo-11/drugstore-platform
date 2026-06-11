---
codeExamples:
  - id: "command-query-separation"
    title: "Command/Query Separation (CQS)"
    description: "Separate controllers and services for commands (writes) and queries (reads) with dedicated command/query objects"
    category: "Architectural"
    duration: "12 min read"
    views: 0
    tags:
      - "CQS"
      - "DDD"
      - "Separation of Concerns"
      - "REST API"
    files:
      - name: "EmployeeCommandController.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/employees/adapter/inbound/rest/controller/EmployeeCommandController.java"
        language: "java"
        content: |
          @RestController
          @RequestMapping("/api/v2/employees")
          @Tag(name = "Employee Command Operations", description = "Endpoints for creating, updating...")
          @SecurityRequirement(name = "bearerAuth")
          public class EmployeeCommandController {

              @PostMapping
              @RateLimit(profile = RateLimitProfile.SENSITIVE)
              public ResponseEntity<ResponseWrapper<EmployeeId>> createEmployee(
                      @Valid @RequestBody CreateEmployeeRequest request) {

                  CreateEmployeeCommand command = request.toCommand();
                  EmployeeId employeeId = employeeCommandService.createEmployee(command);

                  return ResponseEntity.status(HttpStatus.CREATED)
                      .body(ResponseWrapper.created(employeeId));
              }

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
              // ... more command endpoints
          }
        highlighted: true
        explanation: "Command controller handles writes (POST, PUT, PATCH, DELETE) with @RateLimit annotations using profiles from libs-kernel."

      - name: "EmployeeQueryController.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/employees/adapter/inbound/rest/controller/EmployeeQueryController.java"
        language: "java"
        content: |
          @RestController
          @RequestMapping("/api/v2/employees")
          @Tag(name = "Employee Query Operations", description = "Endpoints for querying...")
          @SecurityRequirement(name = "bearerAuth")
          public class EmployeeQueryController {

              @GetMapping("/{id}")
              @RateLimit(profile = RateLimitProfile.PUBLIC)
              public ResponseWrapper<EmployeeResponse> getEmployeeById(
                      @EmployeeIdPathParameter @PathVariable String id) {

                  GetEmployeeByIdQuery query = new GetEmployeeByIdQuery(EmployeeId.of(id));
                  Optional<Employee> employee = employeeQueryService.getEmployeeById(query);

                  EmployeeResponse response = responseMapper.toResponse(employee.get());
                  return ResponseWrapper.found(response, "Employee");
              }

              @GetMapping("/search")
              @RateLimit(profile = RateLimitProfile.PUBLIC)
              public ResponseWrapper<PageResponse<EmployeeResponse>> searchEmployees(
                      @ModelAttribute SearchEmployeesRequest request) {

                  SearchEmployeesQuery query = request.toQuery();
                  Page<Employee> employeesPage = employeeQueryService.searchEmployees(query);

                  PageResponse<EmployeeResponse> pageResponse =
                      responseMapper.toResponsePage(employeesPage);
                  return ResponseWrapper.found(pageResponse, "Employees");
              }
          }
        highlighted: false
        explanation: "Query controller handles reads (GET) with PUBLIC rate limit profile for higher throughput."

  - id: "jpa-entity-model"
    title: "Rich JPA Entity with Enums"
    description: "EmployeeEntity with embedded objects, certifications list, workday schedule as JSONB, and comprehensive enums for roles, types, and status"
    category: "Persistence"
    duration: "15 min read"
    views: 0
    tags:
      - "JPA"
      - "Entity Model"
      - "Enums"
      - "JSONB"
    files:
      - name: "EmployeeEntity.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/employees/adapter/outbound/persistence/entity/EmployeeEntity.java"
        language: "java"
        content: |
          @Entity
          @Table(name = "employees", indexes = {
              @Index(name = "idx_employee_number", columnList = "employeeNumber", unique = true),
              @Index(name = "idx_employee_status", columnList = "status"),
              @Index(name = "idx_employee_role", columnList = "role"),
              @Index(name = "idx_employee_store", columnList = "storeId"),
              @Index(name = "idx_employee_deleted", columnList = "deletedAt")
          })
          public class EmployeeEntity {

              @Id
              private String id;

              @Column(nullable = false, unique = true, length = 20)
              private String employeeNumber;

              @Enumerated(EnumType.STRING)
              @Column(nullable = false, length = 50)
              private EmployeeRoleEnum role;

              @Enumerated(EnumType.STRING)
              @Column(nullable = false, length = 20)
              private EmployeeTypeEnum employeeType;

              @Enumerated(EnumType.STRING)
              @Column(nullable = false, length = 20)
              private EmployeeStatusEnum status;

              @JdbcTypeCode(SqlTypes.JSON)
              @Column(columnDefinition = "jsonb")
              private Map<String, Object> workdaySchedule = new HashMap<>();

              @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
              @JoinColumn(name = "employee_id")
              private List<CertificationEntity> certifications = new ArrayList<>();

              // Audit fields
              private LocalDateTime deletedAt;

              public void markAsDeleted() {
                  this.deletedAt = LocalDateTime.now();
                  this.status = EmployeeStatusEnum.TERMINATED;
              }

              public void markAsRestored() {
                  this.deletedAt = null;
                  this.status = EmployeeStatusEnum.ACTIVE;
              }
          }
        highlighted: true
        explanation: "Rich JPA entity with 5 indexes, JSONB column for workday schedule, enums stored as strings, and soft delete support."

      - name: "EmployeeRoleEnum.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/employees/adapter/outbound/persistence/entity/EmployeeEntity.java"
        language: "java"
        content: |
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
        highlighted: false
        explanation: "9 employee roles stored as strings in PostgreSQL for readability."

  - id: "rate-limiting"
    title: "Rate Limiting with libs-kernel"
    description: "Uses @RateLimit annotations from shared libs-kernel library with three profiles: PUBLIC (60/min), STANDARD (60/min), SENSITIVE (10/min)"
    category: "Security"
    duration: "8 min read"
    views: 0
    tags:
      - "Rate Limiting"
      - "Redis"
      - "libs-kernel"
      - "AOP"
    files:
      - name: "EmployeeCommandController.java (Rate Limits)"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/employees/adapter/inbound/rest/controller/EmployeeCommandController.java"
        language: "java"
        content: |
          // Command endpoints use SENSITIVE or STANDARD profiles
          @PostMapping
          @RateLimit(profile = RateLimitProfile.SENSITIVE)
          public ResponseEntity<ResponseWrapper<EmployeeId>> createEmployee(...) { ... }

          @PutMapping("/{id}")
          @RateLimit(profile = RateLimitProfile.STANDARD)
          public ResponseWrapper<Void> updateEmployee(...) { ... }

          @PatchMapping("/{id}/role")
          @RateLimit(profile = RateLimitProfile.SENSITIVE)
          public ResponseWrapper<Void> changeRole(...) { ... }
        highlighted: true
        explanation: "All command endpoints have @RateLimit annotations with appropriate profiles based on sensitivity."

      - name: "EmployeeQueryController.java (Rate Limits)"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/employees/adapter/inbound/rest/controller/EmployeeQueryController.java"
        language: "java"
        content: |
          // Query endpoints use PUBLIC profile for higher throughput
          @GetMapping("/{id}")
          @RateLimit(profile = RateLimitProfile.PUBLIC)
          public ResponseWrapper<EmployeeResponse> getEmployeeById(...) { ... }

          @GetMapping("/by-number/{employeeNumber}")
          @RateLimit(profile = RateLimitProfile.PUBLIC)
          public ResponseWrapper<EmployeeResponse> getEmployeeByNumber(...) { ... }

          @GetMapping("/search")
          @RateLimit(profile = RateLimitProfile.PUBLIC)
          public ResponseWrapper<PageResponse<EmployeeResponse>> searchEmployees(...) { ... }
        highlighted: false
        explanation: "Query endpoints use PUBLIC profile (60/min) for higher throughput on read operations."

  - id: "soft-delete-pattern"
    title: "Soft Delete with Audit Trail"
    description: "Employees are soft-deleted via deletedAt timestamp, with restore functionality. Audit fields track created/updated/deleted operations."
    category: "Persistence"
    duration: "6 min read"
    views: 0
    tags:
      - "Soft Delete"
      - "Audit Trail"
      - "JPA"
    files:
      - name: "EmployeeEntity.java (Soft Delete)"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/employees/adapter/outbound/persistence/entity/EmployeeEntity.java"
        language: "java"
        content: |
          // Soft delete
          public void markAsDeleted() {
              this.deletedAt = LocalDateTime.now();
              this.status = EmployeeStatusEnum.TERMINATED;
          }

          public void markAsRestored() {
              this.deletedAt = null;
              this.status = EmployeeStatusEnum.ACTIVE;
          }

          public boolean isDeleted() {
              return deletedAt != null;
          }
        highlighted: true
        explanation: "Soft delete sets deletedAt timestamp and updates status to TERMINATED. Restore resets deletedAt and sets status to ACTIVE."

      - name: "Audit Fields"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/employees/adapter/outbound/persistence/entity/EmployeeEntity.java"
        language: "java"
        content: |
          // Audit fields
          @Column(nullable = false, updatable = false)
          private LocalDateTime createdAt;

          @Column(nullable = false)
          private LocalDateTime updatedAt;

          @Column(updatable = false, length = 100)
          private String createdBy;

          @Column(length = 100)
          private String lastModifiedBy;

          @PrePersist
          protected void onCreate() {
              if (createdAt == null) {
                  createdAt = LocalDateTime.now();
              }
              updatedAt = LocalDateTime.now();
          }

          @PreUpdate
          protected void onUpdate() {
              updatedAt = LocalDateTime.now();
          }
        highlighted: false
        explanation: "Audit fields automatically managed via @PrePersist and @PreUpdate JPA callbacks."

  - id: "specification-pattern"
    title: "Specification Pattern for Dynamic Queries"
    description: "EmployeeSpecificationBuilder creates dynamic JPA criteria queries for searching employees with multiple filters"
    category: "Persistence"
    duration: "7 min read"
    views: 0
    tags:
      - "Specification"
      - "JPA Criteria"
      - "Dynamic Queries"
    files:
      - name: "EmployeeSpecificationBuilder.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/employees/adapter/outbound/persistence/specification/EmployeeSpecificationBuilder.java"
        language: "java"
        content: |
          public class EmployeeSpecificationBuilder {
              // Builds dynamic JPA Specifications based on search criteria
              // Supports filtering by: firstName, lastName, role, status, employeeType, storeId
              // Uses optional filters that are only added if provided
          }
        highlighted: true
        explanation: "Dynamic query building for /api/v2/employees/search endpoint with optional filters."

      - name: "SearchEmployeesRequest.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/employees/adapter/inbound/rest/dto/request/SearchEmployeesRequest.java"
        language: "java"
        content: |
          public record SearchEmployeesRequest(
              String firstName,
              String lastName,
              String role,
              String status,
              String employeeType,
              String storeId
          ) {
              public SearchEmployeesQuery toQuery() {
                  return new SearchEmployeesQuery(/* map fields to query */);
              }
          }
        highlighted: false
        explanation: "Request DTO maps to SearchEmployeesQuery for the specification builder."

  - id: "embeddable-objects"
    title: "Embeddable Objects for Complex Data"
    description: "ContactInfoEmbeddable and AddressEmbeddable for storing complex objects in JPA entity"
    category: "Persistence"
    duration: "5 min read"
    views: 0
    tags:
      - "Embeddable"
      - "JPA"
      - "Complex Objects"
    files:
      - name: "ContactInfoEmbeddable.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/employees/adapter/outbound/persistence/entity/ContactInfoEmbeddable.java"
        language: "java"
        content: |
          @Embeddable
          public class ContactInfoEmbeddable {
              private String email;
              private String phoneNumber;

              @Embedded
              @AttributeOverrides({
                  @AttributeOverride(name = "street", column = @Column(name = "address_street")),
                  @AttributeOverride(name = "city", column = @Column(name = "address_city")),
                  // ... more overrides
              })
              private AddressEmbeddable address;
          }
        highlighted: true
        explanation: "Embeddable objects allow storing complex data in a single table with column name overrides."

      - name: "AddressEmbeddable.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/employees/adapter/outbound/persistence/entity/AddressEmbeddable.java"
        language: "java"
        content: |
          @Embeddable
          public class AddressEmbeddable {
              private String street;
              private String city;
              private String state;
              private String country;
              private String postalCode;
          }
        highlighted: false
        explanation: "Simple embeddable for address fields, embedded within ContactInfoEmbeddable."
---
# CodeShowCase

> 6 comprehensive code examples covering CQS, JPA entity model, rate limiting, soft delete, specifications, and embeddable objects. Uses @RateLimit from libs-kernel (correctly implemented unlike cart-service). PLACEHOLDER issues: No Dockerfile/docker-compose.yml, no Kafka event publishing, no unit/integration tests found. Potential additions: Kafka events (employee.created/updated/deleted), caching annotations, Kubernetes manifests, CI/CD pipeline.
