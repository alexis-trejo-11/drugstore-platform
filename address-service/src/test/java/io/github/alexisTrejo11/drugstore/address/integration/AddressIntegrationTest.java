package io.github.alexisTrejo11.drugstore.address.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.alexisTrejo11.drugstore.address.config.ratelimit.RedisRateLimiter;
import io.github.alexisTrejo11.drugstore.address.entity.AddressEntity;
import io.github.alexisTrejo11.drugstore.address.repository.AddressRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AddressIntegrationTest {

  private static final String USER_ENDPOINT = "/api/v2/user/addresses";
  private static final String ADMIN_ENDPOINT = "/api/v2/addresses/admin";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private AddressRepository addressRepository;

  @Value("${jwt.secret}")
  private String jwtSecret;

  @Value("${jwt.issuer}")
  private String jwtIssuer;

  @MockBean
  private RedisRateLimiter redisRateLimiter;

  @BeforeEach
  void setupRateLimiter() {
    when(redisRateLimiter.isAllowed(anyString(), anyInt(), any(Duration.class))).thenReturn(true);
    when(redisRateLimiter.getRateLimitInfo(anyString(), eq(40)))
        .thenReturn(new RedisRateLimiter.RateLimitInfo(40, 39, 60));
    when(redisRateLimiter.getRateLimitInfo(anyString(), eq(7)))
        .thenReturn(new RedisRateLimiter.RateLimitInfo(7, 6, 60));
    when(redisRateLimiter.getRateLimitInfo(anyString(), anyInt()))
        .thenReturn(new RedisRateLimiter.RateLimitInfo(100, 99, 60));
  }

  @Test
  void createThenGetById_shouldPersistAndReturnAddress_forAuthenticatedCustomer() throws Exception {
    String customerToken = generateAccessToken("user-001", "CUSTOMER");

    String createPayload = """
        {
          "street": "742 Evergreen Terrace",
          "city": "Springfield",
          "state": "IL",
          "country": "US",
          "postalCode": "62704",
          "additionalDetails": "Near park",
          "isDefault": true
        }
        """;

    String createResponse = mockMvc.perform(post(USER_ENDPOINT)
            .header("Authorization", "Bearer " + customerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(createPayload))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.userId").value("user-001"))
        .andExpect(jsonPath("$.data.country").value("US"))
        .andExpect(jsonPath("$.data.id").isNotEmpty())
        .andReturn()
        .getResponse()
        .getContentAsString();

    JsonNode root = objectMapper.readTree(createResponse);
    String addressId = root.path("data").path("id").asText();

    mockMvc.perform(get(USER_ENDPOINT + "/{addressId}", addressId)
            .header("Authorization", "Bearer " + customerToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(addressId))
        .andExpect(jsonPath("$.data.userId").value("user-001"))
        .andExpect(jsonPath("$.data.street").value("742 Evergreen Terrace"))
        .andExpect(jsonPath("$.data.isDefault").value(true));

    UUID storedId = UUID.fromString(addressId);
    assertThat(addressRepository.findByIdAndUserIdAndActiveTrue(storedId, "user-001")).isPresent();
  }

  @Test
  void adminGetById_shouldReturnAddressCreatedByCustomer() throws Exception {
    String customerToken = generateAccessToken("user-002", "CUSTOMER");
    String adminToken = generateAccessToken("admin-001", "ADMIN");

    String createPayload = """
        {
          "street": "1600 Pennsylvania Ave",
          "city": "Washington",
          "state": "DC",
          "country": "US",
          "postalCode": "20500",
          "additionalDetails": "North entrance",
          "isDefault": false
        }
        """;

    String createResponse = mockMvc.perform(post(USER_ENDPOINT)
            .header("Authorization", "Bearer " + customerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(createPayload))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    String addressId = objectMapper.readTree(createResponse).path("data").path("id").asText();

    mockMvc.perform(get(ADMIN_ENDPOINT + "/{id}", addressId)
            .header("Authorization", "Bearer " + adminToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(addressId))
        .andExpect(jsonPath("$.data.userId").value("user-002"))
        .andExpect(jsonPath("$.data.city").value("Washington"));
  }

  @Test
  void delete_shouldSoftDeleteAddressInDatabase() throws Exception {
    String customerToken = generateAccessToken("user-003", "CUSTOMER");

    String createPayload = """
        {
          "street": "10 Downing Street",
          "city": "London",
          "state": "Greater London",
          "country": "UK",
          "postalCode": "SW1A 2AA",
          "additionalDetails": "Prime office",
          "isDefault": false
        }
        """;

    String createResponse = mockMvc.perform(post(USER_ENDPOINT)
            .header("Authorization", "Bearer " + customerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(createPayload))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    String addressId = objectMapper.readTree(createResponse).path("data").path("id").asText();

    mockMvc.perform(delete(USER_ENDPOINT + "/{addressId}", addressId)
            .header("Authorization", "Bearer " + customerToken))
        .andExpect(status().isOk());

    AddressEntity deleted = addressRepository.findById(UUID.fromString(addressId)).orElseThrow();
    assertThat(deleted.isActive()).isFalse();
  }

  @Test
  void shouldRejectRequestWithoutToken() throws Exception {
    mockMvc.perform(get(USER_ENDPOINT))
        .andExpect(status().isForbidden());
  }

  private String generateAccessToken(String userId, String role) {
    Instant now = Instant.now();
    SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

    return Jwts.builder()
        .setId(UUID.randomUUID().toString())
        .setIssuer(jwtIssuer)
        .setSubject(userId)
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(now.plusSeconds(3600)))
        .claim("userId", userId)
        .claim("role", role)
        .claim("type", "access")
        .claim("email", userId + "@example.com")
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }
}
