---
# ProjectFeature[]
features:
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
---
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
