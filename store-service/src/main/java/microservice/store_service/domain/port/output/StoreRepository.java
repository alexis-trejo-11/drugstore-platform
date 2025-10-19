package microservice.store_service.domain.port.output;

import microservice.store_service.domain.model.Store;
import microservice.store_service.domain.model.valueobjects.StoreCode;
import microservice.store_service.domain.model.valueobjects.StoreID;
import microservice.store_service.domain.specification.StoreSearchCriteria;

import java.util.List;
import java.util.Optional;

public interface StoreRepository {
    Optional<Store> findByID(StoreID id);
    Optional<Store> findByCode(StoreCode code);
    List<Store> search(StoreSearchCriteria criteria);
    long count(StoreSearchCriteria criteria);

    Store save(Store store);
    void deleteByID(StoreID id);
    boolean existsByCode(StoreCode code);
    boolean existsByID(StoreID id);
}
