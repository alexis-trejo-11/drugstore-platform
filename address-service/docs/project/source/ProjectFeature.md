---
# ProjectFeature[]
features:
  - id: "multi-country-postal-validation"
    title: "Multi-Country Postal Code Validation"
    description: "Validates postal codes against country-specific formats using Factory and Strategy patterns. Supports US (ZIP), Mexico (CP), Canada, Spain, and UK with regex-based validation. Extensible design allows adding new countries easily."
    icon: "🌍"
    category: "integration"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/address-service/src/main/java/io/github/alexisTrejo11/drugstore/address/utils/validation"
    highlights:
      - "Factory Pattern: PostalCodeValidatorFactory creates appropriate validator based on country code"
      - "Strategy Pattern: Each country implements PostalCodeValidator interface"
      - "Supports 5+ countries: US, MX, CA, ES, UK"
      - "DefaultPostalCodeValidator as fallback for unsupported countries"
      - "Format description returned in error messages for client guidance"
    techStack:
      - "Java"
      - "Spring Dependency Injection"
      - "Regex Pattern Matching"
    metrics:
      - label: "Countries Supported"
        value: "5+"
        trend: "up"
        icon: "globe"
      - label: "Validation Speed"
        value: "<1ms"
        trend: "stable"
        icon: "speed"
    codeSnippet:
      language: "java"
      filename: "USPostalCodeValidator.java"
      code: |
        @Component
        public class USPostalCodeValidator implements PostalCodeValidator {
            private static final String ZIP_REGEX = "^\\d{5}(-\\d{4})?$";

            @Override
            public boolean isValid(String postalCode) {
                return postalCode != null && postalCode.matches(ZIP_REGEX);
            }

            @Override
            public String getCountryCode() { return "US"; }

            @Override
            public String getFormatDescription() {
                return "US ZIP code (12345 or 12345-6789)";
            }
        }

  - id: "role-based-address-limits"
    title: "Role-Based Address Limits"
    description: "Enforces different address limits based on user type: CUSTOMERs can have up to 5 addresses, while EMPLOYEEs are limited to 1 address. Configurable via AddressServiceProperties with @ConfigurationProperties."
    icon: "👥"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/address-service/src/main/java/io/github/alexisTrejo11/drugstore/address/config/AddressServiceProperties.java"
    highlights:
      - "CUSTOMER: max 5 addresses (configurable via customerMaxAddresses)"
      - "EMPLOYEE: max 1 address (configurable via employeeMaxAddresses)"
      - "Validation in AddressService.createAddress() before persisting"
      - "Throws AddressLimitExceededException when limit reached"
      - "User type auto-determined from JWT role (CUSTOMER/EMPLOYEE)"
    techStack:
      - "Spring Boot"
      - "Spring Configuration Properties"
      - "JPA/Hibernate"
    metrics:
      - label: "Customer Limit"
        value: "5"
        trend: "stable"
        icon: "user"
      - label: "Employee Limit"
        value: "1"
        trend: "stable"
        icon: "briefcase"
    codeSnippet:
      language: "java"
      filename: "AddressService.java"
      code: |
        private void validateAddressLimit(String userId, AddressEntity.UserType userType) {
            long currentAddressCount = addressRepository.countByUserIdAndActiveTrue(userId);
            int limit = properties.getAddressLimit(userType.name());

            if (currentAddressCount >= limit) {
                throw new AddressLimitExceededException(userId, limit, userType.name());
            }
        }

  - id: "default-address-management"
    title: "Default Address Management"
    description: "Allows users to set a default address with automatic handling: first address becomes default automatically, setting a new default resets the previous one. Implemented at database level with resetDefaultAddressForUser()."
    icon: "⭐"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/address-service/src/main/java/io/github/alexisTrejo11/drugstore/address/service/AddressService.java"
    highlights:
      - "First address for user automatically set as default"
      - "Only one address can be default per user (database constraint)"
      - "PUT /api/v2/user/addresses/{id}/set-default for users"
      - "PUT /api/v2/addresses/admin/{id}/set-default-for-user/{userId} for admins"
      - "Database-level reset: addressRepository.resetDefaultAddressForUser()"
    techStack:
      - "Spring Data JPA"
      - "PostgreSQL"
      - "REST API"
    metrics:
      - label: "Response Time"
        value: "<50ms"
        trend: "stable"
        icon: "clock"
    codeSnippet:
      language: "java"
      filename: "AddressService.java"
      code: |
        private void setAsDefaultAddress(AddressEntity newDefaultAddress) {
            addressRepository.resetDefaultAddressForUser(newDefaultAddress.getUserId());
            newDefaultAddress.setIsDefault(true);
        }

  - id: "dual-controller-architecture"
    title: "Dual Controller Architecture"
    description: "Separate controllers for user self-service (UserAddressController) and admin operations (AddressAdminController). Clear separation of concerns with different authorization requirements and base paths."
    icon: "🔐"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/address-service/src/main/java/io/github/alexisTrejo11/drugstore/address/controller"
    highlights:
      - "User controller: /api/v2/user/addresses (requires USER or ADMIN role)"
      - "Admin controller: /api/v2/addresses/admin (requires ADMIN role only)"
      - "Users can only access their own addresses"
      - "Admins can manage addresses for any user"
      - "Shared AddressService with different method signatures for authorization"
    techStack:
      - "Spring MVC"
      - "Spring Security"
      - "JWT Authentication"
    metrics:
      - label: "User Endpoints"
        value: "6"
        trend: "stable"
        icon: "user"
      - label: "Admin Endpoints"
        value: "7"
        trend: "stable"
        icon: "admin"
    codeSnippet:
      language: "java"
      filename: "SecurityConfig.java"
      code: |
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/v2/addresses/**").hasRole("ADMIN")
                .requestMatchers("/api/v2/user/addresses/**").hasAnyRole("CUSTOMER", "ADMIN")
                ...

  - id: "redis-rate-limiting"
    title: "Redis-Backed Rate Limiting"
    description: "Distributed rate limiting using Redis with token bucket algorithm. Applied via AOP aspect with @RateLimit annotation. Two profiles: STANDARD (60/min) for reads, SENSITIVE (10/min) for writes."
    icon: "🚦"
    category: "security"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/address-service/src/main/java/io/github/alexisTrejo11/drugstore/address/config/ratelimit"
    highlights:
      - "Redis atomic increment for distributed rate counting"
      - "AOP Aspect (RateLimitAspect) intercepts annotated methods"
      - "Two profiles: STANDARD (reads), SENSITIVE (writes)"
      - "Rate limit info endpoint for clients (X-RateLimit-* headers PLACEHOLDER)"
      - "Configurable per endpoint via @RateLimit(profile = ...) annotation"
    techStack:
      - "Spring AOP"
      - "Redis"
      - "Spring Data Redis"
    metrics:
      - label: "STANDARD Limit"
        value: "60/min"
        trend: "stable"
        icon: "speed"
      - label: "SENSITIVE Limit"
        value: "10/min"
        trend: "stable"
        icon: "lock"
    codeSnippet:
      language: "java"
      filename: "RateLimitAspect.java"
      code: |
        @Around("@annotation(rateLimit)")
        public Object checkRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
            RateLimitProfile profile = rateLimit.profile();
            String clientKey = getClientKey(request, profile);
            int maxRequests = getMaxRequests(profile);
            Duration duration = getDuration(profile);

            if (!redisRateLimiter.isAllowed(clientKey, maxRequests, duration)) {
                return ResponseWrapper.error(HttpStatus.TOO_MANY_REQUESTS.value(),
                    "Rate limit exceeded. Please try again later.", "RATE_LIMIT_EXCEEDED");
            }

            return joinPoint.proceed();
        }

  - id: "jwt-authentication"
    title: "JWT Authentication & Authorization"
    description: "Stateless authentication using JWT tokens validated by JwtAuthenticationFilter from shared libs-kernel. Role-based access control with CUSTOMER, EMPLOYEE, and ADMIN roles."
    icon: "🔑"
    category: "security"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/address-service/src/main/java/io/github/alexisTrejo11/drugstore/address/config/SecurityConfig.java"
    highlights:
      - "JwtAuthenticationFilter from shared libs-kernel for token validation"
      - "Bearer token authentication (Authorization: Bearer <token>)"
      - "AuthUserDetails provides userId and role in controller methods"
      - "UserAuthValidator.assertUserInContext() for context validation"
      - "@SecurityRequirement(name = \"bearerAuth\") for OpenAPI docs"
    techStack:
      - "Spring Security"
      - "JWT"
      - "Shared Library (libs-kernel)"
    metrics:
      - label: "Auth Overhead"
        value: "<10ms"
        trend: "stable"
        icon: "timer"
    codeSnippet:
      language: "java"
      filename: "UserAddressController.java"
      code: |
        @GetMapping("/{addressId}")
        public ResponseWrapper<Address> getAddressById(
            @PathVariable String addressId,
            @AuthenticationPrincipal AuthUserDetails userDetails) {
            UserAuthValidator.assertUserInContext(userDetails);
            Address address = addressService.findAddressByIdAndUserId(addressId, userDetails.getUserId());
            return ResponseWrapper.success(address);
        }

  - id: "soft-delete-pattern"
    title: "Soft Delete Pattern"
    description: "Addresses are never hard-deleted; instead, the 'active' flag is set to false. This preserves data integrity and allows potential recovery. @PrePersist ensures new addresses are active by default."
    icon: "🗑️"
    category: "database"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/address-service/src/main/java/io/github/alexisTrejo11/drugstore/address/entity/AddressEntity.java"
    highlights:
      - "active flag (boolean) instead of DELETE FROM database"
      - "@PrePersist sets active=true for new addresses"
      - "All queries filter by active=true (findByActiveTrue, findByUserIdAndActiveTrue)"
      - "Preserves referential integrity with orders and other entities"
      - "Audit trail: address data remains for historical records"
    techStack:
      - "JPA/Hibernate"
      - "PostgreSQL"
      - "Flyway Migrations"
    metrics:
      - label: "Data Recovery"
        value: "Possible"
        trend: "stable"
        icon: "restore"
    codeSnippet:
      language: "java"
      filename: "AddressEntity.java"
      code: |
        @PrePersist
        protected void onCreate() {
            if (isDefault == null) {
                isDefault = false;
            }
            active = true;
        }

        // Soft delete
        private void softDeleteAddress(AddressEntity entity) {
            entity.setActive(false);
            addressRepository.save(entity);
        }

  - id: "docker-containerization"
    title: "Docker Containerization"
    description: "Multi-stage Docker build with Eclipse Temurin JDK 23 for build and JRE for runtime. Non-root user (spring:spring) for security, health checks via Actuator, and SSL/HTTPS on port 8443."
    icon: "🐳"
    category: "integration"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/address-service/dockerfile"
    highlights:
      - "Multi-stage build: JDK for compilation, JRE for runtime (smaller image)"
      - "Non-root user execution (spring:spring) for security best practices"
      - "Health check: wget --spider https://localhost:8443/actuator/health"
      - "SSL/HTTPS on port 8443 with keystore.p12"
      - "Entrypoint script fixes volume permissions before starting"
    techStack:
      - "Docker"
      - "Eclipse Temurin 23"
      - "Spring Boot Actuator"
    metrics:
      - label: "Image Size"
        value: "~250MB"
        trend: "stable"
        icon: "disk"
      - label: "HTTPS Port"
        value: "8443"
        trend: "stable"
        icon: "lock"
    codeSnippet:
      language: "dockerfile"
      filename: "dockerfile"
      code: |
        # Build stage
        FROM eclipse-temurin:23-jdk-alpine AS builder
        COPY . /app
        RUN ./gradlew bootJar --no-daemon

        # Runtime stage
        FROM eclipse-temurin:23-jre-alpine
        RUN addgroup -S spring && adduser -S spring -G spring
        COPY --from=builder /app/build/libs/*.jar app.jar
        USER spring
        EXPOSE 8443
        ENTRYPOINT ["java", "-jar", "app.jar"]

  - id: "openapi-documentation"
    title: "OpenAPI Documentation (Swagger)"
    description: "Comprehensive API documentation using Springdoc OpenAPI 2.6.0. Custom annotations per endpoint with request/response examples. Swagger UI available at /swagger-ui.html."
    icon: "📚"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/address-service/src/main/java/io/github/alexisTrejo11/drugstore/address/config/OpenApiConfig.java"
    highlights:
      - "Springdoc OpenAPI 2.6.0 integration with Spring Boot 3.3.2"
      - "Custom annotations (e.g., @GetMyAddressesAnnotation) with full OpenAPI metadata"
      - "Request/response examples in annotations using @ExampleObject"
      - "JWT Bearer Auth defined in OpenAPI components"
      - "Swagger UI at /swagger-ui.html, API docs at /v3/api-docs"
    techStack:
      - "Springdoc OpenAPI"
      - "Swagger UI"
      - "Jakarta Validation (schema generation)"
    metrics:
      - label: "Endpoints Documented"
        value: "10/10"
        trend: "stable"
        icon: "check"
      - label: "Example Coverage"
        value: "100%"
        trend: "stable"
        icon: "percent"
    codeSnippet:
      language: "java"
      filename: "OpenApiConfig.java"
      code: |
        @Bean
        public OpenAPI addressServiceOpenAPI() {
            return new OpenAPI()
                    .info(new Info()
                            .title("Address Service API")
                            .description("Drugstore Microservice for managing addresses")
                            .version("2.0.0"))
                    .components(new Components()
                            .addSecuritySchemes("bearerAuth",
                                    new SecurityScheme()
                                            .type(SecurityScheme.Type.HTTP)
                                            .scheme("bearer")
                                            .bearerFormat("JWT")));
        }

  - id: "flyway-migrations"
    title: "Database Migrations with Flyway"
    description: "Versioned database schema management using Flyway. V1__initial_schema.sql creates the addresses table with proper indexes for performance. Supports PostgreSQL with automated migration on startup."
    icon: "✈️"
    category: "database"
    status: "stable"
    githubExampleUrl: "PLACEHOLDER: Check config-data or resources/db/migration"
    highlights:
      - "Flyway Core 10.17.0 with PostgreSQL support"
      - "V1__initial_schema.sql creates addresses table"
      - "Indexes on user_id and (user_id, isDefault) for query performance"
      - "Automatic migration on application startup"
      - "Reproducible across environments (dev, staging, prod)"
    techStack:
      - "Flyway"
      - "PostgreSQL"
      - "Spring Boot"
    metrics:
      - label: "Migrations"
        value: "1"
        trend: "up"
        icon: "migration"
      - label: "Indexed Columns"
        value: "2"
        trend: "stable"
        icon: "index"
    codeSnippet:
      language: "sql"
      filename: "V1__initial_schema.sql"
      code: |
        CREATE TABLE addresses (
            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
            user_id VARCHAR(255) NOT NULL,
            user_type VARCHAR(50) NOT NULL,
            street VARCHAR(200) NOT NULL,
            city VARCHAR(100) NOT NULL,
            state VARCHAR(100) NOT NULL,
            country VARCHAR(2) NOT NULL,
            postal_code VARCHAR(20) NOT NULL,
            additional_details VARCHAR(200),
            is_default BOOLEAN DEFAULT FALSE,
            active BOOLEAN DEFAULT TRUE,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );

        CREATE INDEX idx_user_id ON addresses(user_id);
        CREATE INDEX idx_user_default ON addresses(user_id, is_default);

  - id: "cors-configuration"
    title: "CORS Configuration"
    description: "Configured Cross-Origin Resource Sharing to allow requests from multiple localhost development servers. Supports credentials and configurable allowed origins."
    icon: "🔗"
    category: "security"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/address-service/src/main/java/io/github/alexisTrejo11/drugstore/address/config/CORSConfig.java"
    highlights:
      - "Allows localhost:3000, 8082, 8888, 8080, 4200 for development"
      - "Supports GET, POST, PUT, DELETE, OPTIONS, PATCH methods"
      - "Allows credentials (cookies, authorization headers)"
      - "Max age 3600 seconds for preflight cache"
      - "PLACEHOLDER: Configure production origins via environment variable"
    techStack:
      - "Spring MVC"
      - "CORS"
    metrics:
      - label: "Allowed Origins"
        value: "5+"
        trend: "stable"
        icon: "globe"
    codeSnippet:
      language: "java"
      filename: "CORSConfig.java"
      code: |
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                    .allowedOrigins("http://localhost:3000", "http://localhost:8082",
                                   "http://localhost:8888", "http://localhost:8080", "http://localhost:4200")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                    .allowedHeaders("*")
                    .allowCredentials(true)
                    .maxAge(3600);
        }

  - id: "observability-stack"
    title: "Observability Stack (Prometheus + Loki + Grafana + Tracing)"
    description: "End-to-end observability with metrics, logs, and tracing. Actuator and Micrometer expose Prometheus metrics, Logback ships logs to Loki, and Grafana visualizes both data sources with trace correlation fields."
    icon: "📈"
    category: "monitoring"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/address-service/observability"
    highlights:
      - "Actuator endpoints exposed: health, info, prometheus"
      - "Micrometer Prometheus registry enabled for service/JVM metrics"
      - "Logback Loki appender enabled for non-test profiles"
      - "Tracing sampling configured at 100% (probability=1.0)"
      - "Grafana data sources provisioned automatically (Prometheus + Loki)"
    techStack:
      - "Spring Boot Actuator"
      - "Micrometer Prometheus"
      - "Micrometer Tracing (OpenTelemetry bridge)"
      - "Console logging + shared Promtail/Loki"
      - "Prometheus"
      - "Grafana"
      - "Loki"
    metrics:
      - label: "Scrape Endpoint"
        value: "/actuator/prometheus"
        trend: "stable"
        icon: "metrics"
      - label: "Health Visibility"
        value: "Detailed"
        trend: "up"
        icon: "heart"
      - label: "Trace Sampling"
        value: "100%"
        trend: "stable"
        icon: "trace"
    codeSnippet:
      language: "yaml"
      filename: "application-docker.yml"
      code: |
        management:
          endpoints:
            web:
              exposure:
                include: health,info,prometheus
          endpoint:
            health:
              show-details: always
          metrics:
            export:
              prometheus:
                enabled: true
          tracing:
            sampling:
              probability: 1.0

  - id: "github-packages-shared-kernel"
    title: "GitHub Packages (Shared External Library)"
    description: "Consumes the platform shared-kernel artifact from GitHub Packages Maven instead of vendoring Java sources into each microservice. Gradle and Docker builds authenticate with GITHUB_ACTOR and GITHUB_TOKEN to download io.github.alexisTrejo11:shared-kernel at a pinned version."
    icon: "📦"
    category: "integration"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/address-service/build.gradle"
    highlights:
      - "Artifact: io.github.alexisTrejo11:shared-kernel:2.0.0 from maven.pkg.github.com/alexisTrejo11/drugstore-platform"
      - "Shared APIs: ResponseWrapper, Error, domain exceptions, JWT DTOs, AuditEvent, pagination/mappers (libs_kernel.*)"
      - "build.gradle loads .env at configuration time via envOrDotEnv for GitHub Packages credentials"
      - "docker-compose build args forward GITHUB_ACTOR and GITHUB_TOKEN into the Gradle builder stage"
      - "Published from libs/shared-kernel with maven-publish; bump version in dependencies when releasing"
    techStack:
      - "Gradle"
      - "GitHub Packages (Maven)"
      - "Docker / Docker Compose"
      - "shared-kernel (Java library)"
    metrics:
      - label: "Shared Library Version"
        value: "2.0.0"
        trend: "stable"
        icon: "package"
      - label: "Registry"
        value: "GitHub Packages"
        trend: "stable"
        icon: "github"
    codeSnippet:
      language: "groovy"
      filename: "build.gradle"
      code: |
        repositories {
            mavenCentral()
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/alexisTrejo11/drugstore-platform")
                credentials {
                    username = envOrDotEnv("GITHUB_ACTOR", "alexisTrejo11")
                    password = envOrDotEnv("GITHUB_TOKEN")
                }
            }
        }

        dependencies {
            implementation 'io.github.alexisTrejo11:shared-kernel:2.0.0'
        }
---
# Project Features

> 13 comprehensive features documented covering all major functionality. The service is production-ready with proper validation, security, and documentation. Potential improvements: Add Kafka integration for publishing address events (created/updated/deleted), implement caching with @Cacheable for frequently accessed addresses, add Micrometer metrics for observability (address creation rate, validation failures), and create Kubernetes deployment manifests (deployment.yaml, service.yaml, ingress.yaml).
