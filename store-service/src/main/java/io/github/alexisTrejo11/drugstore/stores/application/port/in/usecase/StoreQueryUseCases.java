package io.github.alexisTrejo11.drugstore.stores.application.port.in.usecase;


import io.github.alexisTrejo11.drugstore.stores.application.port.in.query.GetStoreByCodeQuery;
import io.github.alexisTrejo11.drugstore.stores.application.port.in.query.GetStoreByIDQuery;
import io.github.alexisTrejo11.drugstore.stores.application.port.in.query.GetStoresByStatusQuery;
import io.github.alexisTrejo11.drugstore.stores.application.port.in.query.SearchStoresQuery;
import io.github.alexisTrejo11.drugstore.stores.domain.model.Store;
import org.springframework.data.domain.Page;

public interface StoreQueryUseCases {
    Store getStoreByCode(GetStoreByCodeQuery query);
    Store getStoreByID(GetStoreByIDQuery query);
    Page<Store> searchStores(SearchStoresQuery query);
    Page<Store> getStoresByStatus(GetStoresByStatusQuery query);
}

