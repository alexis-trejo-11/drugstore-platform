package microservice.store_service.infrastructure.inbound.rest.controller;

import libs_kernel.page.PageResponse;
import libs_kernel.response.ResponseWrapper;
import microservice.store_service.app.application.port.in.query.GetStoreByCodeQuery;
import microservice.store_service.app.application.port.in.query.GetStoreByIDQuery;
import microservice.store_service.app.application.port.in.usecase.StoreQueryUseCases;
import microservice.store_service.app.domain.model.ReconstructParams;
import microservice.store_service.app.domain.model.Store;
import microservice.store_service.app.domain.model.enums.StoreStatus;
import microservice.store_service.app.domain.model.valueobjects.StoreCode;
import microservice.store_service.app.domain.model.valueobjects.StoreID;
import microservice.store_service.app.domain.model.valueobjects.StoreName;
import microservice.store_service.app.infrastructure.inbound.rest.dto.response.StoreResponse;
import libs_kernel.mapper.ResponseMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import libs_kernel.log.audit.AuditLogger;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = microservice.store_service.app.infrastructure.inbound.rest.controller.StoreQueryController.class, properties = "spring.cloud.config.enabled=false")
class StoreQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StoreQueryUseCases storeApplicationFacade;

    @MockBean
    private ResponseMapper<StoreResponse, Store> responseMapper;

    @MockBean
    private AuditLogger auditLogger;

    private Store store;
    private StoreResponse storeResponse;

    @BeforeEach
    void setUp() {
        var id = StoreID.generate();
        store = Store.reconstruct(new ReconstructParams(id, StoreCode.create("ABC123"), StoreName.of("MyStore"), StoreStatus.ACTIVE, null, null, null, null, null));
        storeResponse = StoreResponse.builder().id(id.value()).code("ABC123").name("MyStore").status("ACTIVE").build();
    }

    @Test
    void getStoreById_shouldReturnStore() throws Exception {
        when(storeApplicationFacade.getStoreByID(any(GetStoreByIDQuery.class))).thenReturn(store);
        when(responseMapper.toResponse(store)).thenReturn(storeResponse);

        mockMvc.perform(get("/api/v2/stores/" + store.getId().value()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Store found successfully"))
                .andExpect(jsonPath("$.data.code").value("ABC123"));
    }

    @Test
    void getStoreByCode_shouldReturnStore() throws Exception {
        when(storeApplicationFacade.getStoreByCode(any(GetStoreByCodeQuery.class))).thenReturn(store);
        when(responseMapper.toResponse(store)).thenReturn(storeResponse);

        mockMvc.perform(get("/api/v2/stores/ABC123/code").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Store found successfully"))
                .andExpect(jsonPath("$.data.code").value("ABC123"));
    }

    @Test
    void searchStores_shouldReturnPage() throws Exception {
        var page = new PageImpl<>(List.of(store));
        when(storeApplicationFacade.searchStores(any())).thenReturn(page);

        var pageResp = PageResponse.<StoreResponse>builder().content(List.of(storeResponse)).page(1).size(10).totalElements(1).build();
        when(responseMapper.toResponsePage(page)).thenReturn(pageResp);

        mockMvc.perform(get("/api/v2/stores").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Stores by specifications found successfully"))
                .andExpect(jsonPath("$.data.content[0].code").value("ABC123"));
    }

    @Test
    void getStoresByStatus_shouldReturnPage() throws Exception {
        var page = new PageImpl<>(List.of(store));
        when(storeApplicationFacade.getStoresByStatus(any())).thenReturn(page);

        var pageResp = PageResponse.<StoreResponse>builder().content(List.of(storeResponse)).page(1).size(10).totalElements(1).build();
        when(responseMapper.toResponsePage(page)).thenReturn(pageResp);

        mockMvc.perform(get("/api/v2/stores/status/ACTIVE").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Stores by status found successfully"))
                .andExpect(jsonPath("$.data.content[0].code").value("ABC123"));
    }

}
