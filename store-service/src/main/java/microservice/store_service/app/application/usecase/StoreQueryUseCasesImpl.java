package microservice.store_service.app.application.usecase;

import microservice.store_service.app.application.port.in.query.GetStoreByCodeQuery;
import microservice.store_service.app.application.port.in.query.GetStoreByIDQuery;
import microservice.store_service.app.application.port.in.query.GetStoresByStatusQuery;
import microservice.store_service.app.application.port.in.query.SearchStoresQuery;
import microservice.store_service.app.application.port.in.usecase.StoreQueryUseCases;
import microservice.store_service.app.domain.model.Store;
import microservice.store_service.app.application.port.out.StoreRepository;
import microservice.store_service.app.domain.specification.StoreSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class StoreQueryUseCasesImpl implements StoreQueryUseCases {

    private final StoreRepository storeRepository;

    @Autowired
    public StoreQueryUseCasesImpl(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @Override
    @Cacheable(value = "stores", key = "#query.code.value()")
    public Store getStoreByCode(GetStoreByCodeQuery query) {
        return storeRepository.findByCode(query.code()).orElseThrow();
    }

    @Override
    @Cacheable(value = "stores", key = "#query.id.value()")
    public Store getStoreByID(GetStoreByIDQuery query) {
        return storeRepository.findByID(query.id()).orElseThrow();
    }

    @Override
    @Cacheable(value = "store_searches", key = "#query.toString()")
    public Page<Store> searchStores(SearchStoresQuery query) {
        var criteria = query.toCriteria();
        return storeRepository.search(criteria);
    }

    @Override
    @Cacheable(value = "store_status", key = "#query.status.name() + '_' + #query.page + '_' + #query.size")
    public Page<Store> getStoresByStatus(GetStoresByStatusQuery query) {
        StoreSearchCriteria searchCriteria = StoreSearchCriteria.findByStatus(
                query.status(),
                query.pagination().getPageSize(),
                query.pagination().getPageNumber(),
                query.sortCriteria()
        );

        return storeRepository.search(searchCriteria);
    }
}

