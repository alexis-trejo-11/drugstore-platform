---
codeExamples:
  - id: "use-cases-orchestrator"
    title: "DDD UseCasesOrchestrator Pattern"
    description: "Central orchestrator implementing 4 port interfaces (AuthUseCases, LogoutUseCases, PasswordUseCases, RegisterUseCases, TwoFaConfigUseCases) following Domain-Driven Design"
    category: "Architecture"
    duration: "10 min read"
    views: 0
    tags:
      - "DDD"
      - "Ports & Adapters"
      - "Orchestrator"
      - "Clean Architecture"
    files:
      - name: "UseCasesOrquestrator.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/accounts/auth/core/application/UseCasesOrquestrator.java"
        language: "java"
        content: |
          /**
           * UseCasesOrquestrator - Orchestrator for all authentication use cases
           * This is the entry point for the authentication bounded context
           */
          @Service
          @Slf4j
          @RequiredArgsConstructor
          public class UseCasesOrquestrator
              implements AuthUseCases, LogoutUseCases, PasswordUseCases, RegisterUseCases, TwoFaConfigUseCases {

            private final RegisterUseCase registerUseCase;
            private final LoginUseCase loginUseCase;
            private final RefreshAccessTokenUseCase refreshAccessTokenUseCase;
            private final LogoutUseCase logoutUseCase;
            private final LogoutAllUseCase logoutAllUseCase;
            private final TwoFactorLoginUseCase twoFactorLoginUseCase;
            private final ForgotPasswordUseCase forgotPasswordUseCase;
            private final ValidateResetTokenUseCase validateResetTokenUseCase;
            private final ResetPasswordUseCase resetPasswordUseCase;
            private final ChangePasswordUseCase changePasswordUseCase;
            private final ActivateAccountUseCase activateAccountUseCase;
            private final EnableTwoFactorAuthUseCase enableTwoFactorAuthUseCase;
            private final DisableTwoFactorAuthUseCase disableTwoFactorAuthUseCase;
            private final SendValidationCodeUseCase sendValidationCodeUseCase;
            private final VerifyTwoFactorCodeUseCase verifyTwoFactorCodeUseCase;

            @Override
            public SignUpResult register(SignupCommand command) {
                log.info("AuthUseCases: Executing signup use case for email: {}", command.email().value());
                return registerUseCase.execute(command);
            }

            @Override
            public SessionPayload login(LoginCommand command) {
                log.info("AuthUseCases: Executing login use case for identifier: {}", maskIdentifier(command.identifier()));
                return loginUseCase.execute(command);
            }

            // ... other delegation methods
          }
        highlighted: true
        explanation: "Orchestrator pattern: single entry point delegating to specific use case implementations. Implements multiple port interfaces for clean API."

  - id: "token-factory"
    title: "TokenFactory - Multi-Type Token Creation"
    description: "Factory creating 4 token types: ACCESS (JWT), REFRESH (JWT), ACTIVATION (numeric), TWO_FA (numeric) using JJWT library"
    category: "Security"
    duration: "8 min read"
    views: 0
    tags:
      - "Factory Pattern"
      - "JWT"
      - "JJWT"
      - "Token Management"
    files:
      - name: "TokenFactory.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/accounts/auth/adapter/output/security/tokens/factory/TokenFactory.java"
        language: "java"
        content: |
          @Component
          @RequiredArgsConstructor
          @Slf4j
          public class TokenFactory {
            private final JwtProperties jwtProperties;
            private static final SecureRandom SECURE_RANDOM = new SecureRandom();

            public Token createToken(TokenType type, UserClaims userClaims) {
                return switch (type) {
                    case ACCESS -> createAccessToken(userClaims);
                    case REFRESH -> createRefreshToken(userClaims);
                    case ACTIVATION -> createActivationToken(userClaims);
                    case TWO_FA -> createTwoFaToken(userClaims);
                };
            }

            private Token createAccessToken(UserClaims userClaims) {
                Map<String, Object> claims = new HashMap<>();
                claims.put("role", userClaims.role());
                claims.put("userId", userClaims.userId());
                claims.put("email", userClaims.email());
                claims.put("type", "access");

                long expirationMs = jwtProperties.getAccessTokenExpirationSeconds();
                String tokenCode = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(userClaims.userId())
                    .setIssuer(jwtProperties.getIssuer())
                    .setIssuedAt(Date.from(Instant.now()))
                    .setExpiration(Date.from(Instant.now().plusMillis(expirationMs)))
                    .signWith(getSigningKey())
                    .compact();

                return new Token(tokenCode, "ACCESS", Duration.ofMillis(expirationMs),
                    LocalDateTime.now().plusSeconds(expirationMs / 1000), userClaims);
            }

            private Token createRefreshToken(UserClaims userClaims) {
                // Similar to access token but with different claims and longer expiration
                // Default: 7 days
            }

            private Token createActivationToken(UserClaims userClaims) {
                String tokenCode = generateNumericToken(activationTokenLength); // 6 digits
                return new Token(tokenCode, "ACTIVATION",
                    Duration.ofMinutes(activationExpirationMinutes),
                    LocalDateTime.now().plusMinutes(activationExpirationMinutes), userClaims);
            }
          }
        highlighted: true
        explanation: "Factory pattern with switch expression for different token types. JWTs signed with HMAC-SHA key, numeric tokens for activation/2FA."

      - name: "TokenType.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/accounts/auth/adapter/output/security/tokens/TokenType.java"
        language: "java"
        content: |
          public enum TokenType {
              ACCESS,     // JWT access token (short-lived, ~15min)
              REFRESH,     // JWT refresh token (long-lived, ~7 days)
              ACTIVATION,  // Numeric code (6 digits, 15min) for email activation
              TWO_FA       // Numeric code (6 digits, 5min) for 2FA
          }
        highlighted: false
        explanation: "Enum defining the 4 token types supported by the auth service."

  - id: "kafka-event-producer"
    title: "Kafka Event Publishing"
    description: "UserEventProducer publishes 8+ event types to Kafka topics with CompletableFuture for non-blocking operations"
    category: "Messaging"
    duration: "12 min read"
    views: 0
    tags:
      - "Kafka"
      - "Events"
      - "DDD"
      - "Pub/Sub"
    files:
      - name: "UserEventProducer.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/accounts/auth/adapter/output/messaging/kafka/producer/UserEventProducer.java"
        language: "java"
        content: |
          @Component
          public class UserEventProducer implements UserEventPublisher {
            @Value("${kafka.topics.user.created:user.created}")
            private String userCreatedTopic;

            @Value("${kafka.topics.user.registered:user-registered}")
            private String userRegisteredTopic;

            @Value("${kafka.topics.auth.password-changed:auth.password-changed}")
            private String passwordChangedTopic;

            // ... more topic configs

            @Override
            public boolean publishUserCreated(UserCreatedEvent event) {
                log.info("Publishing UserCreatedEvent for userId: {}", event.userId());

                try {
                    CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                        userCreatedTopic, event.userId(), event);

                    future.whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("UserCreatedEvent published successfully");
                        } else {
                            log.error("Failed to publish UserCreatedEvent: {}", ex.getMessage());
                        }
                    });

                    future.get(timeoutSeconds, TimeUnit.SECONDS);
                    return true;
                } catch (Exception e) {
                    log.error("Error publishing UserCreatedEvent", e);
                    return false;
                }
            }
          }
        highlighted: true
        explanation: "Uses KafkaTemplate with CompletableFuture for async publishing. Events include: UserCreated, UserUpdated, UserDeleted, UserRegistered, UserLogin, PasswordChanged, AccountActivated, TwoFactorEnabled/Disabled."

  - id: "grpc-user-service-client"
    title: "gRPC Client for User-Service"
    description: "UserServiceGrpcClient communicates with user-service via Protobuf/gRPC for user CRUD operations"
    category: "Communication"
    duration: "7 min read"
    views: 0
    tags:
      - "gRPC"
      - "Protobuf"
      - "Microservices"
      - "RPC"
    files:
      - name: "UserServiceGrpcClient.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/accounts/auth/adapter/output/grpc/UserServiceGrpcClient.java"
        language: "java"
        content: |
          @Component
          @Slf4j
          public class UserServiceGrpcClient implements UserServiceClient {
            private final UserGrpcServiceBlockingStub blockingStub;

            public Optional<UserDto> getUserByEmail(String email) {
                log.debug("gRPC: Getting user by email: {}", maskEmail(email));

                try {
                    UserRequest request = UserRequest.newBuilder()
                        .setIdentifier(email)
                        .setIdentifierType(IdentifierType.EMAIL)
                        .build();

                    UserResponse response = blockingStub.getUser(request);
                    return Optional.of(UserGrpcMapper.toDto(response));

                } catch (StatusRuntimeException e) {
                    if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                        log.debug("User not found by email: {}", maskEmail(email));
                        return Optional.empty();
                    }
                    log.error("gRPC error getting user by email", e);
                    throw new UserServiceException("gRPC call failed", e);
                }
            }

            // ... other gRPC methods: createUser, updateUser, deleteUser, etc.
          }
        highlighted: true
        explanation: "Uses gRPC blocking stub for synchronous calls. Protobuf messages defined in .proto files. Maps between Protobuf and domain objects using UserGrpcMapper."

      - name: "UserGrpcMapper.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/accounts/auth/adapter/output/grpc/UserGrpcMapper.java"
        language: "java"
        content: |
          public class UserGrpcMapper {
            public static UserDto toDto(UserResponse response) {
                return UserDto.builder()
                    .userId(response.getUserId())
                    .email(response.getEmail())
                    .name(response.getName())
                    .phoneNumber(response.getPhoneNumber())
                    .role(response.getRole())
                    .enabled(response.getEnabled())
                    .build();
            }

            public static UserRequest toCreateRequest(CreateUserCommand command) {
                return UserRequest.newBuilder()
                    .setEmail(command.email().value())
                    .setPassword(command.password().value())
                    .setName(command.name())
                    .setPhoneNumber(command.phoneNumber().value())
                    .setRole(command.role())
                    .build();
            }
          }
        highlighted: false
        explanation: "Mapper between Protobuf messages and domain objects/DTOs."

  - id: "redis-session-repository"
    title: "Redis Session Management"
    description: "RedisSessionRepository manages JWT refresh token sessions with blacklisting support for immediate revocation"
    category: "Persistence"
    duration: "6 min read"
    views: 0
    tags:
      - "Redis"
      - "Sessions"
      - "JWT"
      - "Blacklisting"
    files:
      - name: "RedisSessionRepository.java"
        path: "PLACEHOLDER: Check adapter/output/persistence/ for actual file"
        language: "java"
        content: |
          @Repository
          @Slf4j
          public class RedisSessionRepository implements SessionRepository {
            private final RedisTemplate<String, Object> redisTemplate;
            private final JwtProperties jwtProperties;

            @Override
            public void saveSession(String refreshToken, JwtSession session) {
                String key = "session:" + refreshToken;
                long ttlSeconds = jwtProperties.getRefreshTokenExpirationSeconds();
                redisTemplate.opsForValue().set(key, session, ttlSeconds, TimeUnit.SECONDS);
                log.debug("Session saved for user: {}", session.userId());
            }

            @Override
            public Optional<JwtSession> getSession(String refreshToken) {
                String key = "session:" + refreshToken;
                JwtSession session = (JwtSession) redisTemplate.opsForValue().get(key);
                return Optional.ofNullable(session);
            }

            @Override
            public void blacklistSession(String refreshToken) {
                String blacklistKey = "blacklist:" + refreshToken;
                redisTemplate.opsForValue().set(blacklistKey, "true",
                    jwtProperties.getRefreshTokenExpirationSeconds(), TimeUnit.SECONDS);
                log.info("Session blacklisted: {}", maskToken(refreshToken));
            }
          }
        highlighted: true
        explanation: "Stores refresh token sessions in Redis with TTL matching token expiration. Blacklisting allows immediate session revocation before expiration."

  - id: "command-pattern"
    title: "Command Pattern for Requests"
    description: "Each request type has a corresponding Command object (SignupCommand, LoginCommand, ChangePasswordCommand) with validation"
    category: "Design Patterns"
    duration: "5 min read"
    views: 0
    tags:
      - "Command Pattern"
      - "CQRS-like"
      - "DDD"
    files:
      - name: "SignupCommand.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/accounts/auth/core/application/command/SignupCommand.java"
        language: "java"
        content: |
          public record SignupCommand(
              Email email,
              Password password,
              String name,
              PhoneNumber phoneNumber,
              String role
          ) {
            public static SignupCommand fromRequest(SignupRequest request, String role) {
                return new SignupCommand(
                    new Email(request.email()),
                    new Password(request.password()),
                    request.name(),
                    new PhoneNumber(request.phoneNumber()),
                    role
                );
            }
          }
        highlighted: true
        explanation: "Command objects encapsulate request data with domain value objects (Email, Password, PhoneNumber) for validation at the domain level."

      - name: "LoginCommand.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/accounts/auth/core/application/command/login/LoginCommand.java"
        language: "java"
        content: |
          public record LoginCommand(
              String identifier,  // email or phone number
              String password
          ) {
            public static LoginCommand fromRequest(LoginRequest request) {
                return new LoginCommand(request.emailOrPhoneNumber(), request.password());
            }
          }
        highlighted: false
        explanation: "Simple command for login with identifier (email or phone) and password."

  - id: "http-integration-tests"
    title: "Full-stack HTTP integration tests"
    description: "AuthEndpointsIntegrationTest boots the Spring context with Testcontainers Redis/Kafka, wires an in-process UserService gRPC server (InMemoryUserGrpcServer), and drives register→activate→login and related flows using real Redis opaque tokens."
    category: "Quality"
    duration: "15 min read"
    views: 0
    tags:
      - "JUnit 5"
      - "Testcontainers"
      - "Spring Boot Test"
      - "gRPC"
    files:
      - name: "AuthEndpointsIntegrationTest.java"
        path: "src/test/java/io/github/alexisTrejo11/drugstore/accounts/integration/AuthEndpointsIntegrationTest.java"
        language: "java"
        content: |
          @SpringBootTest(classes = AuthServiceApplication.class,
              webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
          @ActiveProfiles({"integration-test", "test"})
          @Testcontainers(disabledWithoutDocker = true)
          class AuthEndpointsIntegrationTest {

            @Container static final GenericContainer<?> REDIS =
                new GenericContainer<>(DockerImageName.parse("redis:7-alpine")).withExposedPorts(6379);

            @Container static final KafkaContainer KAFKA =
                new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.1"));

            @DynamicPropertySource
            static void registerProps(DynamicPropertyRegistry r) {
              r.add("spring.data.redis.host", REDIS::getHost);
              r.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
              r.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
              r.add("grpc.client.user-service.host", () -> "localhost");
              r.add("grpc.client.user-service.port", () -> grpcPort());
            }
            // Netty gRPC server + InMemoryUserGrpcServer started in static initializer
          }
        highlighted: true
        explanation: "Uses real infrastructure boundaries (Redis keys, Kafka producers, gRPC stub) so regressions in wiring or token handling surface in CI when Docker is available."
---
# CodeShowCase

> Examples span DDD, security, messaging, persistence, and **full-stack integration tests** under `src/test/.../integration/`. Potential additions: OAuth2 success handler walkthrough, CustomOAuth2UserService, value objects, domain events, and more unit-level tests.
