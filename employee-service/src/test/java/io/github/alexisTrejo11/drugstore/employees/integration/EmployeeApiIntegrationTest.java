package io.github.alexisTrejo11.drugstore.employees.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.alexisTrejo11.drugstore.employees.config.log.AuditLogger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Employee REST API integration (JWT + H2)")
class EmployeeApiIntegrationTest {

  @TestConfiguration
  static class TestBeans {
    @Bean
    AuditLogger auditLogger(ObjectMapper objectMapper) {
      return new AuditLogger(objectMapper);
    }
  }

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  @Value("${jwt.secret}")
  private String jwtSecret;

  private record TestEmployee(String id, String employeeNumber) {}

  private String url(String path) {
    return "http://localhost:" + port + "/api/v2/employees" + path;
  }

  private HttpHeaders authJsonHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(JwtTestTokens.accessToken(jwtSecret, "integration-user", "ADMIN"));
    return headers;
  }

  private String newUniqueEmployeeNumber() {
    int n = ThreadLocalRandom.current().nextInt(1_000, 9_999);
    return "EMP-" + n;
  }

  @Test
  @Order(1)
  void unauthenticatedRequestsAreRejected() {
    ResponseEntity<String> missing = restTemplate.getForEntity(url("/" + UUID_PLACEHOLDER), String.class);
    assertThat(missing.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

    HttpHeaders badToken = new HttpHeaders();
    badToken.setBearerAuth("not-a-valid-jwt");
    ResponseEntity<String> invalid = restTemplate.exchange(
        url("/" + UUID_PLACEHOLDER),
        HttpMethod.GET,
        new HttpEntity<>(badToken),
        String.class);
    assertThat(invalid.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  private static final String UUID_PLACEHOLDER = "00000000-0000-0000-0000-000000000001";

  private TestEmployee createEmployeeForTest() throws Exception {
    String employeeNumber = newUniqueEmployeeNumber();
    Map<String, Object> contact = new LinkedHashMap<>();
    contact.put("email", "integration.case@drugstore.com");
    contact.put("phone", "+15550170");
    contact.put("emergencyContact", "EC");
    contact.put("emergencyPhone", "+15550171");

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("employeeNumber", employeeNumber);
    body.put("firstName", "Integration");
    body.put("lastName", "Tester");
    body.put("dateOfBirth", LocalDate.of(1990, 1, 15).toString());
    body.put("contactInfo", contact);
    body.put("role", "CASHIER");
    body.put("employeeType", "FULL_TIME");
    body.put("department", "Customer Service");
    body.put("storeId", "store-it-1");
    body.put("hireDate", LocalDate.of(2024, 6, 1).toString());
    body.put("hourlyRate", new BigDecimal("19.50"));
    body.put("weeklyHours", 40);
    body.put("createdBy", "admin@integration.test");

    ResponseEntity<String> response = restTemplate.postForEntity(
        url(""),
        new HttpEntity<>(body, authJsonHeaders()),
        String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    JsonNode root = objectMapper.readTree(response.getBody());
    assertThat(root.path("data").path("value").asText()).isNotBlank();
    return new TestEmployee(root.path("data").path("value").asText(), employeeNumber);
  }

  @Test
  @Order(2)
  void createEmployee_persistsAndReturnsId() throws Exception {
    TestEmployee created = createEmployeeForTest();
    assertThat(created.id()).isNotBlank();
  }

  @Test
  @Order(3)
  void getEmployeeById_returnsCreatedRow() throws Exception {
    TestEmployee created = createEmployeeForTest();
    ResponseEntity<String> response = restTemplate.exchange(
        url("/" + created.id()),
        HttpMethod.GET,
        new HttpEntity<>(authJsonHeaders()),
        String.class);

    assertThat(response.getStatusCode())
        .withFailMessage("Unexpected response: %s", response.getBody())
        .isEqualTo(HttpStatus.OK);
    JsonNode root = objectMapper.readTree(response.getBody());
    assertThat(root.path("data").path("employeeNumber").asText()).isEqualTo(created.employeeNumber());
    assertThat(root.path("data").path("firstName").asText()).isEqualTo("Integration");
  }

  @Test
  @Order(4)
  void getEmployeeByNumber_andSearch_andExistsEndpoints() throws Exception {
    TestEmployee created = createEmployeeForTest();
    ResponseEntity<String> byNumber = restTemplate.exchange(
        url("/by-number/" + created.employeeNumber()),
        HttpMethod.GET,
        new HttpEntity<>(authJsonHeaders()),
        String.class);
    assertThat(byNumber.getStatusCode()).isEqualTo(HttpStatus.OK);

    ResponseEntity<String> search = restTemplate.exchange(
        url("?employeeNumber=" + created.employeeNumber() + "&page=0&size=10"),
        HttpMethod.GET,
        new HttpEntity<>(authJsonHeaders()),
        String.class);
    assertThat(search.getStatusCode()).isEqualTo(HttpStatus.OK);
    JsonNode searchRoot = objectMapper.readTree(search.getBody());
    assertThat(searchRoot.path("data").path("content").isArray()).isTrue();
    assertThat(searchRoot.path("data").path("content").isEmpty()).isFalse();

    ResponseEntity<String> existsId = restTemplate.exchange(
        url("/exists/id/" + created.id()),
        HttpMethod.GET,
        new HttpEntity<>(authJsonHeaders()),
        String.class);
    assertThat(existsId.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(objectMapper.readTree(existsId.getBody()).path("data").asBoolean()).isTrue();

    ResponseEntity<String> existsNum = restTemplate.exchange(
        url("/exists/number/" + created.employeeNumber()),
        HttpMethod.GET,
        new HttpEntity<>(authJsonHeaders()),
        String.class);
    assertThat(existsNum.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(objectMapper.readTree(existsNum.getBody()).path("data").asBoolean()).isTrue();
  }

  @Test
  @Order(5)
  void updateEmployee() throws Exception {
    TestEmployee created = createEmployeeForTest();
    Map<String, Object> contact = new LinkedHashMap<>();
    contact.put("email", "updated.integration@drugstore.com");
    contact.put("phone", "+15550270");
    contact.put("emergencyContact", "EC2");
    contact.put("emergencyPhone", "+15550271");

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("firstName", "Integration");
    body.put("lastName", "Updated");
    body.put("dateOfBirth", LocalDate.of(1990, 1, 15).toString());
    body.put("contactInfo", contact);
    body.put("updatedBy", "hr@integration.test");

    ResponseEntity<String> response = restTemplate.exchange(
        url("/" + created.id()),
        HttpMethod.PUT,
        new HttpEntity<>(body, authJsonHeaders()),
        String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  @Order(6)
  void addCertification_andChangeRole() throws Exception {
    TestEmployee created = createEmployeeForTest();
    Map<String, Object> cert = new LinkedHashMap<>();
    cert.put("type", "CPR_CERTIFICATION");
    cert.put("licenseNumber", "CPR-IT-001");
    cert.put("issuingAuthority", "AHA");
    cert.put("issueDate", LocalDate.of(2024, 1, 1).toString());
    cert.put("expirationDate", LocalDate.now().plusYears(2).toString());
    cert.put("addedBy", "admin@integration.test");

    ResponseEntity<String> addCert = restTemplate.postForEntity(
        url("/" + created.id() + "/certifications"),
        new HttpEntity<>(cert, authJsonHeaders()),
        String.class);
    assertThat(addCert.getStatusCode())
        .withFailMessage("Unexpected addCert response: %s", addCert.getBody())
        .isEqualTo(HttpStatus.OK);

    Map<String, Object> roleBody = new LinkedHashMap<>();
    roleBody.put("newRole", "STORE_MANAGER");
    roleBody.put("reason", "Promotion for integration test");
    roleBody.put("approvedBy", "hr@integration.test");

    ResponseEntity<String> role = restTemplate.exchange(
        url("/" + created.id() + "/role"),
        HttpMethod.PATCH,
        new HttpEntity<>(roleBody, authJsonHeaders()),
        String.class);
    assertThat(role.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(objectMapper.readTree(role.getBody()).path("error").path("errorType").asText())
        .isEqualTo("DataIntegrityViolationException");
  }

  @Test
  @Order(7)
  void changeStatus_andCompensation() throws Exception {
    TestEmployee created = createEmployeeForTest();
    Map<String, Object> statusBody = new LinkedHashMap<>();
    statusBody.put("newStatus", "INACTIVE");
    statusBody.put("reason", "Temporary");
    statusBody.put("changedBy", "hr@integration.test");

    ResponseEntity<String> st = restTemplate.exchange(
        url("/" + created.id() + "/status"),
        HttpMethod.PATCH,
        new HttpEntity<>(statusBody, authJsonHeaders()),
        String.class);
    assertThat(st.getStatusCode()).isEqualTo(HttpStatus.OK);

    Map<String, Object> activeAgain = new LinkedHashMap<>();
    activeAgain.put("newStatus", "ACTIVE");
    activeAgain.put("reason", "Back to work");
    activeAgain.put("changedBy", "hr@integration.test");
    ResponseEntity<String> st2 = restTemplate.exchange(
        url("/" + created.id() + "/status"),
        HttpMethod.PATCH,
        new HttpEntity<>(activeAgain, authJsonHeaders()),
        String.class);
    assertThat(st2.getStatusCode()).isEqualTo(HttpStatus.OK);

    Map<String, Object> comp = new LinkedHashMap<>();
    comp.put("hourlyRate", new BigDecimal("21.00"));
    comp.put("weeklyHours", 40);
    comp.put("updatedBy", "payroll@integration.test");

    ResponseEntity<String> compensation = restTemplate.exchange(
        url("/" + created.id() + "/compensation"),
        HttpMethod.PATCH,
        new HttpEntity<>(comp, authJsonHeaders()),
        String.class);
    assertThat(compensation.getStatusCode())
        .withFailMessage("Unexpected compensation response: %s", compensation.getBody())
        .isEqualTo(HttpStatus.OK);
  }

  @Test
  @Order(8)
  void suspend_activate_onLeave_thenRecoverLifecycle() throws Exception {
    TestEmployee created = createEmployeeForTest();
    ResponseEntity<String> suspend = restTemplate.exchange(
        url("/" + created.id() + "/suspend?reason=Policy&suspendedBy=hr@integration.test"),
        HttpMethod.PATCH,
        new HttpEntity<>(authJsonHeaders()),
        String.class);
    assertThat(suspend.getStatusCode()).isEqualTo(HttpStatus.OK);

    ResponseEntity<String> activate = restTemplate.exchange(
        url("/" + created.id() + "/activate?reason=Cleared&activatedBy=hr@integration.test"),
        HttpMethod.PATCH,
        new HttpEntity<>(authJsonHeaders()),
        String.class);
    assertThat(activate.getStatusCode()).isEqualTo(HttpStatus.OK);

    ResponseEntity<String> leave = restTemplate.exchange(
        url("/" + created.id() + "/on-leave?reason=FMLA&approvedBy=hr@integration.test"),
        HttpMethod.PATCH,
        new HttpEntity<>(authJsonHeaders()),
        String.class);
    assertThat(leave.getStatusCode()).isEqualTo(HttpStatus.OK);

    Map<String, Object> activeAgain = new LinkedHashMap<>();
    activeAgain.put("newStatus", "ACTIVE");
    activeAgain.put("reason", "Returned");
    activeAgain.put("changedBy", "hr@integration.test");
    ResponseEntity<String> st = restTemplate.exchange(
        url("/" + created.id() + "/status"),
        HttpMethod.PATCH,
        new HttpEntity<>(activeAgain, authJsonHeaders()),
        String.class);
    assertThat(st.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  @Order(9)
  void softDelete_restore_andQueriesReflectDeletion() throws Exception {
    TestEmployee created = createEmployeeForTest();
    ResponseEntity<String> del = restTemplate.exchange(
        url("/" + created.id() + "?deletedBy=hr@integration.test"),
        HttpMethod.DELETE,
        new HttpEntity<>(authJsonHeaders()),
        String.class);
    assertThat(del.getStatusCode()).isEqualTo(HttpStatus.OK);

    ResponseEntity<String> missing = restTemplate.exchange(
        url("/" + created.id()),
        HttpMethod.GET,
        new HttpEntity<>(authJsonHeaders()),
        String.class);
    assertThat(missing.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(objectMapper.readTree(missing.getBody()).path("error").path("errorType").asText())
        .isEqualTo("EmployeeNotFoundException");

    ResponseEntity<String> existsAfterDel = restTemplate.exchange(
        url("/exists/id/" + created.id()),
        HttpMethod.GET,
        new HttpEntity<>(authJsonHeaders()),
        String.class);
    assertThat(existsAfterDel.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(objectMapper.readTree(existsAfterDel.getBody()).path("data").asBoolean()).isFalse();

    ResponseEntity<String> restore = restTemplate.exchange(
        url("/" + created.id() + "/restore?restoredBy=hr@integration.test"),
        HttpMethod.PATCH,
        new HttpEntity<>(authJsonHeaders()),
        String.class);
    assertThat(restore.getStatusCode()).isEqualTo(HttpStatus.OK);

    ResponseEntity<String> back = restTemplate.exchange(
        url("/" + created.id()),
        HttpMethod.GET,
        new HttpEntity<>(authJsonHeaders()),
        String.class);
    assertThat(back.getStatusCode()).isEqualTo(HttpStatus.OK);
  }
}
