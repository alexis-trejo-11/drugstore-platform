package io.github.alexisTrejo11.drugstore.stores.infrastructure.inbound.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.alexisTrejo11.drugstore.stores.application.port.in.query.StoreOperationResult;
import io.github.alexisTrejo11.drugstore.stores.application.port.in.usecase.StoreCommandUseCases;
import io.github.alexisTrejo11.drugstore.stores.application.port.in.query.CreateStoreResult;
import io.github.alexisTrejo11.drugstore.stores.domain.model.enums.StoreStatus;
import io.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.StoreCode;
import io.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.StoreID;
import io.github.alexisTrejo11.drugstore.stores.infrastructure.inbound.rest.dto.request.AddressRequest;
import io.github.alexisTrejo11.drugstore.stores.infrastructure.inbound.rest.dto.request.CreateStoreRequest;
import io.github.alexisTrejo11.drugstore.stores.infrastructure.inbound.rest.dto.request.GeolocationRequest;
import io.github.alexisTrejo11.drugstore.stores.infrastructure.inbound.rest.dto.request.ScheduleInsertRequest;
import io.github.alexisTrejo11.drugstore.stores.infrastructure.inbound.rest.dto.request.StoreContactInfoRequest;
import libs_kernel.security.ApiSecurityResponseWriter;
import libs_kernel.security.jwt.JwtTokenValidator;
import io.github.alexisTrejo11.drugstore.stores.config.security.RestAuthenticationEntryPoint;
import io.github.alexisTrejo11.drugstore.stores.config.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import libs_kernel.log.audit.AuditLogger;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StoreCommandController.class, properties = "spring.cloud.config.enabled=false")
@Import({
        ApiSecurityResponseWriter.class,
        RestAuthenticationEntryPoint.class,
        RestAccessDeniedHandler.class,
        SecurityConfig.class
})
@ActiveProfiles("test")
@WithMockUser(roles = "ADMIN")
class StoreCommandControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    StoreCommandUseCases storeCommandUseCases;

    @MockitoBean
    AuditLogger auditLogger;

    @MockitoBean
    JwtTokenValidator jwtTokenValidator;

    private CreateStoreRequest createReq;

    @BeforeEach
    void setUp() {
        var contact = new StoreContactInfoRequest("123","a@b.com");
        var addr = new AddressRequest("Peru","Lima","Lima","15001","Miraflores","Av. Larco","123");
        var geo = new GeolocationRequest(12.0, -77.0);
        var schedule = ScheduleInsertRequest.createStandard();
        createReq = new CreateStoreRequest("ABC123","My Store", StoreStatus.ACTIVE, contact, addr, schedule, geo);
    }

    @Test
    void createStore_shouldReturnCreated() throws Exception {
        var id = StoreID.generate();
        var res = CreateStoreResult.builder().storeID(id).code(StoreCode.create("ABC123")).build();
        when(storeCommandUseCases.createStore(any())).thenReturn(res);

        mockMvc.perform(post("/api/v2/stores").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Entity created successfully"))
                .andExpect(jsonPath("$.data.value").value(id.value()));
    }

    @Test
    void activateStore_shouldReturnOk() throws Exception {
        var id = StoreID.generate();
        when(storeCommandUseCases.activateStore(any())).thenReturn(StoreOperationResult.activateResult(id));

        mockMvc.perform(patch("/api/v2/stores/" + id.value() + "/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Store with ID StoreID[value=" + id.value() + "] has been activated."));
    }

    @Test
    void deleteStore_shouldReturnOk() throws Exception {
        var id = StoreID.generate();
        when(storeCommandUseCases.deleteStore(any())).thenReturn(StoreOperationResult.deleteResult(id));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/v2/stores/" + id.value()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Store with ID StoreID[value=" + id.value() + "] has been deleted."));
    }

}
