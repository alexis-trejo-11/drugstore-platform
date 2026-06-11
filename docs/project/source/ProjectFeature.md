---
features:
  # address-service
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
      - "Loki4j Logback Appender"
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
  # auth-service
  - id: "jwt-token-management"
    title: "JWT Access + Refresh Token Management"
    description: "Secure token-based authentication with short-lived access tokens (15min) and long-lived refresh tokens (7 days). TokenFactory creates HMAC-SHA signed JWTs with configurable expiration via JJWT 0.11.5."
    icon: "🎫"
    category: "security"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/auth-service/src/main/java/io/github/alexisTrejo11/drugstore/accounts/auth/adapter/output/security/tokens/factory/TokenFactory.java"
    highlights:
      - "ACCESS tokens: short-lived (15min default), contains role, userId, email, name, phoneNumber"
      - "REFRESH tokens: long-lived (7 days), used to obtain new access tokens"
      - "HMAC-SHA signing with configurable secret key"
      - "TokenFactory creates JWTs + numeric tokens (ACTIVATION, TWO_FA)"
      - "Claims extraction and validation via Jwts.parserBuilder()"
    techStack:
      - "JJWT 0.11.5"
      - "Spring Security"
      - "Java 23"
    metrics:
      - label: "Access Token TTL"
        value: "15min"
        trend: "stable"
        icon: "clock"
      - label: "Refresh Token TTL"
        value: "7 days"
        trend: "stable"
        icon: "calendar"
    codeSnippet:
      language: "java"
      filename: "TokenFactory.java"
      code: |
        private Token createAccessToken(UserClaims userClaims) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", userClaims.role());
            claims.put("userId", userClaims.userId());
            claims.put("type", "access");

            String tokenCode = Jwts.builder()
                .setClaims(claims)
                .setSubject(userClaims.userId())
                .setExpiration(Date.from(Instant.now().plusMillis(expirationMs)))
                .signWith(getSigningKey())
                .compact();

            return new Token(tokenCode, "ACCESS", Duration.ofMillis(expirationMs), expiresAt, userClaims);
        }

  - id: "two-factor-authentication"
    title: "Two-Factor Authentication (TOTP)"
    description: "Enhances security with time-based one-time passwords. Users can enable/disable 2FA, receive validation codes via notification service, and complete 2FA login flow."
    icon: "🔐"
    category: "security"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/auth-service/src/main/java/io/github/alexisTrejo11/drugstore/accounts/auth/core/application/usecase/twofa"
    highlights:
      - "TokenFactory generates 6-digit numeric codes (configurable length)"
      - "Codes expire in 5 minutes (configurable)"
      - "TwoFactorConfigController: enable/disable/send-code endpoints"
      - "TwoFactorLoginUseCase handles 2FA login flow"
      - "Events: TwoFactorEnabledEvent, TwoFactorDisabledEvent published to Kafka"
    techStack:
      - "Spring Boot"
      - "Kafka (events)"
      - "Notification Service (sends codes)"
    metrics:
      - label: "Code Length"
        value: "6 digits"
        trend: "stable"
        icon: "pin"
      - label: "Code Expiration"
        value: "5 min"
        trend: "stable"
        icon: "timer"
    codeSnippet:
      language: "java"
      filename: "TwoFactorConfigController.java"
      code: |
        @PostMapping("{userId}/enable")
        @RateLimit(profile = RateLimitProfile.SENSITIVE)
        public ResponseWrapper<String> enableTwoFactorAuth(@PathVariable String userId) {
            var command = EnableTwoFactorCommand.of(userId);
            authUseCases.enableTwoFactorAuth(command);
            return ResponseWrapper.success("2FA enabled for user: " + userId);
        }

  - id: "oauth2-social-login"
    title: "OAuth2 Social Login"
    description: "Supports authentication via external OAuth2 providers (Google, GitHub, etc.) using Spring OAuth2 Client. CustomOAuth2UserService handles user creation/login."
    icon: "🌐"
    category: "security"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/auth-service/src/main/java/io/github/alexisTrejo11/drugstore/accounts/auth/core/application/CustomOAuth2UserService.java"
    highlights:
      - "Spring OAuth2 Client integration"
      - "CustomOAuth2UserService for user creation/login"
      - "OAuth2AuthenticationSuccessHandler for handling successful auth"
      - "ExternalIDs value object stores provider-specific user IDs"
      - "OAuth2Provider enum: GOOGLE, GITHUB, etc."
    techStack:
      - "Spring OAuth2 Client"
      - "Spring Security"
      - "OAuth2"
    metrics:
      - label: "Supported Providers"
        value: "PLACEHOLDER: 2+"
        trend: "up"
        icon: "providers"
    codeSnippet:
      language: "java"
      filename: "PLACEHOLDER: CustomOAuth2UserService.java"
      code: |
        // OAuth2 user service handles external authentication
        // Creates/updates user in user-service via gRPC
        // Returns AuthUserDetails for Spring Security context

  - id: "password-management"
    title: "Password Management & Reset Flow"
    description: "Comprehensive password handling: change password (authenticated), forgot password (email reset link), reset password (with token), and validate reset token. Uses BCrypt hashing."
    icon: "🔑"
    category: "security"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/auth-service/src/main/java/io/github/alexisTrejo11/drugstore/accounts/auth/core/application/usecase/password"
    highlights:
      - "BCryptPasswordEncoder for secure password hashing"
      - "Forgot password: generates ACTIVATION token, sends email via Kafka event"
      - "Reset password: validates token, updates password in user-service via gRPC"
      - "Change password: requires current password verification"
      - "PasswordChangedEvent published to Kafka for audit/logging"
    techStack:
      - "Spring Security Crypto (BCrypt)"
      - "Kafka (events)"
      - "gRPC (user-service)"
    metrics:
      - label: "BCrypt Strength"
        value: "PLACEHOLDER: Default"
        trend: "stable"
        icon: "lock"
    codeSnippet:
      language: "java"
      filename: "PasswordAuthController.java"
      code: |
        @PostMapping("/forgot")
        @RateLimit(profile = RateLimitProfile.SENSITIVE)
        public ResponseWrapper<Void> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
            ForgotPasswordCommand command = request.toCommand("Unknown IP");
            passwordUseCases.forgotPassword(command);
            return ResponseWrapper.success("Password reset email sent successfully");
        }

  - id: "kafka-event-publishing"
    title: "Kafka Event Publishing"
    description: "Publishes 8+ domain events to Kafka topics for asynchronous inter-service communication. Uses CompletableFuture for non-blocking publishing with timeout."
    icon: "📢"
    category: "messaging"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/auth-service/src/main/java/io/github/alexisTrejo11/drugstore/accounts/auth/adapter/output/messaging/kafka/producer/UserEventProducer.java"
    highlights:
      - "UserRegisteredEvent → user-registered topic"
      - "UserCreatedEvent → user.created topic"
      - "UserUpdatedEvent → user.updated topic"
      - "UserDeletedEvent → user.deleted topic"
      - "UserLoginEvent → user-login topic"
      - "PasswordChangedEvent → auth.password-changed topic"
      - "AccountActivatedEvent → auth.account-activated topic"
      - "TwoFactorEnabled/DisabledEvent → auth.two-factor-* topics"
    techStack:
      - "Spring Kafka"
      - "Kafka Template"
      - "CompletableFuture"
    metrics:
      - label: "Event Types"
        value: "8+"
        trend: "up"
        icon: "events"
      - label: "Publishing Timeout"
        value: "10s"
        trend: "stable"
        icon: "timer"
    codeSnippet:
      language: "java"
      filename: "UserEventProducer.java"
      code: |
        @Override
        public void publishUserRegistered(UserRegisteredEvent event) {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                userRegisteredTopic, event.userId(), event);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("UserRegisteredEvent published successfully");
                } else {
                    log.error("Failed to publish event: {}", ex.getMessage());
                }
            });

            future.get(timeoutSeconds, TimeUnit.SECONDS);
        }

  - id: "grpc-user-service-client"
    title: "gRPC Client for User-Service"
    description: "High-performance Protobuf-based RPC communication with user-service for user CRUD operations. Uses gRPC 1.60.0 and Protobuf 3.25.1."
    icon: "🔌"
    category: "integration"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/auth-service/src/main/java/io/github/alexisTrejo11/drugstore/accounts/auth/adapter/output/grpc/UserServiceGrpcClient.java"
    highlights:
      - "UserServiceGrpcClient using blocking stub for sync calls"
      - "Protobuf messages defined in .proto files"
      - "UserGrpcMapper for Protobuf ↔ Domain object mapping"
      - "CRUD operations: getUserByEmail, getUserById, createUser, updateUser, deleteUser"
      - "PLACEHOLDER: Add Circuit Breaker (Resilience4j) for fault tolerance"
    techStack:
      - "gRPC 1.60.0"
      - "Protobuf 3.25.1"
      - "Spring gRPC"
    metrics:
      - label: "Latency"
        value: "~10ms"
        trend: "stable"
        icon: "speed"
      - label: "Protocol"
        value: "HTTP/2"
        trend: "stable"
        icon: "http2"
    codeSnippet:
      language: "java"
      filename: "UserServiceGrpcClient.java"
      code: |
        public Optional<UserDto> getUserByEmail(String email) {
            try {
                UserRequest request = UserRequest.newBuilder()
                    .setIdentifier(email)
                    .setIdentifierType(IdentifierType.EMAIL)
                    .build();

                UserResponse response = blockingStub.getUser(request);
                return Optional.of(UserGrpcMapper.toDto(response));
            } catch (StatusRuntimeException e) {
                if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                    return Optional.empty();
                }
                throw new UserServiceException("gRPC call failed", e);
            }
        }

  - id: "ddd-ports-adapters"
    title: "DDD Ports & Adapters Architecture"
    description: "Clean bounded context with ports (interfaces) in core/ports and adapters (implementations) in adapter/output. UseCasesOrquestrator coordinates all use cases."
    icon: "🏰"
    category: "architecture"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/auth-service/src/main/java/io/github/alexisTrejo11/drugstore/accounts/auth/core/application/UseCasesOrquestrator.java"
    highlights:
      - "Ports: AuthUseCases, LogoutUseCases, PasswordUseCases, RegisterUseCases, TwoFaConfigUseCases"
      - "Adapters: TokenManager, UserEventProducer, UserServiceGrpcClient, RedisSessionRepository"
      - "UseCasesOrquestrator implements 4 port interfaces"
      - "Command objects: LoginCommand, SignupCommand, ChangePasswordCommand"
      - "Result objects: SessionPayload, SignUpResult, TwoFactorQRResult"
    techStack:
      - "DDD"
      - "Hexagonal Architecture"
      - "Clean Code"
    metrics:
      - label: "Use Cases"
        value: "12+"
        trend: "up"
        icon: "usecase"
      - label: "Port Interfaces"
        value: "5"
        trend: "stable"
        icon: "interface"
    codeSnippet:
      language: "java"
      filename: "UseCasesOrquestrator.java"
      code: |
        @Service
        public class UseCasesOrquestrator
            implements AuthUseCases, LogoutUseCases, PasswordUseCases, RegisterUseCases, TwoFaConfigUseCases {

            @Override
            public SignUpResult register(SignupCommand command) {
                return registerUseCase.execute(command);
            }

            @Override
            public SessionPayload login(LoginCommand command) {
                return loginUseCase.execute(command);
            }
            // ... more delegation methods
        }

  - id: "redis-session-management"
    title: "Redis Session Management"
    description: "Refresh token sessions stored in Redis with configurable TTL. Supports session blacklisting for immediate token revocation. Rate limiting also uses Redis."
    icon: "🗄️"
    category: "persistence"
    status: "stable"
    githubExampleUrl: "PLACEHOLDER: Check adapter/output/persistence/"
    highlights:
      - "RedisSessionRepository stores/retrieves/blacklists sessions"
      - "Sessions keyed by refresh token with user ID mapping"
      - "Blacklisting: expired/invalid tokens rejected immediately"
      - "RedisRateLimiter for distributed rate limiting (token bucket)"
      - "TokenRepository for non-JWT tokens (ACTIVATION, TWO_FA)"
    techStack:
      - "Spring Data Redis"
      - "Redis 7"
      - "Token Bucket Algorithm"
    metrics:
      - label: "Session TTL"
        value: "7 days"
        trend: "stable"
        icon: "clock"
      - label: "Redis Host"
        value: "PLACEHOLDER: Configurable"
        trend: "stable"
        icon: "redis"
    codeSnippet:
      language: "java"
      filename: "PLACEHOLDER: RedisSessionRepository.java"
      code: |
        public void saveSession(String refreshToken, JwtSession session) {
            String key = "session:" + refreshToken;
            redisTemplate.opsForValue().set(key, session,
                jwtProperties.getRefreshTokenExpirationSeconds(), TimeUnit.SECONDS);
        }

        public void blacklistSession(String refreshToken) {
            String blacklistKey = "blacklist:" + refreshToken;
            redisTemplate.opsForValue().set(blacklistKey, "true",
                jwtProperties.getRefreshTokenExpirationSeconds(), TimeUnit.SECONDS);
        }

  - id: "value-objects-pattern"
    title: "Value Objects Pattern"
    description: "Domain values wrapped in objects for validation: Email, Password (BCrypt hashed), UserId, UserRole (enum), Token, SessionId, PhoneNumber, OAuth2Provider, ExternalIDs."
    icon: "🧱"
    category: "domain"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/auth-service/src/main/java/io/github/alexisTrejo11/drugstore/accounts/auth/core/domain/valueobjects"
    highlights:
      - "Email value object validates email format"
      - "Password value object wraps BCrypt hashed password"
      - "UserRole enum: CUSTOMER, EMPLOYEE, ADMIN"
      - "OAuth2Provider enum: GOOGLE, GITHUB, etc."
      - "PhoneNumber with validation"
    techStack:
      - "DDD Value Objects"
      - "Validation Logic"
    metrics:
      - label: "Value Objects"
        value: "8+"
        trend: "stable"
        icon: "object"
    codeSnippet:
      language: "java"
      filename: "Email.java"
      code: |
        public record Email(String value) {
            public Email {
                validateEmail(value);
                this.value = value.toLowerCase().trim();
            }

            private void validateEmail(String email) {
                if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                    throw new IllegalArgumentException("Invalid email format");
                }
            }
        }

  - id: "multi-role-registration"
    title: "Multi-Role User Registration"
    description: "Supports registration for different user roles: CUSTOMER (default), EMPLOYEE, and ADMIN. Each role has separate registration endpoint with role assignment."
    icon: "👥"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/auth-service/src/main/java/io/github/alexisTrejo11/drugstore/accounts/auth/adapter/input/web/controller/RegisterController.java"
    highlights:
      - "POST /api/v2/auth/register/customer - Creates CUSTOMER role"
      - "POST /api/v2/auth/register/employee - Creates EMPLOYEE role"
      - "POST /api/v2/auth/register/admin - Creates ADMIN role"
      - "Role assigned via SignupCommand with UserRole enum"
      - "Account activation required via email token (ACTIVATION type)"
    techStack:
      - "Spring MVC"
      - "DDD Commands"
      - "Role-Based Access"
    metrics:
      - label: "Supported Roles"
        value: "3"
        trend: "stable"
        icon: "roles"
    codeSnippet:
      language: "java"
      filename: "RegisterController.java"
      code: |
        @PostMapping("/register/customer")
        @RateLimit(profile = RateLimitProfile.SENSITIVE)
        public ResponseEntity<ResponseWrapper<SignUpResponse>> registerCustomer(
                @RequestBody @Valid SignupRequest request) {
            SignupCommand command = request.toCommand(UserRole.CUSTOMER);
            SignUpResult result = useCases.register(command);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseWrapper.created(response, "Customer User"));
        }

  - id: "http-integration-testing"
    title: "HTTP integration tests (Testcontainers)"
    description: "Spring Boot tests spin up real Redis and Kafka via Testcontainers and an in-process Netty gRPC UserService with BCrypt validation. Covers registration→activation→login, refresh/logout, password reset, 2FA, and authenticated routes."
    icon: "🧪"
    category: "quality"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/auth-service/src/test/java/io/github/alexisTrejo11/drugstore/accounts/integration/AuthEndpointsIntegrationTest.java"
    highlights:
      - "Profiles: integration-test + test (JWT test secret, relaxed rate limits, Logback excludes Loki)"
      - "Opaque tokens read from Redis (ACTIVATION, PASSWORD_RESET, TWO_FA) for realistic flows"
      - "InMemoryUserGrpcServer implements full UserService contract used by UserServiceGrpcClient"
      - "@Testcontainers(disabledWithoutDocker = true) skips when Docker is not installed"
    techStack:
      - "JUnit 5"
      - "Testcontainers"
      - "Spring Boot Test"
    metrics:
      - label: "Primary test class"
        value: "AuthEndpointsIntegrationTest"
        trend: "stable"
        icon: "code"
      - label: "Infra in tests"
        value: "Redis+Kafka"
        trend: "stable"
        icon: "docker"
    codeSnippet:
      language: "java"
      filename: "AuthEndpointsIntegrationTest.java"
      code: |
        @SpringBootTest(classes = AuthServiceApplication.class,
            webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
        @ActiveProfiles({"integration-test", "test"})
        @Testcontainers(disabledWithoutDocker = true)
        class AuthEndpointsIntegrationTest {
          // Testcontainers Redis + Kafka; @DynamicPropertySource wires ports;
          // Netty gRPC server + InMemoryUserGrpcServer for user-service
        }


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
  # cart-service
  - id: "cart-aggregate-root"
    title: "Cart Aggregate Root"
    description: "Cart.java is the aggregate root encapsulating items and afterwardsItems with business logic for add/update/remove/clear operations. Enforces invariants like max 100 unique items."
    icon: "🏰"
    category: "domain"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/cart-service/src/main/java/io/github/alexisTrejo11/drugstore/carts/cart/core/domain/model/Cart.java"
    highlights:
      - "Cart.create() and Cart.reconstruct() factory methods"
      - "addItem() with quantity merging for existing products"
      - "updateItemQuantity() for changing item quantities"
      - "removeItem() and removeItems() for item removal"
      - "clear() with optional exclusion list for checkout"
      - "moveItemsToAfterwards() and returnItemsFromAfterwards() for save-for-later"
      - "calculateTotal(), calculateSubtotal(), calculateDiscount() for price calculations"
    techStack:
      - "DDD Aggregate Root"
      - "Domain Model"
      - "Business Logic Encapsulation"
    metrics:
      - label: "Max Items/Cart"
        value: "100"
        trend: "stable"
        icon: "cart"
      - label: "Business Methods"
        value: "15+"
        trend: "stable"
        icon: "methods"
    codeSnippet:
      language: "java"
      filename: "Cart.java"
      code: |
        public void addItem(CartItem item) {
            Optional<CartItem> existingItem = findItemByProductId(item.getProductId());
            if (existingItem.isPresent()) {
                existingItem.get().mergeWith(item);
            } else {
                validateCanAddItem();
                items.add(item);
            }
            timeStamps.markAsUpdated();
        }

  - id: "afterwards-save-for-later"
    title: "Afterwards (Save-for-Later)"
    description: "Separate list for items saved for later with move-to-afterwards and restore-from-afterwards operations. Managed within Cart aggregate root."
    icon: "📥"
    category: "feature"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/cart-service/src/main/java/io/github/alexisTrejo11/drugstore/carts/cart/core/domain/model/AfterwardsItem.java"
    highlights:
      - "AfterwardsItem created from CartItem via createFromItem()"
      - "moveItemsToAfterwards() moves items from cart to afterwards"
      - "returnItemsFromAfterwards() restores items back to cart"
      - "Both lists maintained in Cart aggregate root"
      - "REST endpoints: /move-to-afterwards, /restore-from-afterwards"
    techStack:
      - "DDD Value Objects"
      - "Domain Logic"
    metrics:
      - label: "Use Cases"
        value: "2"
        trend: "stable"
        icon: "usecase"
    codeSnippet:
      language: "java"
      filename: "Cart.java"
      code: |
        public void moveItemsToAfterwards(List<ProductId> productIds) {
            List<CartItem> itemsToMove = items.stream()
                .filter(item -> productIds.contains(item.getProductId()))
                .toList();

            List<AfterwardsItem> afterwardsItemsToAdd = itemsToMove.stream()
                .map(AfterwardsItem::createFromItem)
                .toList();

            this.afterwardsItems.addAll(afterwardsItemsToAdd);
            this.items.removeAll(itemsToMove);
            timeStamps.markAsUpdated();
        }

  - id: "grpc-service-interface"
    title: "gRPC Service Interface"
    description: "CartGrpcService exposes GetUserCart and ClearCart endpoints for order-service integration during checkout flow. Uses Protobuf 3.25.1 and gRPC 1.60.0."
    icon: "🔌"
    category: "integration"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/cart-service/src/main/java/io/github/alexisTrejo11/drugstore/carts/cart/adapter/input/grpc/CartGrpcService.java"
    highlights:
      - "GetUserCart - Returns cart for order-service during checkout"
      - "ClearCart - Clears cart after successful order (with exclusion list)"
      - "Uses CartGrpcMapper for Protobuf ↔ Domain mapping"
      - "Spring Boot gRPC integration with @GrpcService annotation"
    techStack:
      - "gRPC 1.60.0"
      - "Protobuf 3.25.1"
      - "Spring gRPC"
    metrics:
      - label: "gRPC Methods"
        value: "2"
        trend: "stable"
        icon: "grpc"
      - label: "Protocol"
        value: "HTTP/2"
        trend: "stable"
        icon: "http2"
    codeSnippet:
      language: "java"
      filename: "CartGrpcService.java"
      code: |
        @GrpcService
        public class CartGrpcService extends CartServiceGrpc.CartServiceImplBase {
            @Override
            public void getUserCart(GetUserCartRequest request,
                                    StreamObserver<CartResponse> responseObserver) {
                GetCartByCustomerIdQuery query =
                    GetCartByCustomerIdQuery.from(request.getUserId());
                Cart cart = cartQueryUseCase.getCartByCustomerId(query);
                CartResponse response = mapper.toGrpcResponse(cart);
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        }

  - id: "kafka-product-events"
    title: "Kafka Product Events Consumption"
    description: "ProductEventConsumer listens to product-events topic and updates cart items via ProductEventHandler. Handles price changes and product availability updates."
    icon: "📢"
    category: "messaging"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/cart-service/src/main/java/io/github/alexisTrejo11/drugstore/carts/product/adapter/input/message/kafka/ProductEventConsumer.java"
    highlights:
      - "Listens to product-events topic with @KafkaListener"
      - "ProductEventHandler updates cart items on price changes"
      - "Handles product deletion (remove from carts)"
      - "Eventually consistent cart data with latest product info"
    techStack:
      - "Spring Kafka"
      - "Kafka"
      - "Event-Driven"
    metrics:
      - label: "Kafka Topic"
        value: "product-events"
        trend: "stable"
        icon: "topic"
      - label: "Consumer Group"
        value: "PLACEHOLDER: ${spring.kafka.consumer.group-id}"
        trend: "stable"
        icon: "group"
    codeSnippet:
      language: "java"
      filename: "ProductEventConsumer.java"
      code: |
        @KafkaListener(topics = "product-events",
                      groupId = "${spring.kafka.consumer.group-id}")
        public void consume(ProductEvent event) {
            log.info("Received product event: type={}, id={}",
                      event.getEventType(), event.getPayload().getId());
            eventHandler.handle(event);
        }

  - id: "value-objects-pattern"
    title: "Value Objects Pattern"
    description: "Strongly-typed values: CartId (UUID), CustomerId, ProductId, Quantity (with validation), ItemPrice (BigDecimal wrapper), CartTimeStamps."
    icon: "🧱"
    category: "domain"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/cart-service/src/main/java/io/github/alexisTrejo11/drugstore/carts/cart/core/domain/model/valueobjects"
    highlights:
      - "CartId - UUID generation via CartId.generate()"
      - "CustomerId - Wraps customer identifier"
      - "ProductId - Wraps product identifier"
      - "Quantity - Validates non-negative, non-zero values"
      - "ItemPrice - BigDecimal wrapper with arithmetic operations"
      - "CartTimeStamps - Created/updated/deleted timestamps with markAsUpdated()"
    techStack:
      - "DDD Value Objects"
      - "Type Safety"
      - "Validation"
    metrics:
      - label: "Value Objects"
        value: "6+"
        trend: "stable"
        icon: "object"
    codeSnippet:
      language: "java"
      filename: "Quantity.java"
      code: |
        public record Quantity(int value) {
            public Quantity {
                if (value < 0) {
                    throw new InvalidQuantityException("Quantity cannot be negative");
                }
                if (value == 0) {
                    throw new InvalidQuantityException("Quantity cannot be zero");
                }
            }
            public static Quantity of(int value) {
                return new Quantity(value);
            }
        }

  - id: "ddd-ports-adapters"
    title: "DDD Ports & Adapters Architecture"
    description: "Clean bounded context with ports (CartRepository, CartCommandUseCase, CartQueryUseCase) and adapters (CartJpaRepository, CartCommandUseCaseImpl)."
    icon: "🔌"
    category: "architecture"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/cart-service/src/main/java/io/github/alexisTrejo11/drugstore/carts/cart/core/port"
    highlights:
      - "Ports: CartRepository, CartCommandUseCase, CartQueryUseCase"
      - "Adapters: CartJpaRepository, CartCommandUseCaseImpl, CartQueryUseCaseImpl"
      - "Command Use Cases: CreateCart, UpdateCartItems, MoveToAfterwards, ReturnFromAfterwards, ClearCart"
      - "Query Use Cases: GetCartById, GetCartByCustomerId, SearchCarts"
    techStack:
      - "DDD"
      - "Hexagonal Architecture"
      - "Clean Code"
    metrics:
      - label: "Use Cases"
        value: "8+"
        trend: "up"
        icon: "usecase"
      - label: "Port Interfaces"
        value: "3"
        trend: "stable"
        icon: "interface"
    codeSnippet:
      language: "java"
      filename: "CartCommandUseCase.java"
      code: |
        public interface CartCommandUseCase {
            void createCart(CreateCartCommand command);
            Cart updateCartItems(UpdateCartCommand command);
            void moveItemToAfterwards(CreateAfterwardsCommand command);
            void removeItemFromAfterwards(RemoveAfterwardsCommand command);
            void clearCart(ClearCartCommand command);
        }

  - id: "command-pattern"
    title: "Command Pattern for Cart Operations"
    description: "Command objects: CreateCartCommand, UpdateCartCommand, ClearCartCommand, CreateAfterwardsCommand, RemoveAfterwardsCommand."
    icon: "📋"
    category: "design-pattern"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/cart-service/src/main/java/io/github/alexisTrejo11/drugstore/carts/cart/core/application/command"
    highlights:
      - "CreateCartCommand - For creating new cart"
      - "UpdateCartCommand - For updating cart items"
      - "ClearCartCommand - For clearing cart (with reason and exclusions)"
      - "CreateAfterwardsCommand - For moving items to afterwards"
      - "RemoveAfterwardsCommand - For restoring items from afterwards"
    techStack:
      - "Command Pattern"
      - "CQRS-like"
      - "DDD"
    metrics:
      - label: "Command Types"
        value: "5+"
        trend: "stable"
        icon: "command"
    codeSnippet:
      language: "java"
      filename: "UpdateCartCommand.java"
      code: |
        public record UpdateCartCommand(
            CustomerId customerId,
            List<CartItem> items
        ) {
            public static UpdateCartCommand from(String customerId,
                                                  List<CartItemRequest> requests) {
                // Converts requests to domain objects
                return new UpdateCartCommand(...);
            }
        }

  - id: "repository-pattern"
    title: "Repository Pattern with Specifications"
    description: "CartRepository port with CartJpaRepository and CartRepositoryImpl using Specification for dynamic search queries."
    icon: "🗄️"
    category: "persistence"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/cart-service/src/main/java/io/github/alexisTrejo11/drugstore/carts/cart/adapter/output/persistence/repository/CartJpaRepository.java"
    highlights:
      - "CartJpaRepository - Spring Data JPA with entity graph for items/afterwards"
      - "CartRepositoryImpl - Implementation with search using Specification"
      - "CartSpecificationBuilder - Dynamic query building for search"
      - "Entity Graph: CartWithItems, CartWithItemsAndAfterwards for eager loading"
    techStack:
      - "Spring Data JPA"
      - "PostgreSQL 15"
      - "Flyway Migrations"
    metrics:
      - label: "Entity Graphs"
        value: "2"
        trend: "stable"
        icon: "graph"
      - label: "Indexes"
        value: "3"
        trend: "stable"
        icon: "index"
    codeSnippet:
      language: "java"
      filename: "CartJpaRepository.java"
      code: |
        @EntityGraph(attributePaths = {"cartItems", "afterwardItems"})
        Optional<CartModel> findByIdAndCustomerId(String id, String customerId);

        @EntityGraph(value = "CartWithItems")
        Optional<CartModel> findById(String id);

        Page<CartModel> findAll(
            Specification<CartModel> spec, Pageable pageable);

  - id: "redis-caching"
    title: "Redis Caching with Spring Cache"
    description: "Spring Cache abstraction with Redis backend for frequently accessed cart data. Configured via RedisCacheConfig."
    icon: "🗃️"
    category: "cache"
    status: "stable"
    githubExampleUrl: "PLACEHOLDER: Check config/cache/RedisCacheConfig.java"
    highlights:
      - "Spring Cache annotations (@Cacheable, @CacheEvict)"
      - "Redis backend via Spring Data Redis"
      - "Configurable TTL via ADDRESS_SERVICE_CACHE_TTL (default 3600s)"
      - "PLACEHOLDER: @Cacheable annotations needed on repository methods"
    techStack:
      - "Spring Cache"
      - "Redis 7"
      - "Spring Data Redis"
    metrics:
      - label: "Cache TTL"
        value: "3600s"
        trend: "stable"
        icon: "clock"
    codeSnippet:
      language: "java"
      filename: "PLACEHOLDER: RedisCacheConfig.java"
      code: |
        // PLACEHOLDER: Configure Spring Cache with Redis
        // @Bean
        // public RedisCacheManager cacheManager() { ... }

  - id: "rest-controllers"
    title: "REST Controllers (User + Admin)"
    description: "UserCartController for customer operations and CartManagerController for admin operations. Uses ResponseWrapper from libs-kernel."
    icon: "🔌"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/cart-service/src/main/java/io/github/alexisTrejo11/drugstore/carts/cart/adapter/input/web/controller"
    highlights:
      - "UserCartController - /api/v2/carts/users/* (requires USER role)"
      - "CartManagerController - /api/v2/carts/admin/* (requires ADMIN role)"
      - "Endpoints: getMyCart, updateItems, moveToAfterwards, restoreFromAfterwards"
      - "Admin endpoints: getCustomerCart, getCartById, searchCarts"
      - "PLACEHOLDER: No @RateLimit annotations on controllers"
    techStack:
      - "Spring MVC"
      - "Spring Security"
      - "libs-kernel (ResponseWrapper)"
    metrics:
      - label: "User Endpoints"
        value: "4"
        trend: "stable"
        icon: "user"
      - label: "Admin Endpoints"
        value: "3"
        trend: "stable"
        icon: "admin"
    codeSnippet:
      language: "java"
      filename: "UserCartController.java"
      code: |
        @RestController
        @RequestMapping("/api/v2/carts/users")
        public class UserCartController {
            @GetMapping("/my-cart")
            public ResponseWrapper<CartResponse> getMyCart(
                    @RequestAttribute("userId") String userId) {
                // Returns cart for authenticated user
            }
        }

  - id: "domain-events"
    title: "Domain Events (PLACEHOLDER)"
    description: "CartPurchasedEvent is defined but not published to Kafka yet. Should be published when cart is cleared after successful order."
    icon: "📢"
    category: "events"
    status: "PLACEHOLDER"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/blob/main/cart-service/src/main/java/io/github/alexisTrejo11/drugstore/carts/cart/core/domain/events/CartPurchasedEvent.java"
    highlights:
      - "CartPurchasedEvent - Should be published to cart.purchased topic"
      - "Domain events collected in Cart aggregate via domainEvents list"
      - "PLACEHOLDER: Event publishing not implemented yet"
      - "Would notify other services (e.g., inventory-service) of cart purchase"
    techStack:
      - "Domain Events"
      - "Kafka (PLACEHOLDER)"
    metrics:
      - label: "Event Types"
        value: "1 (PLACEHOLDER)"
        trend: "up"
        icon: "event"
    codeSnippet:
      language: "java"
      filename: "CartPurchasedEvent.java"
      code: |
        // PLACEHOLDER: This event should be published to Kafka
        // when cart is cleared after successful order
        public class CartPurchasedEvent implements DomainEvent {
            // Event data: cartId, customerId, items, total
        }
  # employee-service
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
  # inventory-service
  - id: "inventory-management"
    title: "Inventory Management"
    description: "Core inventory tracking with total, available, and reserved quantity management. Supports reorder levels, maximum stock levels, and warehouse location tracking."
    icon: "inventory"
    category: "database"
    status: "stable"
    githubExampleUrl: "PLACEHOLDER"
    highlights:
      - "Track total, available, and reserved quantities"
      - "Reorder level and quantity thresholds"
      - "Warehouse location mapping"
      - "Inventory status management (ACTIVE, INACTIVE, OUT_OF_STOCK)"
      - "CQRS pattern with separate query/command models"
    techStack:
      - "Spring Data JPA"
      - "PostgreSQL"
      - "Flyway (disabled)"
      - "Lombok"
    metrics:
      - label: "Products Tracked"
        value: "PLACEHOLDER"
        trend: "stable"
        icon: "inventory"
      - label: "Avg Query Time"
        value: "PLACEHOLDER"
        trend: "stable"
        icon: "speed"

  - id: "batch-tracking"
    title: "Batch Tracking & Expiration Management"
    description: "Pharmaceutical-grade batch tracking with lot numbers, manufacturing/expiration dates, supplier info, and storage conditions. Supports batch status lifecycle (ACTIVE, EXPIRED, DAMAGED, QUARANTINED)."
    icon: "batch"
    category: "database"
    status: "stable"
    githubExampleUrl: "PLACEHOLDER"
    highlights:
      - "Unique batch numbers per inventory"
      - "Lot number tracking for compliance"
      - "Expiration date monitoring with alerting"
      - "Batch status lifecycle management"
      - "Storage conditions tracking"
      - "Supplier information linkage"
    techStack:
      - "JPA Entity Relationships"
      - "BatchStatus Enum"
      - "ISO DateTime formatting"
      - "Paginated queries"
    metrics:
      - label: "Active Batches"
        value: "PLACEHOLDER"
        trend: "up"
        icon: "batch"
      - label: "Expiring Soon (30d)"
        value: "PLACEHOLDER"
        trend: "stable"
        icon: "warning"

  - id: "stock-reservations"
    title: "Stock Reservation System"
    description: "Temporary stock reservation system for order processing. Supports reserve, confirm, release, and cancel operations with reason tracking."
    icon: "reservation"
    category: "api"
    status: "stable"
    githubExampleUrl: "PLACEHOLDER"
    highlights:
      - "Reserve stock for pending orders"
      - "Confirm reservations upon order confirmation"
      - "Release reservations with reason tracking"
      - "Cancel reservations entirely"
      - "Query active reservations per inventory"
      - "ReservationUseCase port interface"
    techStack:
      - "REST API"
      - "CQRS Commands/Queries"
      - "ResponseWrapper from shared kernel"
      - "ReservationId value object"
    metrics:
      - label: "Active Reservations"
        value: "PLACEHOLDER"
        trend: "stable"
        icon: "lock"
      - label: "Avg Reservation Time"
        value: "PLACEHOLDER"
        trend: "stable"
        icon: "timer"

  - id: "stock-movements"
    title: "Stock Movements (Adjustments & Transfers)"
    description: "Comprehensive stock movement tracking with adjustments (damage, loss, correction) and transfers between inventory locations. Full audit trail via InventoryMovement entities."
    icon: "movement"
    category: "api"
    status: "stable"
    githubExampleUrl: "PLACEHOLDER"
    highlights:
      - "Stock adjustments with reason codes"
      - "Inter-location inventory transfers"
      - "Date-range filtered movement history"
      - "Paginated movement queries"
      - "Audit trail via InventoryMovement entities"
      - "ISO DateTime format support"
    techStack:
      - "Spring MVC"
      - "StockMovementUseCase port"
      - "AdjustmentId value object"
      - "MovementResponse DTO"
    metrics:
      - label: "Daily Movements"
        value: "PLACEHOLDER"
        trend: "up"
        icon: "trending_up"
      - label: "Transfer Operations"
        value: "PLACEHOLDER"
        trend: "stable"
        icon: "swap_horiz"

  - id: "expiration-alerts"
    title: "Expiration Alerts & Batch Lifecycle"
    description: "Proactive expiration monitoring with configurable day thresholds. Batch lifecycle management including mark-as-expired, mark-as-damaged, and quarantine operations."
    icon: "notifications"
    category: "monitoring"
    status: "beta"
    githubExampleUrl: "PLACEHOLDER"
    highlights:
      - "Configurable expiration threshold (default 30 days)"
      - "Expiring batches query endpoint"
      - "Mark batch as expired"
      - "Mark batch as damaged"
      - "Quarantine batch for quality control"
      - "Performed-by user tracking"
    techStack:
      - "Spring Scheduled Tasks (PLACEHOLDER)"
      - "BatchUseCase port"
      - "MarkBatchAsExpiredCommand"
      - "UserId value object"
    metrics:
      - label: "Batches Near Expiry"
        value: "PLACEHOLDER"
        trend: "up"
        icon: "warning"
      - label: "Quarantined Batches"
        value: "PLACEHOLDER"
        trend: "stable"
        icon: "quarantine"

  - id: "rabbitmq-messaging"
    title: "RabbitMQ Event Publishing"
    description: "Asynchronous event publishing via RabbitMQ (AMQP). Publishes inventory events for other services. NOTE: This creates inconsistency as other services use Kafka."
    icon: "rabbitmq"
    category: "messaging"
    status: "beta"
    githubExampleUrl: "PLACEHOLDER"
    highlights:
      - "AMQP messaging with spring-boot-starter-amqp"
      - "Inventory event publishing"
      - "Decoupled service communication"
      - "**WARNING**: Inconsistent with Kafka-using services"
    techStack:
      - "Spring AMQP"
      - "RabbitTemplate"
      - "AMQP Messages"
    metrics:
      - label: "Events Published"
        value: "PLACEHOLDER"
        trend: "stable"
        icon: "send"
      - label: "Message Queue Depth"
        value: "PLACEHOLDER"
        trend: "stable"
        icon: "queue"

  - id: "caching-layer"
    title: "Redis Caching"
    description: "Spring Cache abstraction with Redis backend. 1-hour TTL for general cache with statistics enabled. Used for inventory queries and rate limiting."
    icon: "cache"
    category: "caching"
    status: "stable"
    githubExampleUrl: "PLACEHOLDER"
    highlights:
      - "Redis cache with 1-hour TTL"
      - "Lettuce connection pooling (max 8 active)"
      - "Cache statistics enabled"
      - "Null value caching disabled"
      - "Environment variable configuration"
    techStack:
      - "Spring Cache"
      - "Redis"
      - "Lettuce"
      - "Spring Data Redis"
    metrics:
      - label: "Cache Hit Rate"
        value: "PLACEHOLDER"
        trend: "up"
        icon: "speed"
      - label: "Cached Items"
        value: "PLACEHOLDER"
        trend: "stable"
        icon: "storage"

  - id: "rate-limiting"
    title: "Rate Limiting"
    description: "Global and endpoint-specific rate limiting using custom RateLimit filter. Protects against abuse with configurable request thresholds."
    icon: "speed"
    category: "security"
    status: "stable"
    githubExampleUrl: "PLACEHOLDER"
    highlights:
      - "Global rate limit: 1000 requests/hour"
      - "Default endpoint limit: 100 requests/minute"
      - "Auth endpoints: 10 requests/minute"
      - "Orders endpoint: 50 requests/30 seconds"
      - "Health endpoint: 5 requests/10 seconds"
      - "Redis-backed rate limiter"
    techStack:
      - "Spring Filter"
      - "Redis counters"
      - "libs-kernel RateLimit"
    metrics:
      - label: "Blocked Requests"
        value: "PLACEHOLDER"
        trend: "stable"
        icon: "block"
      - label: "Rate Limit Violations"
        value: "PLACEHOLDER"
        trend: "down"
        icon: "trending_down"

  - id: "actuator-monitoring"
    title: "Spring Boot Actuator & Monitoring"
    description: "Comprehensive monitoring with Actuator endpoints (health, info, metrics, env, prometheus). Exposes Redis, DB, and disk space health indicators."
    icon: "monitoring"
    category: "monitoring"
    status: "stable"
    githubExampleUrl: "PLACEHOLDER"
    highlights:
      - "Health checks for Redis, DB, disk space"
      - "Prometheus metrics endpoint"
      - "Environment info exposure"
      - "CORS enabled for monitoring tools"
      - "Spring Boot Admin client integration"
    techStack:
      - "Spring Boot Actuator"
      - "Prometheus"
      - "Spring Boot Admin"
      - "Micrometer"
    metrics:
      - label: "Health Check Response"
        value: "PLACEHOLDER"
        trend: "stable"
        icon: "health"
      - label: "Exposed Endpoints"
        value: "5"
        trend: "stable"
        icon: "api"

  - id: "flyway-migrations"
    title: "Flyway Database Migrations"
    description: "Database schema versioning with Flyway. Currently disabled in configuration. Supports PostgreSQL with public schema."
    icon: "database"
    category: "database"
    status: "experimental"
    githubExampleUrl: "PLACEHOLDER"
    highlights:
      - "Flyway Core 10.17.0"
      - "PostgreSQL-specific migrations"
      - "Clean disabled: false (dangerous for prod!)"
      - "**WARNING**: Currently disabled in application.yml"
      - "Migration locations: classpath:db/migration"
    techStack:
      - "Flyway Core"
      - "Flyway PostgreSQL"
      - "SQL Migrations"
    metrics:
      - label: "Migration Status"
        value: "DISABLED"
        trend: "stable"
        icon: "warning"
      - label: "Pending Migrations"
        value: "PLACEHOLDER"
        trend: "stable"
        icon: "pending"

  - id: "openapi-docs"
    title: "OpenAPI / Swagger Documentation"
    description: "Auto-generated API documentation with SpringDoc OpenAPI. Exposes Swagger UI and API docs endpoints for easy API exploration."
    icon: "docs"
    category: "api"
    status: "stable"
    githubExampleUrl: "PLACEHOLDER"
    highlights:
      - "Swagger UI at /swagger-ui.html"
      - "API docs at /api-docs"
      - "Alphabetical sorting for tags and operations"
      - "Packages to scan configuration"
      - "Cache disabled for real-time docs"
    techStack:
      - "SpringDoc OpenAPI 2.7.0"
      - "Swagger UI"
      - "OpenAPI 3.0"
    metrics:
      - label: "Documented Endpoints"
        value: "18"
        trend: "stable"
        icon: "description"
      - label: "API Version"
        value: "v2"
        trend: "stable"
        icon: "tag"

  - id: "integration-testing"
    title: "Integration tests (Spring profile test)"
    description: "End-to-end REST tests with H2, real JWT validation (JwtAuthenticationFilter), Redis/Kafka excluded, rate limiting off. InventoryApiIntegrationTest + IntegrationTestJwtSupport."
    icon: "science"
    category: "quality"
    status: "stable"
    githubExampleUrl: "PLACEHOLDER"
    highlights:
      - "Profile test via src/test/resources/application.yml + application-test.yml"
      - "H2 MODE=PostgreSQL; Hibernate ddl-auto create-drop; Flyway disabled"
      - "Bearer JWT built with same jwt.secret as test YAML (no mocked Security)"
      - "MockMvc + @Transactional rollback per test method"
      - "./gradlew test"
    techStack:
      - "JUnit 5"
      - "Spring Boot Test"
      - "MockMvc"
      - "JJWT (test tokens)"
      - "H2"
    metrics:
      - label: "Primary integration class"
        value: "InventoryApiIntegrationTest"
        trend: "stable"
        icon: "integration_instructions"

# Code Snippet Example for Batch Status Lifecycle
  - codeSnippet:
      language: "java"
      filename: "InventoryBatchEntity.java"
      code: |
        @Enumerated(EnumType.STRING)
        @Column(name = "status", nullable = false)
        private BatchStatus status;

        // BatchStatus enum values: ACTIVE, EXPIRED, DAMAGED, QUARANTINED
  # notification-service
  - id: ""
    title: ""
    description: ""
    icon: ""
    category: "api"
    status: "stable"
    githubExampleUrl: ""
    highlights:
      - ""
    techStack:
      - ""
    
    # FeatureMetric[]
    metrics:
      - label: ""
        value: ""
        trend: "stable"
        icon: ""

    # CodeSnippet
    codeSnippet:
      language: ""
      filename: ""
      code: ""
  # order-service
  - id: "order-create"
    title: "Create Order"
    description: "Create new orders supporting both delivery and store pickup methods with item validation, duplicate detection, and automatic ID generation"
    icon: "plus-circle"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/order-service/src/main/java/microservice/order_service/orders/infrastructure/api/controller/SaleOrderController.java"
    highlights:
      - "Supports delivery and pickup order creation via single endpoint"
      - "Validates items for duplicates and null values"
      - "Enforces currency consistency across service fee, tax, and items"
      - "Publishes OrderCreatedEvent for async processing"
    techStack:
      - "Spring Boot"
      - "Spring Validation"
      - "JPA/Hibernate"
    metrics:
      - label: "Supported Delivery Methods"
        value: "3"
        trend: "stable"
        icon: "truck"
    codeSnippet:
      language: "java"
      filename: "SaleOrderController.java"
      code: |
        @PostMapping(consumes = "application/json")
        public ResponseWrapper<CreateOrderCommandResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
          if (request.deliveryMethod() != null) {
            var command = request.toDeliveryOrderCommand();
            var result = orderService.createDeliveryOrder(command);
            return ResponseWrapper.created(result, "PurchaseOrder");
          }
          var command = request.toPickupOrderCommand();
          var result = orderService.createPickupOrder(command);
          return ResponseWrapper.created(result, "PurchaseOrder");
        }

  - id: "order-search"
    title: "Search Orders"
    description: "Paginated and filtered order search with dynamic criteria including status, delivery method, date ranges, and user ID"
    icon: "search"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/order-service/src/main/java/microservice/order_service/orders/infrastructure/api/controller/SaleOrderController.java"
    highlights:
      - "Dynamic filtering with Specification pattern"
      - "Pagination and sorting support"
      - "Filter by status, delivery method, date range, user ID"
      - "Returns PageResponse with metadata"
    techStack:
      - "Spring Data JPA"
      - "Specification API"
      - "PageResponse wrapper"
    metrics:
      - label: "Filter Criteria"
        value: "6+"
        trend: "stable"
        icon: "filter"

  - id: "order-status-transitions"
    title: "Order Status Management"
    description: "Complete order lifecycle management with state machine validation, supporting confirm, prepare, ship, deliver, pickup, cancel, and return operations"
    icon: "exchange-alt"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/order-service/src/main/java/microservice/order_service/orders/infrastructure/api/controller/SaleOrderStatusController.java"
    highlights:
      - "9 order statuses with validated state transitions"
      - "Role-based access control (ADMIN, EMPLOYEE)"
      - "Automatic timestamp tracking on status changes"
      - "Publishes OrderStatusChangedEvent on transitions"
    techStack:
      - "Spring Boot"
      - "Enum-based state machine"
      - "Domain events"
    metrics:
      - label: "Status States"
        value: "9"
        trend: "stable"
        icon: "state"
      - label: "Transition Rules"
        value: "12+"
        trend: "stable"
        icon: "rules"

  - id: "customer-order-access"
    title: "Customer Order Access"
    description: "Dedicated API for customers to view their own orders with pagination, filtering by status, and detailed order information"
    icon: "user"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/order-service/src/main/java/microservice/order_service/orders/infrastructure/api/controller/UserOrderController.java"
    highlights:
      - "Customers can only access their own orders (enforced by userID)"
      - "Paginated order listing with filtering"
      - "Detailed order view with items and delivery/pickup info"
      - "Role-based: CUSTOMER and ADMIN access"
    techStack:
      - "Spring Security"
      - "Spring Data JPA"
      - "ResponseMapper"

  - id: "order-address-management"
    title: "Delivery Address Management"
    description: "Manage delivery addresses with CRUD operations, validation against order state, and support for multiple addresses per user"
    icon: "map-marker-alt"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/order-service/src/main/java/microservice/order_service/external/address/infrastructure/api/rest/AddressController.java"
    highlights:
      - "Create, read, update, delete delivery addresses"
      - "Address validation for delivery orders"
      - "Prevent deletion of addresses in use by active orders"
      - "Support for building types (HOUSE, APARTMENT, BUSINESS)"
    techStack:
      - "Spring Boot"
      - "JPA/Hibernate"
      - "Bean Validation"
    metrics:
      - label: "Building Types"
        value: "3"
        trend: "stable"
        icon: "building"

  - id: "order-delete"
    title: "Order Deletion"
    description: "Soft and hard delete capabilities for orders with proper validation and audit trail"
    icon: "trash-alt"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/order-service/src/main/java/microservice/order_service/orders/infrastructure/api/controller/SaleOrderController.java"
    highlights:
      - "Soft delete preserves data for audit purposes"
      - "Hard delete for complete removal (admin only)"
      - "Validates order state before deletion"
    techStack:
      - "Spring Boot"
      - "JPA/Hibernate"

  - id: "rate-limiting"
    title: "API Rate Limiting"
    description: "Configurable rate limiting with global and per-profile limits to protect against abuse and ensure fair usage"
    icon: "tachometer-alt"
    category: "security"
    status: "stable"
    highlights:
      - "Global rate limit: 5000 requests per hour"
      - "Per-profile limits: standard (100/60s), sensitive (5/300s), public (1000/60s), admin (50/1s)"
      - "Custom rate limit annotation via @RateLimit"
    techStack:
      - "Spring Boot"
      - "Custom RateLimit annotation"
      - "Redis (planned for distributed rate limiting)"
    metrics:
      - label: "Rate Limit Profiles"
        value: "4"
        trend: "stable"
        icon: "shield-alt"

  - id: "caching"
    title: "Redis Caching"
    description: "Redis-based caching layer for frequently accessed data with decorator pattern implementation"
    icon: "database"
    category: "caching"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/order-service/src/main/java/microservice/order_service/external/users/application/service/decorator/CachingUserServiceDecorator.java"
    highlights:
      - "CachingUserServiceDecorator wraps UserServiceImpl"
      - "Cache frequently accessed user data"
      - "Configurable TTL and eviction policies"
    techStack:
      - "Spring Cache"
      - "Redis"
      - "Decorator Pattern"

  - id: "event-publishing"
    title: "Domain Event Publishing"
    description: "Publish domain events for order creation and status changes to enable event-driven architecture and async processing"
    icon: "bolt"
    category: "messaging"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/order-service/src/main/java/microservice/order_service/orders/domain/models/events"
    highlights:
      - "OrderCreatedEvent with orderId, userID, totalAmount, createdAt"
      - "OrderStatusChangedEvent with oldStatus, newStatus, changedAt"
      - "Events enable integration with other microservices"
    techStack:
      - "Spring Boot"
      - "Domain-Driven Design"
      - "Event Port (EventPublisher)"

  - id: "security"
    title: "Role-Based Security"
    description: "Comprehensive security with HTTPS enforcement, CORS configuration, and role-based access control for customers, employees, and admins"
    icon: "lock"
    category: "security"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/order-service/src/main/java/microservice/order_service/config/SecurityConfig.java"
    highlights:
      - "HTTPS enforced for all requests"
      - "Roles: CUSTOMER (own orders), EMPLOYEE (sale orders), ADMIN (all operations)"
      - "CORS configured for localhost:3000 and production domain"
      - "Bearer token authentication via Spring Security"
    techStack:
      - "Spring Security"
      - "Spring Boot"
      - "JWT (via bearerAuth)"

  - id: "observability"
    title: "Observability and Monitoring"
    description: "Comprehensive monitoring with Spring Boot Actuator, Spring Boot Admin, and ELK stack for centralized logging"
    icon: "chart-line"
    category: "monitoring"
    status: "beta"
    highlights:
      - "Spring Boot Actuator exposes health, metrics, env, loggers endpoints"
      - "Spring Boot Admin for centralized management"
      - "ELK stack: OpenSearch, Logstash, OpenSearch Dashboards"
      - "JSON logging with logstash-logback-encoder"
    techStack:
      - "Spring Boot Actuator"
      - "Spring Boot Admin"
      - "OpenSearch"
      - "Logstash"
      - "Logback"
    metrics:
      - label: "Exposed Endpoints"
        value: "10+"
        trend: "stable"
        icon: "monitoring"

  - id: "api-documentation"
    title: "OpenAPI Documentation"
    description: "Automated API documentation with Swagger UI and OpenAPI 3 annotations for all endpoints"
    icon: "book"
    category: "api"
    status: "stable"
    highlights:
      - "Swagger UI available at /swagger-ui.html"
      - "API docs at /api-docs"
      - "Custom operation annotations for consistent documentation"
      - "Schema examples for request/response objects"
    techStack:
      - "springdoc-openapi-starter-webmvc-ui"
      - "Swagger UI"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/order-service/src/main/java/microservice/order_service/orders/infrastructure/api/annotation"

  - id: "data-migration"
    title: "Database Migrations"
    description: "Versioned database schema management with Flyway for reliable and repeatable migrations"
    icon: "database"
    category: "database"
    status: "stable"
    highlights:
      - "Flyway core and PostgreSQL support"
      - "Migrations located in classpath:db/migration"
      - "Schema validation on migrate enabled"
    techStack:
      - "Flyway"
      - "PostgreSQL"
  # payment-service
  - id: "initiate-payment"
    title: "Initiate Payment"
    description: "Creates a payment in PENDING state, registers a Stripe PaymentIntent, transitions to PROCESSING and returns the clientSecret needed by the frontend to confirm payment."
    icon: "credit-card"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service/src/main/java/io/github/alexistrejo11/drugstore/payments/infrastructure/adapter/input/web/controller/PaymentController.java"
    highlights:
      - "Creates Payment aggregate in PENDING state"
      - "Registers Stripe PaymentIntent via gateway adapter"
      - "Returns clientSecret for frontend Stripe.js confirmation"
      - "Three-step flow: POST /payments → stripe.confirmPayment() → webhook"
    techStack:
      - "Spring Boot"
      - "Stripe API (Planned)"
      - "Domain Events"
    metrics:
      - label: "Payment Methods"
        value: "6"
        trend: "stable"
        icon: "payment"
    codeSnippet:
      language: "java"
      filename: "PaymentController.java"
      code: |
        @PostMapping
        public ResponseEntity<ResponseWrapper<PaymentResponse>> initiatePayment(
            @Valid @RequestBody InitiatePaymentRequest request) {
            PaymentResponse response = paymentService.initiatePayment(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseWrapper.created(response));
        }

  - id: "process-refund"
    title: "Process Refund"
    description: "Processes full or partial refunds for completed payments. Calls Stripe to issue refund, then updates Payment and Sale status accordingly."
    icon: "undo"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service/src/main/java/io/github/alexistrejo11/drugstore/payments/infrastructure/adapter/input/web/controller/PaymentController.java"
    highlights:
      - "Supports full refunds (amount=null) and partial refunds"
      - "Validates refundable amount before processing"
      - "Updates Payment status to REFUNDED or PARTIALLY_REFUNDED"
      - "Updates Sale status accordingly"
    techStack:
      - "Spring Boot"
      - "Stripe API (Planned)"
      - "Domain Validation"

  - id: "stripe-webhook-handler"
    title: "Stripe Webhook Handler"
    description: "Receives and processes Stripe webhook events with signature verification. Handles payment_intent.succeeded, payment_intent.payment_failed, and charge.refunded events."
    icon: "webhook"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service/src/main/java/io/github/alexistrejo11/drugstore/payments/infrastructure/adapter/input/web/controller/StripeWebhookController.java"
    highlights:
      - "Verifies Stripe-Signature header before processing"
      - "Handles payment_intent.succeeded → Payment COMPLETED → Sale CREATED"
      - "Handles payment_intent.payment_failed → Payment FAILED"
      - "Handles charge.refunded → Payment REFUNDED → Sale updated"
      - "Idempotent processing (checks payment.isCompleted() before confirming)"
      - "Always returns 200 to prevent Stripe retries"
    techStack:
      - "Spring Boot"
      - "Stripe SDK (Planned)"
      - "Spring Events"

  - id: "payment-query"
    title: "Payment Query Operations"
    description: "Retrieve payments by ID, order ID, or customer ID. Supports single payment lookup and customer payment history."
    icon: "search"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service/src/main/java/io/github/alexistrejo11/drugstore/payments/infrastructure/adapter/input/web/controller/PaymentController.java"
    highlights:
      - "GET /api/v1/payments/{paymentId} - Single payment lookup"
      - "GET /api/v1/payments/order/{orderId} - Payment by order (one-to-one)"
      - "GET /api/v1/payments/customer/{customerId} - All payments for customer"
      - "Results ordered by creation date descending"
    techStack:
      - "Spring Boot"
      - "Spring Data JPA"
      - "ResponseWrapper"

  - id: "sale-query"
    title: "Sale Query Operations"
    description: "Read-only sale records automatically created from completed payments. Sales represent confirmed business transactions."
    icon: "receipt"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service/src/main/java/io/github/alexistrejo11/drugstore/payments/infrastructure/adapter/input/web/controller/SaleController.java"
    highlights:
      - "GET /api/v1/sales/{saleId} - Single sale lookup with net amount"
      - "GET /api/v1/sales/order/{orderId} - Sale by order ID"
      - "GET /api/v1/sales/customer/{customerId} - All sales for customer"
      - "Includes confirmed, refunded, and cancelled sales"
    techStack:
      - "Spring Boot"
      - "Spring Data JPA"
      - "Domain Events"

  - id: "payment-state-machine"
    title: "Payment State Machine"
    description: "Payment aggregate with 6 states and validated transitions. Enforces business rules for payment lifecycle."
    icon: "exchange-alt"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service/src/main/java/io/github/alexistrejo11/drugstore/payments/core/domain/model/Payment.java"
    highlights:
      - "PENDING → PROCESSING → COMPLETED → (REFUNDED | PARTIALLY_REFUNDED)"
      - "PENDING → CANCELLED"
      - "PROCESSING → FAILED"
      - "Business rule validation in each state transition"
      - "Automatic timestamp tracking (PaymentTimeStamps)"
    techStack:
      - "Java 23"
      - "Domain-Driven Design"
      - "Enum-based State Machine"

  - id: "sale-state-machine"
    title: "Sale State Machine"
    description: "Sale aggregate with 4 states, automatically generated from completed payments. Represents confirmed business transactions."
    icon: "chart-line"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service/src/main/java/io/github/alexistrejo11/drugstore/payments/core/domain/model/Sale.java"
    highlights:
      - "CONFIRMED → REFUNDED | PARTIALLY_REFUNDED"
      - "CONFIRMED → CANCELLED"
      - "Created automatically from completed Payment"
      - "Tracks refunded amount and net amount"
    techStack:
      - "Java 23"
      - "Domain-Driven Design"
      - "Factory Method (fromPayment)"

  - id: "domain-events"
    title: "Domain Events for Event-Driven Architecture"
    description: "PaymentCompletedEvent and PaymentFailedEvent enable loose coupling. SpringEventPublisherAdapter publishes events for in-process handling."
    icon: "bolt"
    category: "messaging"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service/src/main/java/io/github/alexistrejo11/drugstore/payments/core/domain/events"
    highlights:
      - "PaymentCompletedEvent triggers Sale creation"
      - "PaymentFailedEvent allows other services to handle failures"
      - "SpringEventPublisherAdapter for in-process events"
      - "Extensible for Kafka/RabbitMQ migration (only adapter needs change)"
    techStack:
      - "Spring Events"
      - "Domain-Driven Design"
      - "Kafka (Planned)"

  - id: "stripe-gateway-integration"
    title: "Stripe Gateway Integration (Planned)"
    description: "StripeGatewayAdapter implements StripeGatewayPort for PaymentIntent creation, charge processing, and refund operations."
    icon: "credit-card"
    category: "integration"
    status: "experimental"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service/src/main/java/io/github/alexistrejo11/drugstore/payments/infrastructure/adapter/output/StripeGatewayAdapter.java"
    highlights:
      - "Implements StripeGatewayPort interface"
      - "createPaymentIntent() - Creates Stripe PaymentIntent"
      - "refundCharge() - Processes full refunds"
      - "partialRefundCharge() - Processes partial refunds"
      - "⚠️ CURRENTLY A STUB - Returns null/empty strings!"
    techStack:
      - "Stripe API"
      - "Spring Service"
      - "Port-Adapter Pattern"

  - id: "value-objects"
    title: "Strongly-Typed Value Objects"
    description: "Value objects (PaymentID, OrderID, CustomerID, Money, PaymentGatewayRef, RefundInfo) provide type safety and encapsulate validation logic."
    icon: "cube"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/alexisTrejo11/drugstore-platform/tree/main/payment-service/src/main/java/io/github/alexistrejo11/drugstore/payments/core/domain/valueobjects"
    highlights:
      - "PaymentID, OrderID, CustomerID - Strongly-typed identifiers"
      - "Money - Monetary amounts with currency support"
      - "PaymentGatewayRef - Tracks Stripe PaymentIntent and Charge IDs"
      - "RefundInfo - Captures refund details (amount, reason, gateway ID)"
      - "PaymentTimeStamps/SaleTimeStamps - Audit trail"
    techStack:
      - "Java 23"
      - "Domain-Driven Design"
      - "Value Object Pattern"

  - id: "flyway-migrations"
    title: "Database Migrations"
    description: "Versioned database schema management with Flyway for reliable and repeatable migrations."
    icon: "database"
    category: "database"
    status: "stable"
    highlights:
      - "Flyway core and PostgreSQL support in build.gradle"
      - "Migrations located in classpath:db/migration"
      - "JPA ddl-auto: validate (Flyway owns schema)"
      - "HikariCP connection pooling configured"
    techStack:
      - "Flyway"
      - "PostgreSQL"
      - "Hibernate"

  - id: "api-documentation"
    title: "OpenAPI Documentation"
    description: "Automated API documentation with Swagger UI and OpenAPI 3 annotations for all endpoints."
    icon: "book"
    category: "api"
    status: "stable"
    highlights:
      - "Swagger UI available at /swagger-ui.html"
      - "API docs at /v3/api-docs"
      - "OpenAPI annotations on all controllers"
      - "Response examples and status codes documented"
    techStack:
      - "springdoc-openapi-starter-webmvc-ui:2.6.0"
      - "Swagger UI"
  # product-service
  - id: "catalog-crud"
    title: "Product CRUD with soft-delete/restore"
    description: "Create, update, delete (soft), and restore products with domain validation and role-protected writes."
    icon: "package"
    category: "api"
    status: "stable"
    githubExampleUrl: "src/main/java/io/github/alexisTrejo11/drugstore/products/adapter/in/web/controller/ProductController.java"
    highlights:
      - "POST/PUT/PATCH/DELETE require ADMIN or MANAGER role"
      - "GET by id/sku/barcode supports public read access"
      - "Soft-delete avoids physical record removal"
    techStack:
      - "Spring Web"
      - "Spring Security"
      - "JPA"
    
    # FeatureMetric[]
    metrics:
      - label: "Protected write routes"
        value: "4"
        trend: "stable"
        icon: "shield"

    # CodeSnippet
    codeSnippet:
      language: "java"
      filename: "ProductController.java"
      code: "@PostMapping @PutMapping @PatchMapping @DeleteMapping with role-based authorization"

  - id: "cached-queries"
    title: "Caching decorators for query/use-case layer"
    description: "Reads use cache-first strategy for product id/sku/barcode and paginated search."
    icon: "zap"
    category: "performance"
    status: "stable"
    githubExampleUrl: "src/main/java/io/github/alexisTrejo11/drugstore/products/core/application/usecase/decorator/CachingProductQueryUseCases.java"
    highlights:
      - "Cache regions: productById, productBySKU, productByBarcode, productSearch"
      - "Cache eviction triggered after create/update/delete/restore"
    techStack:
      - "Spring Cache"
      - "Redis"
    metrics:
      - label: "Cache regions"
        value: "4"
        trend: "stable"
        icon: "database"
    codeSnippet:
      language: "java"
      filename: "CachingProductQueryUseCases.java"
      code: "Cache lookup -> fallback delegate -> cache put pattern"

  - id: "event-publishing"
    title: "Kafka event publication for product lifecycle"
    description: "Product changes are emitted to Kafka topics for downstream services."
    icon: "activity"
    category: "integration"
    status: "stable"
    githubExampleUrl: "src/main/resources/application.yml"
    highlights:
      - "Topic settings under app.kafka.topics"
      - "DLT topic configured for failed processing flows"
    techStack:
      - "Spring Kafka"
      - "Kafka"
    metrics:
      - label: "Primary product topics"
        value: "2"
        trend: "stable"
        icon: "message-square"
    codeSnippet:
      language: "yaml"
      filename: "application.yml"
      code: "app.kafka.topics.product-events and product-events-dlt"
  # store-service
  - id: "feat-rest-crud"
    title: "REST API v2 — store lifecycle"
    description: "Create store with nested address/contact/schedule/geo; update location & schedule; status patches (maintenance, temp closure, activate/deactivate); soft-delete style flows where implemented."
    icon: "rest"
    category: "api"
    status: "stable"
    githubExampleUrl: ""
    highlights:
      - "Command controller groups mutating verbs; query controller handles search + pagination via libs_kernel PageRequest/PageResponse."
      - "@Valid Jakarta validation on DTO graphs."
      - "**Gap:** two PATCH handlers lack `@RateLimit` annotations."
    techStack:
      - "Spring MVC"
      - "Swagger annotations"
    metrics:
      - label: "Base path"
        value: "/api/v2/stores"
        trend: "stable"
        icon: "link"
      - label: "Role for writes"
        value: "ADMIN | MANAGER"
        trend: "stable"
        icon: "shield"
    codeSnippet:
      language: "java"
      filename: "StoreCommandController.java"
      code: |
        @PostMapping
        @RateLimit(profile = RateLimitProfile.SENSITIVE)
        public ResponseEntity<ResponseWrapper<StoreID>> createStore(@Valid @RequestBody CreateStoreRequest request) {
          ...
        }

  - id: "feat-query-cache"
    title: "Search, filters & Redis caching"
    description: "Dynamic search (`SearchStoreRequest` + specifications), lookup by UUID or business code, list by StoreStatus with pageable defaults; caches `stores`, `store_searches`, `store_status` with TTL 30m."
    icon: "search"
    category: "performance"
    status: "stable"
    githubExampleUrl: ""
    highlights:
      - "Writers `@CacheEvict` allEntries on mutation methods."
      - "**Risk:** search cache keys depend on Query#toString — brittle."
    techStack:
      - "Spring Cache"
      - "Spring Data Redis"
    metrics:
      - label: "Default page size"
        value: "10"
        trend: "stable"
        icon: "layers"
    codeSnippet:
      language: "java"
      filename: "StoreQueryUseCasesImpl.java"
      code: |
        @Cacheable(value = "stores", key = "#query.id.value()")
        public Store getStoreByID(GetStoreByIDQuery query) { ... }

  - id: "feat-security-jwt"
    title: "JWT + method security matrix"
    description: "Stateless Spring Security; JwtAuthenticationFilter; GET store APIs permit anonymous access; POST/PUT/PATCH/DELETE require ADMIN or MANAGER."
    icon: "lock"
    category: "security"
    status: "stable"
    githubExampleUrl: ""
    highlights:
      - "CSRF disabled for API usage."
      - "**Mismatch:** OpenAPI still documents bearer auth for read operations."
    techStack:
      - "Spring Security"
      - "jjwt 0.11.5"
    metrics:
      - label: "Token signing"
        value: "HS256 (secret from env)"
        trend: "stable"
        icon: "key"
    codeSnippet:
      language: "java"
      filename: "SecurityConfig.java"
      code: |
        .requestMatchers(HttpMethod.GET, "/api/v2/stores/**").permitAll()

  - id: "feat-obs-docker"
    title: "Docker Compose observability bundle"
    description: "Prometheus, Loki, Grafana wired on shared network; Micrometer Prometheus registry in app; Loki Logback appender."
    icon: "chart"
    category: "ops"
    status: "develop"
    githubExampleUrl: ""
    highlights:
      - "Healthchecks on app, nginx, Postgres, Redis."
      - "**Replace default Grafana credentials** before sharing."
    techStack:
      - "Micrometer Prometheus"
      - "Grafana stack"
    metrics:
      - label: "Compose services"
        value: "7"
        trend: "stable"
        icon: "docker"
    codeSnippet:
      language: "yaml"
      filename: "docker-compose.yml (excerpt)"
      code: |
        store-service:
          depends_on:
            prometheus: { condition: service_started }
            grafana: { condition: service_started }

  - id: "feat-events-stub"
    title: "Domain events → Kafka (stub)"
    description: "Port `StoreEventPublisher` exists; implementation only logs — no KafkaTemplate send, no topic config in code path."
    icon: "kafka"
    category: "integration"
    status: "planned"
    githubExampleUrl: ""
    highlights:
      - "Compose still requires bootstrap servers — operational friction."
      - "TODO comment mentions retries not implemented."
    techStack:
      - "Spring Kafka (dependency landscape only)"
    metrics:
      - label: "Events delivered"
        value: "0 (stub)"
        trend: "stable"
        icon: "alert"
    codeSnippet:
      language: "java"
      filename: "StoreEventPublisherImpl.java"
      code: |
        public void publishStoreStatusChanged(StoreStatusChangedEvent event) {
          log.info("Publishing ... {}", event.getStoreId());
          // no broker call
        }
  # user-service
  - id: "user-query-api"
    title: "Authenticated user reads"
    description: "Lookup by UUID, email, phone; paginated listings by UserRole or UserStatus."
    icon: "search"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/PLACEHOLDER/drugstore-platform/blob/main/user-service/src/main/java/io/github/alexisTrejo11/drugstore/users/user/adapter/input/rest/UserQueryManagerController.java"
    highlights:
      - "Returns libs_kernel ResponseWrapper wrapping UserHTTPResponse or page content"
      - "Requires JWT for /api/** (see SecurityConfig)"
    techStack:
      - "Spring MVC"
      - "Swagger annotations"
      - "Jakarta Validation"
    metrics:
      - label: "Test coverage signal"
        value: "E2E MockMvc (UserServiceE2EIntegrationTest)"
        trend: "stable"
        icon: "test"
    codeSnippet:
      language: ""
      filename: ""
      code: ""

  - id: "user-manager-api"
    title: "Manager / admin lifecycle"
    description: "Create customer users, activate with code, ban, unban, hard delete via CommandBus."
    icon: "shield"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/PLACEHOLDER/drugstore-platform/blob/main/user-service/src/main/java/io/github/alexisTrejo11/drugstore/users/user/adapter/input/rest/UserManagerController.java"
    highlights:
      - "POST create returns 201 Created"
      - "ROLE ADMIN or MANAGER enforced on /api/v2/users/manager/**"
    techStack:
      - "Spring Security"
      - "CommandBus"
    metrics:
      - label: "Authorization surface"
        value: "Privileged — audit recommended"
        trend: "stable"
        icon: "alert"
    codeSnippet:
      language: ""
      filename: ""
      code: ""

  - id: "profile-api"
    title: "Current user profile"
    description: "GET /profile/me and PATCH profile with rate limiting hooks (libs_kernel)."
    icon: "user"
    category: "api"
    status: "stable"
    githubExampleUrl: "https://github.com/PLACEHOLDER/drugstore-platform/blob/main/user-service/src/main/java/io/github/alexisTrejo11/drugstore/users/profile/infrastructure/web/rest/ProfileController.java"
    highlights:
      - "@RequestAttribute userId injected post-JWT filter"
      - "@SecurityRequirement bearerAuth"
    techStack:
      - "Spring MVC"
      - "RateLimit annotation"
    metrics:
      - label: "PII sensitivity"
        value: "High"
        trend: "stable"
        icon: "lock"
    codeSnippet:
      language: ""
      filename: ""
      code: ""

  - id: "kafka-consumer"
    title: "User domain event consumption"
    description: "Subscribes to configurable topics for created, updated, deleted events with manual acknowledgement."
    icon: "kafka"
    category: "messaging"
    status: "beta"
    githubExampleUrl: "https://github.com/PLACEHOLDER/drugstore-platform/blob/main/user-service/src/main/java/io/github/alexisTrejo11/drugstore/users/user/adapter/output/messaging/kafka/consumer/UserEventConsumer.java"
    highlights:
      - "**Missing DLQ / retry budget** — throws on deserialization or handler failures"
      - "**Trusted packages '*`** — widen attack surface if poison messages arrive"
    techStack:
      - "Spring Kafka"
      - "Jackson"
    metrics:
      - label: "Reliability"
        value: "At-least-once (manual ack)"
        trend: "unstable-without-DLQ"
        icon: "warning"
    codeSnippet:
      language: ""
      filename: ""
      code: ""

  - id: "grpc-contract"
    title: "gRPC protobuf contract + server class"
    description: "user_service.proto defines uniqueness checks, credential validation, CRUD-style RPCs."
    icon: "rpc"
    category: "integration"
    status: "not-wired"
    githubExampleUrl: "https://github.com/PLACEHOLDER/drugstore-platform/blob/main/user-service/src/main/proto/user_service.proto"
    highlights:
      - "**Dangerous assumption**: GRPC_SECURITY_ENABLED:false with no alternate auth"
      - "**Implementation gap**: no Netty server registration;GrpcServerConfig is empty"
    techStack:
      - "grpc-java"
      - "protobuf"
    metrics:
      - label: "Operational readiness"
        value: "0% listener"
        trend: "down"
        icon: "error"
    codeSnippet:
      language: ""
      filename: ""
      code: ""

  - id: "observability-stack"
    title: "Metrics, logs, dashboards (local)"
    description: "Prometheus scrape, Grafana + Loki in docker-compose; Micrometer Prometheus + Loki logback appender in app."
    icon: "chart"
    category: "ops"
    status: "stable-local"
    githubExampleUrl: ""
    highlights:
      - "Grafana default credentials are weak (dev only)"
    techStack:
      - "Prometheus"
      - "Grafana"
      - "Loki"
    metrics:
      - label: "Prod parity"
        value: "Placeholder — map to cloud APM (dummy: DD_SERVICE=user-service)"
        trend: "stable"
        icon: "cloud"
    codeSnippet:
      language: ""
      filename: ""
      code: ""
---

# Project Features

> Auto-generated by `scripts/merge_service_sources.py`. Edit service-level `docs/project/source/*.md` files, then regenerate.

<!-- BEGIN address-service -->
<!-- Source: address-service/docs/project/source/ProjectFeature.md -->
# Project Features

> 13 comprehensive features documented covering all major functionality. The service is production-ready with proper validation, security, and documentation. Potential improvements: Add Kafka integration for publishing address events (created/updated/deleted), implement caching with @Cacheable for frequently accessed addresses, add Micrometer metrics for observability (address creation rate, validation failures), and create Kubernetes deployment manifests (deployment.yaml, service.yaml, ingress.yaml).

<!-- END address-service -->

<!-- BEGIN auth-service -->
<!-- Source: auth-service/docs/project/source/ProjectFeature.md -->
# Project Features

> Feature catalog includes JWT, 2FA, OAuth2, Kafka, gRPC, DDD, and **HTTP integration tests** (Testcontainers + in-process UserService gRPC). 
> 
> **Potential Issues & Improvements:**
> - Expand unit-test coverage beyond integration scenarios
> - Java 23 may cause compatibility issues with some libraries
> - Missing Kubernetes manifests for cloud deployment
> - PLACEHOLDER: Add Circuit Breaker (Resilience4j) for gRPC calls to user-service
> - PLACEHOLDER: Add Micrometer metrics for auth success/failure rates
> - PLACEHOLDER: Set up CI/CD pipeline (GitHub Actions)
> - Docker Compose doesn't include Kafka dependency (needed for events)
> - Consider refresh token rotation with reuse detection for enhanced security
> - Add rate limiting on ALL endpoints (some may be missing @RateLimit)

<!-- END auth-service -->

<!-- BEGIN cart-service -->
<!-- Source: cart-service/docs/project/source/ProjectFeature.md -->
# Project Features

> 12 comprehensive features documented covering DDD, gRPC, Kafka, caching, and design patterns. Has 11 unit test files for domain layer.
> 
> **Potential Issues & Improvements:**
> - No @RateLimit annotations on REST controllers (unlike address-service)
> - CartPurchasedEvent defined but not published to Kafka (PLACEHOLDER)
> - Java 23 may cause compatibility issues with some libraries
> - No integration tests for gRPC endpoints
> - @Cacheable annotations not applied to repository methods
> - Docker Compose missing Kafka dependency for product-events
> - Missing Kubernetes manifests for cloud deployment
> - No CI/CD pipeline (GitHub Actions/Jenkins)
> - Consider adding Circuit Breaker (Resilience4j) for external calls
> - Add Micrometer metrics for cart operations (add/update/clear rates)

<!-- END cart-service -->

<!-- BEGIN employee-service -->
<!-- Source: employee-service/docs/project/source/ProjectFeature.md -->
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

<!-- END employee-service -->

<!-- BEGIN inventory-service -->
<!-- Source: inventory-service/docs/project/source/ProjectFeature.md -->
# Project Features

## Integration testing (`test` profile)

Automated API integration tests exercise the HTTP stack end-to-end with a dedicated Spring profile:

| Area | Detail |
|------|--------|
| **Profile** | `test` — activated via `src/test/resources/application.yml` (`spring.profiles.active=test`). |
| **Config** | `src/test/resources/application-test.yml`: H2 in-memory (`MODE=PostgreSQL`), Hibernate `ddl-auto: create-drop`, Flyway disabled for speed and portability. |
| **Authentication** | **Real JWT path**: tokens signed with the same `jwt.secret` as the test YAML are sent as `Authorization: Bearer …`; requests pass through `JwtAuthenticationFilter` and `JwtTokenValidator` (no mocked security filter chain). |
| **Isolation** | Redis and Kafka auto-configuration excluded; `app.rate-limit.global.enabled: false`; Spring cache type `simple`. |
| **Main suite** | `InventoryApiIntegrationTest`: `@SpringBootTest` + `@AutoConfigureMockMvc` + `@Transactional` — inventory create → GET by id/product, PATCH settings, low-stock pagination, and auth behaviour (401 without token, 403 insufficient role, invalid JWT). |
| **JWT helper** | `IntegrationTestJwtSupport` builds HS256 access tokens with claims `userId`, `role`, `type=access`. |
| **Smoke** | `InventoryItemServiceImplApplicationTests` loads the Spring context under `test`. |

Run: `./gradlew test` (from `inventory-service`).

---

> **CRITICAL OBSERVATIONS:**
> 1. **RabbitMQ vs Kafka Inconsistency**: inventory-service uses RabbitMQ (spring-boot-starter-amqp) while all other services (address, auth, cart) use Kafka. This PREVENTS direct messaging integration.
> 2. **Java Version Mismatch**: build.gradle specifies Java 23 but Dockerfile uses openjdk:17-jdk-slim. This will cause `UnsupportedClassVersionError` at runtime.
> 3. **Flyway Disabled**: Database migrations are configured but disabled (`flyway.enabled: false`). This means schema changes won't be tracked or auto-applied.
> 4. **docker-compose.yml**: Present for local stacks (service + Postgres + Redis + nginx + observability); align Dockerfile/Java/Flyway with how you run locally.
> 5. **No gRPC Endpoints**: Other services expose gRPC for inter-service communication, but inventory-service only has REST endpoints.
> 6. **PLACEHOLDER Metrics**: Actual metric values (cache hit rate, response times, etc.) need to be filled in with real monitoring data.
> 7. **Expiration Alerts Status**: Marked as "beta" because the scheduled job to auto-mark expiring batches may not be implemented yet (only manual endpoints exist).

<!-- END inventory-service -->

<!-- BEGIN notification-service -->
<!-- Source: notification-service/docs/project/source/ProjectFeature.md -->
# Project Features
> Notes goes here...

<!-- END notification-service -->

<!-- BEGIN order-service -->
<!-- Source: order-service/docs/project/source/ProjectFeature.md -->
# Project Features
> Order Service provides 14+ features including order lifecycle management with 9 status states, role-based API access, Redis caching, rate limiting, event-driven architecture, and comprehensive monitoring with ELK stack. The service follows DDD principles with hexagonal architecture.

<!--
  OBSERVATIONS FOR ProjectFeature:
  ✅ POSITIVE:
    - 14 well-documented features covering all aspects of order management
    - Proper role-based security (CUSTOMER, EMPLOYEE, ADMIN)
    - Event-driven architecture with 2 domain events
    - Decorator pattern for caching and logging cross-cutting concerns
    - Comprehensive validation (Bean Validation + domain validation)
    - OpenAPI documentation with custom annotations
    - Flyway for database migrations

  ⚠️ WARNINGS / MISSING / DANGEROUS:
    - CachingUserServiceDecorator exists but Order caching not fully implemented (planned)
    - EventPublisher port exists but event bus integration is "planned" - events published but not consumed
    - Rate limiting uses @RateLimit annotation but distributed rate limiting with Redis not confirmed
    - "observability" feature marked as "beta" - Spring Boot Admin and ELK stack not fully integrated
    - No unit/integration test coverage mentioned in features
    - AddressController features documented but actual endpoint URLs not verified in this review
    - UserController features documented but actual implementation not deeply reviewed
    - No notification feature - customers not notified on status changes (should be event-driven)
-->

<!-- END order-service -->

<!-- BEGIN payment-service -->
<!-- Source: payment-service/docs/project/source/ProjectFeature.md -->
# Project Features
> Payment Service provides 13+ features including dual aggregate roots (Payment and Sale), 6-state payment lifecycle, Stripe webhook handling, domain events, and strongly-typed value objects. The service follows DDD principles with hexagonal architecture. Stripe integration is planned but StripeGatewayAdapter is currently a STUB.

<!--
  OBSERVATIONS FOR ProjectFeature:
  ✅ POSITIVE:
    - 13 well-documented features covering all aspects of payment processing
    - Dual aggregate roots (Payment and Sale) with separate lifecycles
    - Clean hexagonal architecture with ports and adapters
    - Domain events (PaymentCompletedEvent, PaymentFailedEvent) for loose coupling
    - Strongly-typed value objects for type safety
    - Comprehensive webhook handling with Stripe signature verification
    - Flyway migrations with JPA validation mode
    - HikariCP connection pooling configured
    - OpenAPI documentation with springdoc-openapi

  ⚠️ WARNINGS / MISSING / DANGEROUS:
    - StripeGatewayAdapter IS A STUB - all methods return null or empty strings (lines 11, 16, 21)!
    - Stripe integration "Planned" but no actual Stripe SDK dependency in build.gradle
    - PaymentApplicationServiceImpl NOT FOUND - only interface exists, no implementation!
    - No unit/integration test coverage mentioned in features
    - Kafka dependency in build.gradle but NO Kafka code implemented
    - No SecurityConfig - controllers have "Security placeholder" comments
    - Spring Boot Actuator NOT in build.gradle but referenced in comments
    - No rate limiting configured (mentioned as "placeholder" in controllers)
    - SaleController says "Sales are read-only from API perspective" but no enforcement
    - Refund flow depends on StripeGatewayAdapter which is NOT implemented
    - No notification feature - customers not notified on payment completion/failure
    - PaymentMethod enum has 6 methods but Stripe may not support all (e.g., CASH, INTERNAL_CREDIT)
-->

<!-- END payment-service -->

<!-- BEGIN product-service -->
<!-- Source: product-service/docs/project/source/ProjectFeature.md -->
# Project Features

## Notes

- SKU normalization is uppercase in domain value objects; clients should avoid case-sensitive assumptions.
- Barcode validation is strict digits-only, which can reject legacy alphanumeric barcodes if not migrated.

<!-- END product-service -->

<!-- BEGIN store-service -->
<!-- Source: store-service/docs/project/source/ProjectFeature.md -->
# Project Features

> [!missing] Rate limit coverage  
> Apply `@RateLimit` to **`PATCH .../temporary-closure`** and **`PATCH .../deactivate`** for parity with other state-changing endpoints.

> [!danger] Public read API  
> **All GET /api/v2/stores/** are **anonymous** — ensure no PII exposure and consider API gateway auth if catalog should be private.

<!-- END store-service -->

<!-- BEGIN user-service -->
<!-- Source: user-service/docs/project/source/ProjectFeature.md -->
# Project Features

## Summary

Features span **REST** (query, manager, profile), **Kafka** ingestion, **gRPC contract** (not yet served), and **local observability** stack. Prioritize fixing **OpenAPI package scan** and **docker profile YAML** before treating docs or contracts as release artifacts.

<!-- END user-service -->
