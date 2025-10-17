package microservice.store_service.domain.port;

import microservice.store_service.domain.model.Store;
import microservice.store_service.domain.model.valueobjects.StoreID;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface StoreRepositoryPort {
    Store save(Store store);
    Optional<Store> findByID(StoreID id);
    Optional<Store> findByCode(String code);
    Page<Store> findAll();
    Page<Store> findByState(String state);
    void deleteById(StoreID id);
    boolean existsByCode(String code);
    boolean existsById(StoreID id);
}
