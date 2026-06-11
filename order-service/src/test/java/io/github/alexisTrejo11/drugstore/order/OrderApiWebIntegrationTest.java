package io.github.alexisTrejo11.drugstore.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.alexisTrejo11.drugstore.order.external.address.infrastructure.repository.AddressRepository;
import io.github.alexisTrejo11.drugstore.order.external.address.model.BuildingType;
import io.github.alexisTrejo11.drugstore.order.external.address.model.DeliveryAddress;
import io.github.alexisTrejo11.drugstore.order.external.user.model.User;
import io.github.alexisTrejo11.drugstore.order.external.user.repository.UserRepository;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.AddressID;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.UserID;
import io.github.alexisTrejo11.drugstore.order.support.JwtTokenFactory;
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
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderApiWebIntegrationTest {

	private static final String TEST_USER = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee";
	private static final String TEST_ADDRESS = "bbbbbbbb-cccc-dddd-eeee-ffffffffffff";

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private AddressRepository addressRepository;

	private String managerToken;
	private String customerToken;
	private String auditorToken;

	@BeforeEach
	void setUpMocksAndTokens() {
		managerToken = JwtTokenFactory.accessToken(jwtSecret, TEST_USER, "MANAGER");
		customerToken = JwtTokenFactory.accessToken(jwtSecret, TEST_USER, "CUSTOMER");
		auditorToken = JwtTokenFactory.accessToken(jwtSecret, TEST_USER, "AUDITOR");

		User stubUser = User.builder()
				.id(UserID.of(TEST_USER))
				.name("Integration User")
				.email("integration-test@example.com")
				.phoneNumber("+525555555555")
				.status("ACTIVE")
				.role("CUSTOMER")
				.build();

		when(userRepository.findById(any(UserID.class))).thenAnswer(invocation -> {
			UserID id = invocation.getArgument(0);
			if (TEST_USER.equals(id.value())) {
				return Optional.of(stubUser);
			}
			return Optional.empty();
		});

		DeliveryAddress stubAddress = DeliveryAddress.builder()
				.id(AddressID.of(TEST_ADDRESS))
				.userID(UserID.of(TEST_USER))
				.country("MX")
				.state("CDMX")
				.city("Ciudad de México")
				.neighborhood("Centro")
				.zipCode("06000")
				.street("Av. Principal")
				.buildingType(BuildingType.HOUSE)
				.innerNumber("")
				.outerNumber("10")
				.additionalInfo("")
				.isDefault(true)
				.build();

		when(addressRepository.getAddressByIDAndUserID(any(AddressID.class), any(UserID.class)))
				.thenAnswer(invocation -> {
					AddressID aid = invocation.getArgument(0);
					UserID uid = invocation.getArgument(1);
					if (TEST_ADDRESS.equals(aid.value()) && TEST_USER.equals(uid.value())) {
						return stubAddress;
					}
					return null;
				});
	}

	@Test
	void searchOrders_withoutToken_returnsUnauthorized() throws Exception {
		mockMvc.perform(get("/api/v2/sale-orders/search"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void searchOrders_withToken_returnsOk() throws Exception {
		mockMvc.perform(get("/api/v2/sale-orders/search")
						.header("Authorization", bearer(managerToken)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content").exists());
	}

	@Test
	void getOrderById_notFound_returnsNotFound() throws Exception {
		mockMvc.perform(get("/api/v2/sale-orders/" + UUID.randomUUID())
						.header("Authorization", bearer(managerToken)))
				.andExpect(status().isNotFound());
	}

	@Test
	void customerOrders_forbiddenWhenRoleNotAllowed() throws Exception {
		mockMvc.perform(get("/api/v2/customers/orders/" + TEST_USER)
						.header("Authorization", bearer(auditorToken)))
				.andExpect(status().isForbidden());
	}

	@Test
	void createDeliveryOrder_invalidBody_returnsBadRequest() throws Exception {
		String body = """
				{
				  "userID": "%s",
				  "deliveryMethod": "STANDARD_DELIVERY",
				  "notes": "test",
				  "currency": "MXN",
				  "items": []
				}
				""".formatted(TEST_USER);

		mockMvc.perform(post("/api/v2/sale-orders")
						.header("Authorization", bearer(managerToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isUnprocessableEntity());
	}

	@Test
	void deliveryOrder_happyPath_createThroughShipComplete_andQueries() throws Exception {
		String createJson = """
				{
				  "userID": "%s",
				  "deliveryMethod": "STANDARD_DELIVERY",
				  "notes": "integration",
				  "currency": "MXN",
				  "items": [
				    {
				      "productID": "prod-001",
				      "productName": "Test Product",
				      "subtotal": 10.00,
				      "quantity": 1,
				      "isPrescriptionRequired": false
				    }
				  ],
				  "deliveryInfo": { "addressID": "%s" }
				}
				""".formatted(TEST_USER, TEST_ADDRESS);

		MvcResult created = mockMvc.perform(post("/api/v2/sale-orders")
						.header("Authorization", bearer(managerToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(createJson))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.data.orderId.value").exists())
				.andReturn();

		String orderId = objectMapper.readTree(created.getResponse().getContentAsString())
				.path("data").path("orderId").path("value").asText();

		mockMvc.perform(get("/api/v2/sale-orders/" + orderId)
						.header("Authorization", bearer(managerToken)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.orderId").value(orderId));

		mockMvc.perform(get("/api/v2/sale-orders/" + orderId + "/detail")
						.header("Authorization", bearer(managerToken)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.userResponse.email").value("integration-test@example.com"));

		String confirmJson = """
				{
				  "estimatedDeliveryDate": "%s",
				  "paymentID": "pay-integration-001"
				}
				""".formatted(LocalDateTime.now().plusDays(2).withNano(0));

		mockMvc.perform(patch("/api/v2/sale-orders/" + orderId + "/confirm")
						.header("Authorization", bearer(managerToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(confirmJson))
				.andExpect(status().isOk());

		mockMvc.perform(patch("/api/v2/sale-orders/" + orderId + "/start-preparing")
						.header("Authorization", bearer(managerToken)))
				.andExpect(status().isOk());

		mockMvc.perform(patch("/api/v2/sale-orders/" + orderId + "/ship/track_number/TRK-INT-001")
						.header("Authorization", bearer(managerToken)))
				.andExpect(status().isOk());

		mockMvc.perform(patch("/api/v2/sale-orders/" + orderId + "/complete")
						.header("Authorization", bearer(managerToken)))
				.andExpect(status().isOk());

		mockMvc.perform(get("/api/v2/customers/orders/" + TEST_USER)
						.header("Authorization", bearer(customerToken)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content").exists());

		mockMvc.perform(get("/api/v2/customers/orders/" + orderId + "/" + TEST_USER)
						.header("Authorization", bearer(customerToken)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.userResponse.email").exists());
	}

	@Test
	void deliveryOrder_returnAfterShip() throws Exception {
		String createJson = """
				{
				  "userID": "%s",
				  "deliveryMethod": "STANDARD_DELIVERY",
				  "notes": "return path",
				  "currency": "MXN",
				  "items": [
				    {
				      "productID": "prod-ret-1",
				      "productName": "Return test",
				      "subtotal": 8.00,
				      "quantity": 1,
				      "isPrescriptionRequired": false
				    }
				  ],
				  "deliveryInfo": { "addressID": "%s" }
				}
				""".formatted(TEST_USER, TEST_ADDRESS);

		MvcResult created = mockMvc.perform(post("/api/v2/sale-orders")
						.header("Authorization", bearer(managerToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(createJson))
				.andExpect(status().isCreated())
				.andReturn();
		String orderId = objectMapper.readTree(created.getResponse().getContentAsString())
				.path("data").path("orderId").path("value").asText();

		String confirmJson = """
				{
				  "estimatedDeliveryDate": "%s",
				  "paymentID": "pay-return-1"
				}
				""".formatted(LocalDateTime.now().plusDays(3).withNano(0));

		mockMvc.perform(patch("/api/v2/sale-orders/" + orderId + "/confirm")
						.header("Authorization", bearer(managerToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(confirmJson))
				.andExpect(status().isOk());
		mockMvc.perform(patch("/api/v2/sale-orders/" + orderId + "/start-preparing")
						.header("Authorization", bearer(managerToken)))
				.andExpect(status().isOk());
		mockMvc.perform(patch("/api/v2/sale-orders/" + orderId + "/ship/track_number/TRK-RET-1")
						.header("Authorization", bearer(managerToken)))
				.andExpect(status().isOk());

		mockMvc.perform(patch("/api/v2/sale-orders/" + orderId + "/return")
						.param("reason", "CustomerNotAvailable")
						.header("Authorization", bearer(managerToken)))
				.andExpect(status().isOk());
	}

	@Test
	void confirmOrder_pastEstimatedDate_returnsBadRequest() throws Exception {
		String createJson = """
				{
				  "userID": "%s",
				  "deliveryMethod": "EXPRESS_DELIVERY",
				  "notes": "bad confirm",
				  "currency": "MXN",
				  "items": [
				    {
				      "productID": "prod-002",
				      "productName": "Other",
				      "subtotal": 5.00,
				      "quantity": 1,
				      "isPrescriptionRequired": false
				    }
				  ],
				  "deliveryInfo": { "addressID": "%s" }
				}
				""".formatted(TEST_USER, TEST_ADDRESS);

		MvcResult created = mockMvc.perform(post("/api/v2/sale-orders")
						.header("Authorization", bearer(managerToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(createJson))
				.andExpect(status().isCreated())
				.andReturn();
		String orderId = objectMapper.readTree(created.getResponse().getContentAsString())
				.path("data").path("orderId").path("value").asText();

		String confirmJson = """
				{
				  "estimatedDeliveryDate": "%s",
				  "paymentID": "pay-bad"
				}
				""".formatted(LocalDateTime.now().minusDays(1).withNano(0));

		mockMvc.perform(patch("/api/v2/sale-orders/" + orderId + "/confirm")
						.header("Authorization", bearer(managerToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(confirmJson))
				.andExpect(status().isUnprocessableEntity());
	}

	@Test
	void pickupOrder_create_getDetail_cancel() throws Exception {
		String createJson = """
				{
				  "userID": "%s",
				  "deliveryMethod": "STORE_PICKUP",
				  "notes": "pickup flow",
				  "currency": "MXN",
				  "items": [
				    {
				      "productID": "prod-pu-1",
				      "productName": "Pickup item",
				      "subtotal": 20.00,
				      "quantity": 1,
				      "isPrescriptionRequired": false
				    }
				  ],
				  "pickupInfo": {
				    "storeID": "store-11111111-1111-1111-1111-111111111111",
				    "storeName": "Test Store",
				    "storeAddress": "123 Test St"
				  }
				}
				""".formatted(TEST_USER);

		MvcResult created = mockMvc.perform(post("/api/v2/sale-orders")
						.header("Authorization", bearer(managerToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(createJson))
				.andExpect(status().isCreated())
				.andReturn();
		String orderId = objectMapper.readTree(created.getResponse().getContentAsString())
				.path("data").path("orderId").path("value").asText();

		mockMvc.perform(get("/api/v2/sale-orders/" + orderId)
						.header("Authorization", bearer(managerToken)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.status").value("PENDING"));

		mockMvc.perform(get("/api/v2/sale-orders/" + orderId + "/detail")
						.header("Authorization", bearer(managerToken)))
				.andExpect(status().isOk());

		mockMvc.perform(put("/api/v2/sale-orders/" + orderId + "/cancel")
						.param("reason", "customer changed plans")
						.header("Authorization", bearer(managerToken)))
				.andExpect(status().isOk());
	}

	@Test
	void cancelOrder_admin_happyPath() throws Exception {
		String createJson = """
				{
				  "userID": "%s",
				  "deliveryMethod": "STANDARD_DELIVERY",
				  "notes": "to cancel",
				  "currency": "MXN",
				  "items": [
				    {
				      "productID": "prod-can-1",
				      "productName": "Cancel me",
				      "subtotal": 1.00,
				      "quantity": 1,
				      "isPrescriptionRequired": false
				    }
				  ],
				  "deliveryInfo": { "addressID": "%s" }
				}
				""".formatted(TEST_USER, TEST_ADDRESS);

		MvcResult created = mockMvc.perform(post("/api/v2/sale-orders")
						.header("Authorization", bearer(managerToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(createJson))
				.andExpect(status().isCreated())
				.andReturn();
		String orderId = objectMapper.readTree(created.getResponse().getContentAsString())
				.path("data").path("orderId").path("value").asText();

		mockMvc.perform(put("/api/v2/sale-orders/" + orderId + "/cancel")
						.param("reason", "integration test cancel")
						.header("Authorization", bearer(managerToken)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data").exists());
	}

	@Test
	void deleteOrder_softDelete() throws Exception {
		String createJson = """
				{
				  "userID": "%s",
				  "deliveryMethod": "STANDARD_DELIVERY",
				  "notes": "to delete",
				  "currency": "MXN",
				  "items": [
				    {
				      "productID": "prod-del-1",
				      "productName": "Delete me",
				      "subtotal": 2.00,
				      "quantity": 1,
				      "isPrescriptionRequired": false
				    }
				  ],
				  "deliveryInfo": { "addressID": "%s" }
				}
				""".formatted(TEST_USER, TEST_ADDRESS);

		MvcResult created = mockMvc.perform(post("/api/v2/sale-orders")
						.header("Authorization", bearer(managerToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(createJson))
				.andExpect(status().isCreated())
				.andReturn();
		String orderId = objectMapper.readTree(created.getResponse().getContentAsString())
				.path("data").path("orderId").path("value").asText();

		mockMvc.perform(delete("/api/v2/sale-orders/" + orderId)
						.param("isHard", "false")
						.header("Authorization", bearer(managerToken)))
				.andExpect(status().isOk());
	}

	private static String bearer(String token) {
		return "Bearer " + token;
	}
}
