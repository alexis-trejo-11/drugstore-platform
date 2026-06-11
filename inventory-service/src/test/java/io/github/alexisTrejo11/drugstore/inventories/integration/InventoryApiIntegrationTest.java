package io.github.alexisTrejo11.drugstore.inventories.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.alexisTrejo11.drugstore.inventories.integration.support.IntegrationTestJwtSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class InventoryApiIntegrationTest {

    private static final String AUTH_ADMIN = IntegrationTestJwtSupport.bearerAdmin();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/v2/inventories/{id} without Bearer token returns 401")
    void inventories_withoutToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v2/inventories/{id}", "any-id"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.errorCode").value("UNAUTHORIZED"));
    }

    @Test
    @DisplayName("CUSTOMER JWT cannot access inventory API (403)")
    void inventories_customerRole_forbidden() throws Exception {
        mockMvc.perform(get("/api/v2/inventories/{id}", "any-id")
                        .header("Authorization", IntegrationTestJwtSupport.bearerCustomer()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error.errorCode").value("FORBIDDEN"));
    }

    @Test
    @DisplayName("MANAGER JWT validates through JWT filter and can access inventory routes")
    void inventories_managerJwt_ok() throws Exception {
        mockMvc.perform(get("/api/v2/inventories/{id}", "nonexistent-id")
                        .header("Authorization", IntegrationTestJwtSupport.bearerManager()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Create inventory then GET by id and by product id")
    void create_then_getById_and_getByProduct() throws Exception {
        String productId = "integration-product-" + System.nanoTime();

        String body = """
                {
                  "productId": "%s",
                  "reorderLevel": 5,
                  "reorderQuantity": 20,
                  "maximumStockLevel": 100,
                  "warehouseLocation": "TEST-WH-1"
                }
                """.formatted(productId);

        MvcResult created = mockMvc.perform(post("/api/v2/inventories")
                        .header("Authorization", AUTH_ADMIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Inventory created successfully"))
                .andExpect(jsonPath("$.data.value").exists())
                .andReturn();

        JsonNode root = objectMapper.readTree(created.getResponse().getContentAsString());
        String inventoryId = root.path("data").path("value").asText();
        assertThat(inventoryId).isNotBlank();

        mockMvc.perform(get("/api/v2/inventories/{id}", inventoryId)
                        .header("Authorization", AUTH_ADMIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productId").value(productId))
                .andExpect(jsonPath("$.data.availableQuantity").value(0));

        mockMvc.perform(get("/api/v2/inventories/product/{productId}", productId)
                        .header("Authorization", AUTH_ADMIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(inventoryId))
                .andExpect(jsonPath("$.data.productId").value(productId));
    }

    @Test
    @DisplayName("PATCH /api/v2/inventories/{id} updates settings")
    void patch_updatesSettings() throws Exception {
        String productId = "integration-patch-" + System.nanoTime();
        String createJson = """
                {
                  "productId": "%s",
                  "reorderLevel": 2,
                  "reorderQuantity": 10,
                  "maximumStockLevel": 50,
                  "warehouseLocation": "PATCH-BEFORE"
                }
                """.formatted(productId);

        MvcResult created = mockMvc.perform(post("/api/v2/inventories")
                        .header("Authorization", AUTH_ADMIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andReturn();

        String inventoryId = objectMapper.readTree(created.getResponse().getContentAsString())
                .path("data")
                .path("value")
                .asText();

        String patchJson = """
                {
                  "reorderLevel": 7,
                  "warehouseLocation": "PATCH-AFTER"
                }
                """;

        mockMvc.perform(patch("/api/v2/inventories/{id}", inventoryId)
                        .header("Authorization", AUTH_ADMIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v2/inventories/{id}", inventoryId)
                        .header("Authorization", AUTH_ADMIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.reorderLevel").value(7))
                .andExpect(jsonPath("$.data.warehouseLocation").value("PATCH-AFTER"))
                .andExpect(jsonPath("$.data.reorderQuantity").value(10));
    }

    @Test
    @DisplayName("GET /api/v2/inventories/low-stock returns paged wrapper (may be empty)")
    void lowStock_paged() throws Exception {
        mockMvc.perform(get("/api/v2/inventories/low-stock")
                        .param("page", "1")
                        .param("size", "20")
                        .header("Authorization", AUTH_ADMIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.pagination_metadata").exists());
    }

    @Test
    @DisplayName("Invalid access token returns 401 from JWT filter")
    void invalidJwt_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v2/inventories/{id}", "any")
                        .header("Authorization", "Bearer not-a-valid-jwt"))
                .andExpect(status().isUnauthorized());
    }
}
