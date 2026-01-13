package microservice.store_service.application.usecase;

import microservice.store_service.app.application.port.in.query.GetStoreByCodeQuery;
import microservice.store_service.app.application.port.in.query.GetStoreByIDQuery;
import microservice.store_service.app.application.port.in.query.GetStoresByStatusQuery;
import microservice.store_service.app.application.port.in.query.SearchStoresQuery;
import microservice.store_service.app.application.usecase.StoreQueryUseCasesImpl;
import microservice.store_service.app.application.port.out.StoreRepository;
import microservice.store_service.app.domain.model.Store;
import microservice.store_service.app.domain.model.ReconstructParams;
import microservice.store_service.app.domain.model.enums.StoreStatus;
import microservice.store_service.app.domain.model.valueobjects.StoreCode;
import microservice.store_service.app.domain.model.valueobjects.StoreID;
import microservice.store_service.app.domain.model.valueobjects.StoreName;
import microservice.store_service.app.domain.specification.StoreSearchCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class StoreQueryUseCasesImplTest {

    private StoreRepository storeRepository;
    private StoreQueryUseCasesImpl useCases;

    @BeforeEach
    void setUp() {
        storeRepository = Mockito.mock(StoreRepository.class);
        useCases = new StoreQueryUseCasesImpl(storeRepository);
    }

    @Test
    void getStoreByCode_returnsStore() {
        var code = StoreCode.create("ABC123");
        var id = StoreID.generate();
        Store store = Store.reconstruct(new ReconstructParams(id, code, StoreName.of("S"), StoreStatus.ACTIVE, null, null, null, null, null));

        when(storeRepository.findByCode(code)).thenReturn(java.util.Optional.of(store));

        var result = useCases.getStoreByCode(GetStoreByCodeQuery.of("ABC123"));

        assertThat(result).isNotNull();
        assertThat(result.getCode().value()).isEqualTo("ABC123");
    }

    @Test
    void getStoreById_returnsStore() {
        var id = StoreID.generate();
        Store store = Store.reconstruct(new ReconstructParams(id, StoreCode.create("DEF456"), StoreName.of("S"), StoreStatus.ACTIVE, null, null, null, null, null));

        when(storeRepository.findByID(id)).thenReturn(java.util.Optional.of(store));

        var result = useCases.getStoreByID(GetStoreByIDQuery.of(id.value()));

        assertThat(result).isNotNull();
        assertThat(result.getId().value()).isEqualTo(id.value());
    }

    @Test
    void searchStores_returnsPage() {
        Store s1 = Store.reconstruct(new ReconstructParams(StoreID.generate(), StoreCode.create("S00001"), StoreName.of("A"), StoreStatus.ACTIVE, null, null, null, null, null));
        Store s2 = Store.reconstruct(new ReconstructParams(StoreID.generate(), StoreCode.create("S00002"), StoreName.of("B"), StoreStatus.ACTIVE, null, null, null, null, null));

        Page<Store> page = new PageImpl<>(List.of(s1, s2));

        when(storeRepository.search(any(StoreSearchCriteria.class))).thenReturn(page);

        var query = SearchStoresQuery.builder().page(1).size(10).build();
        var result = useCases.searchStores(query);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void getStoresByStatus_returnsPage() {
        Store s1 = Store.reconstruct(new ReconstructParams(StoreID.generate(), StoreCode.create("S00001"), StoreName.of("A"), StoreStatus.INACTIVE, null, null, null, null, null));
        Page<Store> page = new PageImpl<>(List.of(s1));

        when(storeRepository.search(any(StoreSearchCriteria.class))).thenReturn(page);

        var query = new GetStoresByStatusQuery(StoreStatus.INACTIVE, Pageable.ofSize(10).withPage(0), null);
        var result = useCases.getStoresByStatus(query);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

}
