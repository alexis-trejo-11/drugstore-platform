package io.github.alexisTrejo11.drugstore.stores.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.cloud.config.enabled=false")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SpringDocIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void v3ApiDocs_returnsOpenApiJson() throws Exception {
    String body = mockMvc
        .perform(get("/v3/api-docs"))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
    assertThat(body).contains("\"openapi\"");
    assertThat(body).contains("/api/");
  }

  @Test
  void swaggerConfig_returnsJsonWithUrl() throws Exception {
    String body = mockMvc
        .perform(get("/v3/api-docs/swagger-config"))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
    assertThat(body).contains("\"url\"");
  }

  @Test
  void swaggerUiHtml_redirectsToSwaggerUi() throws Exception {
    mockMvc
        .perform(get("/swagger-ui.html"))
        .andExpect(status().isFound())
        .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("swagger-ui")));
  }
}
