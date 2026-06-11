# Code showcase

Plain-Markdown companion to `docs/project/source/ProjectCodeShowCase.md`. Paths are relative to the `auth-service` module root.

---

## 1. DDD UseCasesOrchestrator pattern

**File:** `src/main/java/io/github/alexisTrejo11/drugstore/accounts/auth/core/application/UseCasesOrquestrator.java`

**Idea:** Single orchestrator implements `AuthUseCases`, `LogoutUseCases`, `PasswordUseCases`, `RegisterUseCases`, and `TwoFaConfigUseCases`, delegating to focused use-case classes.

**Why it matters:** One entry point for the authentication bounded context; keeps controllers thin and use cases testable in isolation.

```java
@Service
@RequiredArgsConstructor
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
  // ... delegates to LoginUseCase, PasswordUseCases, etc.
}
```

---

## 2. TokenFactory — multi-type token creation

**Files:**

- `adapter/output/security/tokens/factory/TokenFactory.java`
- `adapter/output/security/tokens/TokenType.java`

**Idea:** Factory with `switch` on `TokenType`: ACCESS/REFRESH (JWT), ACTIVATION/TWO_FA (numeric codes).

```java
public Token createToken(TokenType type, UserClaims userClaims) {
  return switch (type) {
    case ACCESS -> createAccessToken(userClaims);
    case REFRESH -> createRefreshToken(userClaims);
    case ACTIVATION -> createActivationToken(userClaims);
    case TWO_FA -> createTwoFaToken(userClaims);
  };
}
```

**Token types:** ACCESS (~15 min JWT), REFRESH (~7 days JWT), ACTIVATION (6-digit, ~15 min), TWO_FA (6-digit, ~5 min).

---

## 3. Kafka event publishing

**File:** `adapter/output/messaging/kafka/producer/UserEventProducer.java`

**Idea:** `KafkaTemplate` + `CompletableFuture` for async send with timeout; implements `UserEventPublisher` port.

```java
CompletableFuture<SendResult<String, Object>> future =
    kafkaTemplate.send(userCreatedTopic, event.userId(), event);
future.get(timeoutSeconds, TimeUnit.SECONDS);
```

**Events:** UserCreated, UserUpdated, UserDeleted, UserRegistered, UserLogin, PasswordChanged, AccountActivated, TwoFactor enabled/disabled.

---

## 4. gRPC user-service client

**Files:**

- `adapter/output/grpc/UserServiceGrpcClient.java`
- `adapter/output/grpc/UserGrpcMapper.java`

**Idea:** Blocking gRPC stub; map Protobuf ↔ DTO; `NOT_FOUND` → `Optional.empty()`.

```java
UserRequest request = UserRequest.newBuilder()
    .setIdentifier(email)
    .setIdentifierType(IdentifierType.EMAIL)
    .build();
UserResponse response = blockingStub.getUser(request);
return Optional.of(UserGrpcMapper.toDto(response));
```

---

## 5. Redis session management

**File:** `adapter/output/persistence/` (session repository implementation)

**Idea:** Store refresh sessions under `session:{token}`; blacklist under `blacklist:{token}` with TTL matching refresh expiration.

```java
redisTemplate.opsForValue().set("session:" + refreshToken, session, ttlSeconds, TimeUnit.SECONDS);
redisTemplate.opsForValue().set("blacklist:" + refreshToken, "true", ttlSeconds, TimeUnit.SECONDS);
```

---

## 6. Command pattern for requests

**Files:**

- `core/application/command/SignupCommand.java`
- `core/application/command/login/LoginCommand.java`

**Idea:** Records wrap domain value objects (`Email`, `Password`, `PhoneNumber`) so validation happens at the domain boundary.

```java
public record SignupCommand(Email email, Password password, String name, PhoneNumber phoneNumber, String role) {
  public static SignupCommand fromRequest(SignupRequest request, String role) { ... }
}
```

---

## 7. Full-stack HTTP integration tests

**File:** `src/test/java/io/github/alexisTrejo11/drugstore/accounts/integration/AuthEndpointsIntegrationTest.java`

**Idea:** `@SpringBootTest` + Testcontainers Redis/Kafka + Netty in-process `InMemoryUserGrpcServer`; read opaque tokens from real Redis for activation/reset/2FA flows.

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"integration-test", "test"})
@Testcontainers(disabledWithoutDocker = true)
class AuthEndpointsIntegrationTest {
  @Container static final GenericContainer<?> REDIS = ...;
  @Container static final KafkaContainer KAFKA = ...;
  // @DynamicPropertySource wires container ports + gRPC stub port
}
```

---

## Related additions (not yet in showcase)

- OAuth2: `CustomOAuth2UserService`, `OAuth2AuthenticationSuccessHandler`
- Domain events under `core/domain/event`
- Value objects: `Email`, `Password`, `PhoneNumber`
