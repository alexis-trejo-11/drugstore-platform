package io.github.alexisTrejo11.drugstore.accounts.integration;

import java.io.IOException;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.warrenstrange.googleauth.GoogleAuthenticator;

import io.github.alexisTrejo11.drugstore.accounts.AuthServiceApplication;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.Token;
import io.github.alexisTrejo11.drugstore.accounts.integration.support.InMemoryUserGrpcServer;
import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Full-stack HTTP integration tests with real Redis, Kafka, and an in-process gRPC
 * UserService (BCrypt + in-memory users). Login flows exercise registration → Redis
 * opaque tokens → activation → credential validation via gRPC.
 */
@SpringBootTest(classes = AuthServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"integration-test", "test"})
@Testcontainers(disabledWithoutDocker = true)
class AuthEndpointsIntegrationTest {

  static final InMemoryUserGrpcServer USER_GRPC = new InMemoryUserGrpcServer();
  static Server grpcServer;

  static {
    try {
      grpcServer = NettyServerBuilder.forPort(0).addService(USER_GRPC).build().start();
    } catch (IOException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  static int grpcPort() {
    return grpcServer.getPort();
  }

  @Container
  static final GenericContainer<?> REDIS =
      new GenericContainer<>(DockerImageName.parse("redis:7-alpine")).withExposedPorts(6379);

  @Container
  static final KafkaContainer KAFKA =
      new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.1"));

  @DynamicPropertySource
  static void registerProps(DynamicPropertyRegistry r) {
    r.add("spring.data.redis.url",
        () -> "redis://" + REDIS.getHost() + ":" + REDIS.getMappedPort(6379));
    r.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
    r.add("grpc.client.user-service.host", () -> "localhost");
    r.add("grpc.client.user-service.port", () -> grpcPort());
  }

  @LocalServerPort int port;

  @Autowired TestRestTemplate restTemplate;
  @Autowired RedisTemplate<String, Object> redisTemplate;
  @Autowired ObjectMapper objectMapper;

  @BeforeEach
  void cleanState() {
    USER_GRPC.clear();
    Objects.requireNonNull(redisTemplate.getConnectionFactory())
        .getConnection()
        .serverCommands()
        .flushAll();
  }

  @AfterAll
  static void stopGrpc() throws InterruptedException {
    if (grpcServer != null) {
      grpcServer.shutdown();
      if (!grpcServer.awaitTermination(15, TimeUnit.SECONDS)) {
        grpcServer.shutdownNow();
      }
    }
  }

  private String baseUrl() {
    return "http://localhost:" + port;
  }

  private JsonNode postJson(String path, Object body, HttpHeaders extraHeaders) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    if (extraHeaders != null) {
      extraHeaders.forEach(headers::addAll);
    }
    ResponseEntity<String> res =
        restTemplate.exchange(
            baseUrl() + path,
            HttpMethod.POST,
            new HttpEntity<>(body, headers),
            String.class);
    assertThat(res.getStatusCode().is2xxSuccessful())
        .as("POST %s expected 2xx got %s body=%s", path, res.getStatusCode(), res.getBody())
        .isTrue();
    try {
      return objectMapper.readTree(res.getBody());
    } catch (Exception e) {
      throw new AssertionError("Invalid JSON for " + path + ": " + res.getBody(), e);
    }
  }

  private JsonNode putJson(String path, Object body, HttpHeaders auth) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    if (auth != null) {
      auth.forEach(headers::addAll);
    }
    ResponseEntity<String> res =
        restTemplate.exchange(baseUrl() + path, HttpMethod.PUT, new HttpEntity<>(body, headers), String.class);
    assertThat(res.getStatusCode().is2xxSuccessful())
        .as("PUT %s got %s body=%s", path, res.getStatusCode(), res.getBody())
        .isTrue();
    try {
      return objectMapper.readTree(res.getBody());
    } catch (Exception e) {
      throw new AssertionError("Invalid JSON for " + path, e);
    }
  }

  private HttpHeaders bearer(String accessJwt) {
    HttpHeaders h = new HttpHeaders();
    h.setBearerAuth(accessJwt);
    return h;
  }

  private String randomEmail() {
    return "it-" + UUID.randomUUID() + "@example.com";
  }

  private String randomPhone() {
    return "+1555" + String.format("%07d", (int) (Math.random() * 9_000_000) + 1_000_000);
  }

  private Map<String, Object> signupBody(String email, String phone, String password) {
    Map<String, Object> m = new LinkedHashMap<>();
    m.put("email", email);
    m.put("phone", phone);
    m.put("password", password);
    m.put("firstName", "Integration");
    m.put("lastName", "User");
    m.put("dateOfBirth", "1990-01-01");
    m.put("gender", "x");
    return m;
  }

  private String registerCustomer(String email, String phone, String password) {
    JsonNode root =
        postJson("/api/v2/auth/register/customer", signupBody(email, phone, password), null);
    return root.path("data").path("userId").asText();
  }

  private void activate(String userId, String code) {
    Map<String, String> body = Map.of("activationCode", code);
    postJson("/api/v2/auth/activate", body, null);
  }

  private String findOpaqueToken(String userId, String type) {
    String setKey = "user:tokens:" + userId;
    Set<Object> codes = redisTemplate.opsForSet().members(setKey);
    if (codes == null) {
      return null;
    }
    for (Object codeObj : codes) {
      String code = String.valueOf(codeObj);
      Object raw = redisTemplate.opsForValue().get("token:" + code);
      if (!(raw instanceof Token t)) {
        continue;
      }
      if (type.equals(t.type())) {
        return t.code();
      }
    }
    return null;
  }

  private void waitForOpaqueToken(String userId, String type) {
    assertThat(await(() -> findOpaqueToken(userId, type) != null, Duration.ofSeconds(15)))
        .as("token type %s for user %s", type, userId)
        .isTrue();
  }

  private boolean await(java.util.function.BooleanSupplier condition, Duration timeout) {
    long deadline = System.nanoTime() + timeout.toNanos();
    while (System.nanoTime() < deadline) {
      if (condition.getAsBoolean()) {
        return true;
      }
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return false;
      }
    }
    return condition.getAsBoolean();
  }

  private JsonNode loginJson(String emailOrPhone, String password) {
    Map<String, String> body = new LinkedHashMap<>();
    body.put("emailOrPhoneNumber", emailOrPhone);
    body.put("password", password);
    body.put("deviceId", "itest-device");
    body.put("ipAddress", "127.0.0.1");
    return postJson("/api/v2/auth/login", body, null);
  }

  private String totpCode(String secretBase32) {
    int code = new GoogleAuthenticator().getTotpPassword(secretBase32);
    return String.format("%06d", code);
  }

  @Test
  void customer_fullAuthFlow_registerActivateLoginRefreshLogout() {
    String email = randomEmail();
    String phone = randomPhone();
    String password = "secretpass1";

    String userId = registerCustomer(email, phone, password);
    assertThat(userId).isNotBlank();

    waitForOpaqueToken(userId, "ACTIVATION");
    String activation = Objects.requireNonNull(findOpaqueToken(userId, "ACTIVATION"));
    activate(userId, activation);

    JsonNode login = loginJson(email, password);
    JsonNode data = login.path("data");
    assertThat(data.path("requiresTwoFactor").asBoolean()).isFalse();
    String access = data.path("accessToken").path("token").asText();
    String refresh = data.path("refreshToken").path("token").asText();
    assertThat(access).isNotBlank();
    assertThat(refresh).isNotBlank();

    JsonNode refreshed =
        postJson("/api/v2/auth/session/refresh", Map.of("refreshToken", refresh), null);
    assertThat(refreshed.path("data").path("accessToken").path("token").asText()).isNotBlank();

    JsonNode logout =
        postJson("/api/v2/auth/logout", Map.of("refreshToken", refresh), null);
    assertThat(logout.path("message").asText()).isNotBlank();
  }

  @Test
  void employee_and_admin_registration_paths_succeed() {
    String e1 = randomEmail();
    String p1 = randomPhone();
    JsonNode emp =
        postJson("/api/v2/auth/register/employee", signupBody(e1, p1, "secretpass1"), null);
    assertThat(emp.path("data").path("userId").asText()).isNotBlank();

    String e2 = randomEmail();
    String p2 = randomPhone();
    JsonNode adm =
        postJson("/api/v2/auth/register/admin", signupBody(e2, p2, "secretpass1"), null);
    assertThat(adm.path("data").path("userId").asText()).isNotBlank();
  }

  @Test
  void passwordForgotValidateReset_thenLoginWithNewPassword() {
    String email = randomEmail();
    String phone = randomPhone();
    String oldPass = "oldpass12";
    String newPass = "newpass12";

    String userId = registerCustomer(email, phone, oldPass);
    waitForOpaqueToken(userId, "ACTIVATION");
    activate(userId, Objects.requireNonNull(findOpaqueToken(userId, "ACTIVATION")));

    postJson("/api/v2/auth/password/forgot", Map.of("email", email), null);
    waitForOpaqueToken(userId, "PASSWORD_RESET");
    String resetCode = Objects.requireNonNull(findOpaqueToken(userId, "PASSWORD_RESET"));

    postJson("/api/v2/auth/password/validate-token", Map.of("token", resetCode), null);

    Map<String, String> resetBody = new LinkedHashMap<>();
    resetBody.put("token", resetCode);
    resetBody.put("newPassword", newPass);
    resetBody.put("confirmPassword", newPass);
    postJson("/api/v2/auth/password/reset", resetBody, null);

    JsonNode login = loginJson(email, newPass);
    assertThat(login.path("data").path("accessToken").path("token").asText()).isNotBlank();
  }

  @Test
  void authenticated_changePassword_updateCredentials_logoutAll() {
    String email = randomEmail();
    String phone = randomPhone();
    String password = "secretpass1";

    String userId = registerCustomer(email, phone, password);
    waitForOpaqueToken(userId, "ACTIVATION");
    activate(userId, Objects.requireNonNull(findOpaqueToken(userId, "ACTIVATION")));

    JsonNode login = loginJson(email, password);
    String access = login.path("data").path("accessToken").path("token").asText();
    HttpHeaders auth = bearer(access);

    Map<String, String> change = new LinkedHashMap<>();
    change.put("currentPassword", password);
    change.put("newPassword", "nextpass12");
    change.put("confirmPassword", "nextpass12");
    putJson("/api/v2/auth/password/change", change, auth);

    Map<String, String> creds = new LinkedHashMap<>();
    creds.put("email", email);
    creds.put("phone", phone);
    putJson("/api/v2/auth/password/credentials", creds, auth);

    HttpHeaders logoutHeaders = new HttpHeaders();
    logoutHeaders.addAll(auth);
    ResponseEntity<String> logoutAll =
        restTemplate.exchange(
            baseUrl() + "/api/v2/auth/logout-all",
            HttpMethod.POST,
            new HttpEntity<Void>(null, logoutHeaders),
            String.class);
    assertThat(logoutAll.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void twoFactor_enableConfirm_loginWithOtpChallenge_disable() {
    String email = randomEmail();
    String phone = randomPhone();
    String password = "secretpass1";

    String userId = registerCustomer(email, phone, password);
    waitForOpaqueToken(userId, "ACTIVATION");
    activate(userId, Objects.requireNonNull(findOpaqueToken(userId, "ACTIVATION")));

    JsonNode firstLogin = loginJson(email, password);
    String access = firstLogin.path("data").path("accessToken").path("token").asText();
    HttpHeaders auth = bearer(access);

    JsonNode enable = postJson("/api/v2/auth/2fa/enable", Map.of(), auth);
    String secret = enable.path("data").path("secret").asText();
    assertThat(secret).isNotBlank();

    String enrollTotp = totpCode(secret);
    postJson("/api/v2/auth/2fa/confirm", Map.of("code", enrollTotp), auth);

    JsonNode pending = loginJson(email, password);
    assertThat(pending.path("data").path("requiresTwoFactor").asBoolean()).isTrue();

    waitForOpaqueToken(userId, "TWO_FA");
    String otpLogin = Objects.requireNonNull(findOpaqueToken(userId, "TWO_FA"));

    Map<String, String> twoFaBody = new LinkedHashMap<>();
    twoFaBody.put("email", email);
    twoFaBody.put("twoFactorCode", otpLogin);
    twoFaBody.put("deviceId", "itest");
    twoFaBody.put("ipAddress", "127.0.0.1");
    JsonNode after2fa = postJson("/api/v2/auth/login/2fa", twoFaBody, null);
    String access2 = after2fa.path("data").path("accessToken").path("token").asText();
    assertThat(access2).isNotBlank();

    postJson("/api/v2/auth/2fa/disable", Map.of(), bearer(access2));

    JsonNode plainAgain = loginJson(email, password);
    assertThat(plainAgain.path("data").path("requiresTwoFactor").asBoolean()).isFalse();
  }

  @Test
  void twoFa_sendCode_and_verifyCode_roundTrip() {
    String email = randomEmail();
    String phone = randomPhone();
    String password = "secretpass1";

    String userId = registerCustomer(email, phone, password);
    waitForOpaqueToken(userId, "ACTIVATION");
    activate(userId, Objects.requireNonNull(findOpaqueToken(userId, "ACTIVATION")));

    JsonNode login = loginJson(email, password);
    String access = login.path("data").path("accessToken").path("token").asText();
    HttpHeaders auth = bearer(access);

    postJson("/api/v2/auth/2fa/enable", Map.of(), auth);
    postJson("/api/v2/auth/2fa/send-code", Map.of(), auth);

    waitForOpaqueToken(userId, "TWO_FA");
    String code = Objects.requireNonNull(findOpaqueToken(userId, "TWO_FA"));

    postJson("/api/v2/auth/2fa/verify-code", Map.of("code", code), auth);
  }
}
