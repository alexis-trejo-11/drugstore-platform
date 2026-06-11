package io.github.alexisTrejo11.drugstore.products;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.Product;
import io.github.alexisTrejo11.drugstore.products.core.port.output.ProductEventPublisher;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import javax.crypto.SecretKey;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ProductApiIntegrationTest {

  private static final String BASE_PATH = "/api/v2/products";
  private static final String JWT_SECRET = "test-secret-key-for-jwt-signing-which-is-long-enough-123456";

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void searchProducts_shouldReturnOkForPublicEndpoint() throws Exception {
    ResponseEntity<String> response = restTemplate.getForEntity(BASE_PATH, String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    JsonNode body = objectMapper.readTree(response.getBody());
    assertTrue(body.path("message").asText().contains("found"));
    assertTrue(body.path("data").has("content"));
  }

  @Test
  void getCategories_shouldReturnOkForPublicEndpoint() throws Exception {
    ResponseEntity<String> response = restTemplate.getForEntity(BASE_PATH + "/categories", String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    JsonNode body = objectMapper.readTree(response.getBody());
    assertTrue(body.path("data").isArray());
    assertFalse(body.path("data").isEmpty());
  }

  @Test
  void productCrudAndLookups_shouldWorkWithValidJwt() throws Exception {
    Map<String, Object> createRequest = buildCreateRequest();

    ResponseEntity<String> createResponse = restTemplate.exchange(
        BASE_PATH,
        HttpMethod.POST,
        jsonEntity(createRequest, bearerToken("ADMIN")),
        String.class);

    assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
    String productId = extractProductId(createResponse.getBody());
    assertNotNull(productId);

    ResponseEntity<String> getByIdResponse = restTemplate.getForEntity(BASE_PATH + "/" + productId, String.class);
    assertEquals(HttpStatus.OK, getByIdResponse.getStatusCode());

    String sku = createRequest.get("sku").toString();
    ResponseEntity<String> getBySkuResponse = restTemplate.getForEntity(BASE_PATH + "/sku/" + sku, String.class);
    assertEquals(HttpStatus.OK, getBySkuResponse.getStatusCode());

    String barcode = createRequest.get("barcode").toString();
    ResponseEntity<String> getByBarcodeResponse = restTemplate.getForEntity(BASE_PATH + "/barcode/" + barcode, String.class);
    assertEquals(HttpStatus.OK, getByBarcodeResponse.getStatusCode());

    Map<String, Object> updateRequest = new HashMap<>();
    updateRequest.put("name", "Updated Product Name");
    updateRequest.put("price", new BigDecimal("49.90"));
    updateRequest.put("description", "Updated description");

    ResponseEntity<String> updateResponse = restTemplate.exchange(
        BASE_PATH + "/" + productId,
        HttpMethod.PUT,
        jsonEntity(updateRequest, bearerToken("MANAGER")),
        String.class);
    assertEquals(HttpStatus.OK, updateResponse.getStatusCode());

    ResponseEntity<String> deleteResponse = restTemplate.exchange(
        BASE_PATH + "/" + productId,
        HttpMethod.DELETE,
        jsonEntity(null, bearerToken("ADMIN")),
        String.class);
    assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());

    ResponseEntity<String> restoreResponse = restTemplate.exchange(
        BASE_PATH + "/" + productId + "/restore",
        HttpMethod.PATCH,
        jsonEntity(null, bearerToken("MANAGER")),
        String.class);
    assertEquals(HttpStatus.OK, restoreResponse.getStatusCode());
  }

  @Test
  void createProduct_shouldReturnUnauthorizedWithoutToken() {
    ResponseEntity<String> response = restTemplate.exchange(
        BASE_PATH,
        HttpMethod.POST,
        jsonEntity(buildCreateRequest(), null),
        String.class);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }

  @Test
  void createProduct_shouldReturnForbiddenForInvalidRole() {
    ResponseEntity<String> response = restTemplate.exchange(
        BASE_PATH,
        HttpMethod.POST,
        jsonEntity(buildCreateRequest(), bearerToken("CUSTOMER")),
        String.class);

    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
  }

  @Test
  void getProductById_shouldReturnNotFoundForUnknownProduct() {
    ResponseEntity<String> response = restTemplate.getForEntity(
        BASE_PATH + "/" + UUID.randomUUID(),
        String.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void createProduct_shouldReturnValidationErrorForInvalidPayload() {
    Map<String, Object> invalidRequest = new HashMap<>(buildCreateRequest());
    invalidRequest.put("name", "");

    ResponseEntity<String> response = restTemplate.exchange(
        BASE_PATH,
        HttpMethod.POST,
        jsonEntity(invalidRequest, bearerToken("ADMIN")),
        String.class);

    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
  }

  private HttpEntity<Object> jsonEntity(Object body, String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    if (token != null) {
      headers.setBearerAuth(token);
    }
    return new HttpEntity<>(body, headers);
  }

  private Map<String, Object> buildCreateRequest() {
    String suffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    String numericBarcodeSuffix = String.format("%06d", ThreadLocalRandom.current().nextInt(0, 1_000_000));
    Map<String, Object> request = new HashMap<>();
    request.put("sku", "MED-T" + suffix);
    request.put("name", "Integration Test Product " + suffix);
    request.put("description", "Created from integration tests");
    request.put("activeIngredient", "Paracetamol");
    request.put("manufacturer", "Test Labs");
    request.put("type", "MEDICATION");
    request.put("category", "ANALGESICS");
    request.put("price", new BigDecimal("29.90"));
    request.put("barcode", "90000" + numericBarcodeSuffix);
    request.put("images", List.of("https://example.com/product.png"));
    request.put("expirationMinMonths", 12);
    request.put("expirationMaxMonths", 24);
    request.put("requiresPrescription", false);
    request.put("status", "ACTIVE");
    request.put("contraindications", List.of("Do not exceed dosage"));
    request.put("dosage", "500mg");
    request.put("administration", "ORAL");
    return request;
  }

  private String extractProductId(String responseBody) throws Exception {
    JsonNode body = objectMapper.readTree(responseBody);
    JsonNode data = body.path("data");

    if (data.isObject() && data.has("value")) {
      return data.path("value").asText();
    }
    if (data.isTextual()) {
      return data.asText();
    }
    return null;
  }

  private String bearerToken(String role) {
    SecretKey key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
    Instant now = Instant.now();

    return Jwts.builder()
        .setId(UUID.randomUUID().toString())
        .setSubject("integration-test-user")
        .setIssuedAt(java.util.Date.from(now))
        .setExpiration(java.util.Date.from(now.plusSeconds(3600)))
        .claim("userId", UUID.randomUUID().toString())
        .claim("role", role)
        .claim("email", "integration@test.dev")
        .claim("type", "access")
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  @TestConfiguration
  static class TestOverrides {
    @Bean
    @Primary
    ProductEventPublisher productEventPublisher() {
      return new ProductEventPublisher() {
        @Override
        public void publishProductCreated(Product product) {
        }

        @Override
        public void publishProductUpdated(Product product) {
        }

        @Override
        public void publishProductDeleted(String productId, Product product) {
        }
      };
    }
  }
}
