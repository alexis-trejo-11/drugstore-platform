package microservice.user_service;

import io.github.alexisTrejo11.drugstore.users.UserServiceApplication;
import io.github.alexisTrejo11.drugstore.users.user.adapter.output.persistence.jpa.ProfileJpaRepository;
import io.github.alexisTrejo11.drugstore.users.user.adapter.output.persistence.jpa.UserJpaRepository;
import io.github.alexisTrejo11.drugstore.users.user.adapter.output.persistence.models.ProfileModel;
import io.github.alexisTrejo11.drugstore.users.user.adapter.output.persistence.models.UserModel;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.enums.Gender;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.enums.UserRole;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.enums.UserStatus;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = UserServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserServiceE2EIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserJpaRepository userJpaRepository;

  @Autowired
  private ProfileJpaRepository profileJpaRepository;

  @Value("${jwt.secret}")
  private String jwtSecret;

  @BeforeEach
  void cleanDb() {
    userJpaRepository.deleteAll();
    profileJpaRepository.deleteAll();
  }

  @Test
  void getUserById_happyPath() throws Exception {
    UserModel user = seedUser("john.byid@example.com", "+15550010001", UserRole.CUSTOMER, UserStatus.ACTIVE, true);

    mockMvc.perform(get("/api/v2/users/{id}", user.getId())
            .header("Authorization", bearerToken(user.getId(), "CUSTOMER", user.getEmail())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(user.getId()))
        .andExpect(jsonPath("$.data.email").value(user.getEmail()));
  }

  @Test
  void getUserByEmail_happyPath() throws Exception {
    UserModel user = seedUser("john.byemail@example.com", "+15550010002", UserRole.CUSTOMER, UserStatus.ACTIVE, true);

    mockMvc.perform(get("/api/v2/users/by-email/{email}", user.getEmail())
            .header("Authorization", bearerToken(user.getId(), "CUSTOMER", user.getEmail())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.email").value(user.getEmail()));
  }

  @Test
  void getUserByPhone_happyPath() throws Exception {
    UserModel user = seedUser("john.byphone@example.com", "+15550010003", UserRole.CUSTOMER, UserStatus.ACTIVE, true);

    mockMvc.perform(get("/api/v2/users/by-phone/{phone}", user.getPhoneNumber())
            .header("Authorization", bearerToken(user.getId(), "CUSTOMER", user.getEmail())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.phoneNumber").value(user.getPhoneNumber()));
  }

  @Test
  void getUsersByRole_happyPath() throws Exception {
    UserModel admin = seedUser("admin.role@example.com", "+15550010004", UserRole.ADMIN, UserStatus.ACTIVE, false);
    seedUser("customer.role@example.com", "+15550010005", UserRole.CUSTOMER, UserStatus.ACTIVE, false);

    mockMvc.perform(get("/api/v2/users/by-role/{role}", "ADMIN")
            .param("page", "1")
            .param("size", "10")
            .header("Authorization", bearerToken(admin.getId(), "ADMIN", admin.getEmail())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.content", hasSize(1)))
        .andExpect(jsonPath("$.data.content[0].role").value("ADMIN"));
  }

  @Test
  void getUsersByStatus_happyPath() throws Exception {
    UserModel admin = seedUser("admin.status@example.com", "+15550010006", UserRole.ADMIN, UserStatus.ACTIVE, false);
    seedUser("pending.status@example.com", "+15550010007", UserRole.CUSTOMER, UserStatus.PENDING, false);

    mockMvc.perform(get("/api/v2/users/by-status/{status}", "PENDING")
            .param("page", "1")
            .param("size", "10")
            .header("Authorization", bearerToken(admin.getId(), "ADMIN", admin.getEmail())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.content", hasSize(1)))
        .andExpect(jsonPath("$.data.content[0].email").value("pending.status@example.com"));
  }

  @Test
  void managerEndpoints_requireAdminOrManagerRole() throws Exception {
    UserModel customer = seedUser("customer.forbidden@example.com", "+15550010008", UserRole.CUSTOMER, UserStatus.ACTIVE, false);

    mockMvc.perform(post("/api/v2/users/manager/")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"email":"new.user@example.com","password":"SecureP@ss123"}
                """)
            .header("Authorization", bearerToken(customer.getId(), "CUSTOMER", customer.getEmail())))
        .andExpect(status().isForbidden());
  }

  @Test
  void createUser_validationError() throws Exception {
    UserModel admin = seedUser("admin.validation@example.com", "+15550010009", UserRole.ADMIN, UserStatus.ACTIVE, false);

    mockMvc.perform(post("/api/v2/users/manager/")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"email":"invalid-email","password":"123"}
                """)
            .header("Authorization", bearerToken(admin.getId(), "ADMIN", admin.getEmail())))
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  void banUnbanDeleteAndActivate_happyPaths() throws Exception {
    UserModel manager = seedUser("manager.actions@example.com", "+15550010010", UserRole.ADMIN, UserStatus.ACTIVE, false);
    UserModel pending = seedUser("pending.user@example.com", "+15550010011", UserRole.CUSTOMER, UserStatus.PENDING, false);
    UserModel active = seedUser("active.user@example.com", "+15550010012", UserRole.CUSTOMER, UserStatus.ACTIVE, false);

    mockMvc.perform(patch("/api/v2/users/manager/{id}/activate/code/{code}", pending.getId(), "123456")
            .header("Authorization", bearerToken(manager.getId(), "MANAGER", manager.getEmail())))
        .andExpect(status().isOk());

    mockMvc.perform(patch("/api/v2/users/manager/{id}/ban", active.getId())
            .header("Authorization", bearerToken(manager.getId(), "MANAGER", manager.getEmail())))
        .andExpect(status().isOk());

    mockMvc.perform(patch("/api/v2/users/manager/{id}/unban", active.getId())
            .header("Authorization", bearerToken(manager.getId(), "MANAGER", manager.getEmail())))
        .andExpect(status().isOk());

    mockMvc.perform(delete("/api/v2/users/manager/{id}", active.getId())
            .header("Authorization", bearerToken(manager.getId(), "MANAGER", manager.getEmail())))
        .andExpect(status().isOk());

    Optional<UserModel> activated = userJpaRepository.findById(pending.getId());
    Optional<UserModel> deleted = userJpaRepository.findById(active.getId());

    org.junit.jupiter.api.Assertions.assertTrue(activated.isPresent());
    org.junit.jupiter.api.Assertions.assertEquals(UserStatus.ACTIVE, activated.get().getStatus());
    org.junit.jupiter.api.Assertions.assertTrue(deleted.isEmpty());
  }

  @Test
  void profileMe_happyPath() throws Exception {
    UserModel user = seedUser("profile.me@example.com", "+15550010013", UserRole.CUSTOMER, UserStatus.ACTIVE, true);

    mockMvc.perform(get("/api/v2/users/profile/me")
            .header("Authorization", bearerToken(user.getId(), "CUSTOMER", user.getEmail())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.firstName").value("John"))
        .andExpect(jsonPath("$.data.lastName").value("Doe"));
  }

  @Test
  void updateProfile_happyPath() throws Exception {
    UserModel user = seedUser("profile.update@example.com", "+15550010014", UserRole.CUSTOMER, UserStatus.ACTIVE, true);

    mockMvc.perform(patch("/api/v2/users/profile")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "firstName": "Alice",
                  "lastName": "Smith",
                  "bio": "Updated bio"
                }
                """)
            .header("Authorization", bearerToken(user.getId(), "CUSTOMER", user.getEmail())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.firstName").value("Alice"))
        .andExpect(jsonPath("$.data.lastName").value("Smith"))
        .andExpect(jsonPath("$.data.bio").value("Updated bio"));
  }

  @Test
  void endpointsRequireAuth_whenNoToken() throws Exception {
    UserModel user = seedUser("auth.required@example.com", "+15550010015", UserRole.CUSTOMER, UserStatus.ACTIVE, false);

    mockMvc.perform(get("/api/v2/users/{id}", user.getId()))
        .andExpect(status().isUnauthorized());

    mockMvc.perform(get("/api/v2/users/profile/me"))
        .andExpect(status().isUnauthorized());
  }

  private UserModel seedUser(
      String email,
      String phone,
      UserRole role,
      UserStatus status,
      boolean withProfile) {
    String userId = UUID.randomUUID().toString();
    LocalDateTime now = LocalDateTime.now();

    ProfileModel profile = null;
    if (withProfile) {
      profile = ProfileModel.builder()
          .id(UUID.randomUUID().toString())
          .userId(userId)
          .firstName("John")
          .lastName("Doe")
          .dateOfBirth(LocalDate.of(1995, 1, 15))
          .gender(Gender.OTHER)
          .bio("Initial bio")
          .profilePictureUrl("https://example.com/profile.jpg")
          .createdAt(now)
          .updatedAt(now)
          .build();
    }

    UserModel user = UserModel.builder()
        .id(userId)
        .email(email)
        .phoneNumber(phone)
        .hashedPassword("$2a$10$abcdefghijklmnopqrstuv")
        .status(status)
        .role(role)
        .createdAt(now)
        .updatedAt(now)
        .profile(profile)
        .build();

    return userJpaRepository.save(user);
  }

  private String bearerToken(String userId, String role, String email) {
    byte[] secret = jwtSecret.getBytes(StandardCharsets.UTF_8);
    Date issuedAt = new Date();
    Date expiration = new Date(issuedAt.getTime() + 60 * 60 * 1000);

    String token = Jwts.builder()
        .setId(UUID.randomUUID().toString())
        .setSubject(userId)
        .setIssuer("test-suite")
        .setIssuedAt(issuedAt)
        .setExpiration(expiration)
        .claim("userId", userId)
        .claim("role", role)
        .claim("email", email)
        .claim("type", "access")
        .signWith(Keys.hmacShaKeyFor(secret), SignatureAlgorithm.HS256)
        .compact();

    return "Bearer " + token;
  }
}

