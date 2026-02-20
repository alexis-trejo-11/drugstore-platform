package org.github.alexisTrejo11.drugstore.stores.application.port.in.usecase;


import org.github.alexisTrejo11.drugstore.stores.application.port.in.query.GetStoreByCodeQuery;
import org.github.alexisTrejo11.drugstore.stores.application.port.in.query.GetStoreByIDQuery;
import org.github.alexisTrejo11.drugstore.stores.application.port.in.query.GetStoresByStatusQuery;
import org.github.alexisTrejo11.drugstore.stores.application.port.in.query.SearchStoresQuery;
import org.github.alexisTrejo11.drugstore.stores.domain.model.Store;
import org.springframework.data.domain.Page;

public interface StoreQueryUseCases {
    Store getStoreByCode(GetStoreByCodeQuery query);
    Store getStoreByID(GetStoreByIDQuery query);
    Page<Store> searchStores(SearchStoresQuery query);
    Page<Store> getStoresByStatus(GetStoresByStatusQuery query);
}

