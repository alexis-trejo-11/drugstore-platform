package microservice.store_service.app.application.port.in.usecase;


import microservice.store_service.app.application.port.in.query.GetStoreByCodeQuery;
import microservice.store_service.app.application.port.in.query.GetStoreByIDQuery;
import microservice.store_service.app.application.port.in.query.GetStoresByStatusQuery;
import microservice.store_service.app.application.port.in.query.SearchStoresQuery;
import microservice.store_service.app.domain.model.Store;
import org.springframework.data.domain.Page;

public interface StoreQueryUseCases {
    Store getStoreByCode(GetStoreByCodeQuery query);
    Store getStoreByID(GetStoreByIDQuery query);
    Page<Store> searchStores(SearchStoresQuery query);
    Page<Store> getStoresByStatus(GetStoresByStatusQuery query);
}

