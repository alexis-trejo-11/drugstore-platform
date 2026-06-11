---
codeExamples:
  - id: "security-manager-routes"
    title: "JWT + role gates for manager API"
    description: "SecurityConfig permits actuator and Swagger assets, requires ADMIN/MANAGER for /api/v2/users/manager/**, authenticated for other /api/**."
    category: "security"
    duration: "5 min read"
    views: 0
    tags:
      - "Spring Security"
      - "JWT"
    files:
      - name: "SecurityConfig.java"
        path: "user-service/src/main/java/io/github/alexisTrejo11/drugstore/users/config/security/SecurityConfig.java"
        language: "java"
        content: |
          @Bean
          public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
              .csrf(AbstractHttpConfigurer::disable)
              .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
              .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/api/v2/users/manager/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers("/api/**").authenticated()
                .anyRequest().denyAll())
              .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
            return http.build();
          }
        highlighted: true
        explanation: "**Note:** Actuator is permitAll here — harden in production (IP allowlist or auth). Swagger paths are ignored via WebSecurityCustomizer."

  - id: "command-bus-create-user"
    title: "Manager create user → CommandBus"
    description: "HTTP DTO maps to CreateUserCommand with fixed CUSTOMER role for self-service style onboarding by staff."
    category: "application"
    duration: "3 min read"
    views: 0
    tags:
      - "CQRS"
      - "REST"
    files:
      - name: "UserManagerController.java (excerpt)"
        path: "user-service/src/main/java/io/github/alexisTrejo11/drugstore/users/user/adapter/input/rest/UserManagerController.java"
        language: "java"
        content: |
          @PostMapping("/")
          public ResponseEntity<ResponseWrapper<?>> createUser(@Valid @RequestBody UserRequest userRequest) {
            CreateUserCommand command = userRequest.toCommand(UserRole.CUSTOMER);
            CommandResult commandResult = commandBus.dispatch(command);
            var response = ResponseWrapper.success(commandResult.data(), "User created successfully");
            return ResponseEntity.status(201).body(response);
          }
        highlighted: true
        explanation: "**Product note:** Creating users always assigns CUSTOMER — extend if EMPLOYEE provisioning is needed via separate command."

  - id: "kafka-consumer-ack"
    title: "Kafka listener with manual ack"
    description: "Successful path acknowledges; failures log and throw — risking infinite delivery without DLQ."
    category: "messaging"
    duration: "4 min read"
    views: 0
    tags:
      - "Kafka"
      - "Reliability"
    files:
      - name: "UserEventConsumer.java (excerpt)"
        path: "user-service/src/main/java/io/github/alexisTrejo11/drugstore/users/user/adapter/output/messaging/kafka/consumer/UserEventConsumer.java"
        language: "java"
        content: |
          @KafkaListener(topics = "${kafka.topics.user.created}", ...)
          public void consumeUserCreated(@Payload String payload, ..., Acknowledgment acknowledgment) {
            try {
              UserCreatedEvent event = objectMapper.readValue(payload, UserCreatedEvent.class);
              userCreatedEventHandler.handle(event);
              acknowledgment.acknowledge();
            } catch (Exception e) {
              // TODO: retry or DLQ
              throw new RuntimeException("Failed to process UserCreatedEvent", e);
            }
          }
        highlighted: true
        explanation: "**Danger:** operator must monitor consumer lag / errors; poison messages stall partition progress."

  - id: "grpc-not-started"
    title: "gRPC UserGrpcServer stub (no server lifecycle)"
    description: "Implements Generated UserServiceGrpc but repository search shows no ServerBuilder wiring."
    category: "integration"
    duration: "2 min read"
    views: 0
    tags:
      - "gRPC"
      - "Incomplete"
    files:
      - name: "UserGrpcServer.java (class header)"
        path: "user-service/src/main/java/io/github/alexisTrejo11/drugstore/users/user/adapter/output/grpc/server/UserGrpcServer.java"
        language: "java"
        content: |
          @Component
          public class UserGrpcServer extends UserServiceGrpc.UserServiceImplBase {
            // implements RPC methods — no @PostConstruct Netty ServerBuilder found in codebase
          }
        highlighted: false
        explanation: "**Missing:** add spring-grpc starter or lifecycle bean invoking ServerBuilder.forPort(grpcPort).addService(...) .start()."

  - id: "placeholder-integration-test"
    title: "E2E pattern with seeded users + JWT"
    description: "UserServiceE2EIntegrationTest exercises happy paths with H2/Test profile repositories."
    category: "testing"
    duration: "6 min read"
    views: 0
    tags:
      - "JUnit"
      - "MockMvc"
    files:
      - name: "UserServiceE2EIntegrationTest.java"
        path: "user-service/src/test/java/microservice/user_service/UserServiceE2EIntegrationTest.java"
        language: "java"
        content: "// See repo — builds JWT via jjwt Keys.hmacShaKeyFor(jwt.secret) for bearer tokens in tests."
        highlighted: false
        explanation: "Good regression harness; extend with chaos / Kafka testcontainers for messaging paths."
---

# CodeShowCase

Curated excerpts reference real paths under `user-service/`. Inline snippets are abbreviated for documentation; verify against Git for exact line fidelity.
