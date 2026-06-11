# CodeShowCase

Excerpts point to **`user-service/src/...`** in this repo — line numbers omitted; open files in IDE for exact context.

---

## JWT + role matchers — `SecurityConfig.java`

Stateful session disabled; JWT filter injected before username/password authentication filter segment; Swagger static assets deliberately **ignored** by security matcher so UI assets do not bounce to forbidden JSON entrypoint.

```
requestMatchers("/api/v2/users/manager/**").hasAnyRole("ADMIN", "MANAGER")
requestMatchers("/api/**").authenticated()
```

### Risk / note

`/actuator/**` is **`permitAll`** in this configuration — adequate for hacked-together Compose, unacceptable for hostile networks.

---

## Create user mutation — `UserManagerController.java`

```java
@PostMapping("/")
public ResponseEntity<ResponseWrapper<?>> createUser(@Valid @RequestBody UserRequest userRequest) {
  CreateUserCommand command = userRequest.toCommand(UserRole.CUSTOMER);
  CommandResult commandResult = commandBus.dispatch(command);
  var response = ResponseWrapper.success(commandResult.data(), "User created successfully");
  return ResponseEntity.status(201).body(response);
}
```

**Product gap:** Caller cannot choose **`EMPLOYEE`**/`ADMIN` role through this endpoint without further API design.

---

## Kafka manual ack failure path — `UserEventConsumer.java`

```java
} catch (Exception e) {
  log.error("Error consuming UserCreatedEvent...", e);
  // TODO DLQ — currently rethrows causing redelivery churn for poison records
  throw new RuntimeException("Failed to process UserCreatedEvent", e);
}
```

**Operational danger:** pair with alerting on consumer lag and implement DLQ/backoff roadmap.

---

## gRPC skeleton — `UserGrpcServer.java`

```java
@Component
public class UserGrpcServer extends UserServiceGrpc.UserServiceImplBase {
  // Implements RPC stubs — no Startup bean registers Netty gRPC Server in codebase search
}
```

**Action item:** Introduce **`spring.grpc.server`** style auto-config OR manual lifecycle:

```java
// Illustrative pseudo — not committed
Grpc.newServerBuilderForPort(grpcProperties.getPort(), InsecureServerCredentials.create())
    .addService(userGrpcServer)
    .build()
    .start();
```

---

## E2E testing pattern — `UserServiceE2EIntegrationTest.java`

Integration tests bootstrap full Spring context with **`@AutoConfigureMockMvc`**, purge JPA repos per test, synthesize bearer tokens aligned with **`jwt.secret`**. Good guardrail baseline for regressions — extend with **`@EmbeddedKafka`** or Testcontainers when stabilizing messaging.
