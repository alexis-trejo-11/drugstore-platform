package org.github.alexisTrejo11.drugstore.stores.infrastructure.inbound.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.github.alexisTrejo11.drugstore.stores.application.port.in.query.StoreOperationResult;
import org.github.alexisTrejo11.drugstore.stores.application.port.in.usecase.StoreCommandUseCases;
import org.github.alexisTrejo11.drugstore.stores.application.port.in.query.CreateStoreResult;
import org.github.alexisTrejo11.drugstore.stores.domain.model.enums.StoreStatus;
import org.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.StoreCode;
import org.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.StoreID;
import org.github.alexisTrejo11.drugstore.stores.infrastructure.inbound.rest.dto.request.AddressRequest;
import org.github.alexisTrejo11.drugstore.stores.infrastructure.inbound.rest.dto.request.CreateStoreRequest;
import org.github.alexisTrejo11.drugstore.stores.infrastructure.inbound.rest.dto.request.GeolocationRequest;
import org.github.alexisTrejo11.drugstore.stores.infrastructure.inbound.rest.dto.request.ScheduleInsertRequest;
import org.github.alexisTrejo11.drugstore.stores.infrastructure.inbound.rest.dto.request.StoreContactInfoRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import libs_kernel.log.audit.AuditLogger;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StoreCommandController.class, properties = "spring.cloud.config.enabled=false")
class StoreCommandControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    StoreCommandUseCases storeCommandUseCases;

    @MockBean
    AuditLogger auditLogger;

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
