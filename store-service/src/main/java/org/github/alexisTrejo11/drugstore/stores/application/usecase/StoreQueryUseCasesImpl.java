package org.github.alexisTrejo11.drugstore.stores.application.usecase;

import org.github.alexisTrejo11.drugstore.stores.application.port.in.query.GetStoreByCodeQuery;
import org.github.alexisTrejo11.drugstore.stores.application.port.in.query.GetStoreByIDQuery;
import org.github.alexisTrejo11.drugstore.stores.application.port.in.query.GetStoresByStatusQuery;
import org.github.alexisTrejo11.drugstore.stores.application.port.in.query.SearchStoresQuery;
import org.github.alexisTrejo11.drugstore.stores.application.port.in.usecase.StoreQueryUseCases;
import org.github.alexisTrejo11.drugstore.stores.domain.model.Store;
import org.github.alexisTrejo11.drugstore.stores.application.port.out.StoreRepository;
import org.github.alexisTrejo11.drugstore.stores.domain.specification.StoreSearchCriteria;
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

