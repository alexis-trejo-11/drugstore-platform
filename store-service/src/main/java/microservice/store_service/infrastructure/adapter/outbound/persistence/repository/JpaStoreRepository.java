package microservice.store_service.infrastructure.adapter.outbound.persistence.repository;

import microservice.store_service.infrastructure.adapter.outbound.persistence.entity.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface JpaStoreRepository extends JpaRepository<StoreEntity, String>, JpaSpecificationExecutor<StoreEntity> {
    Optional<StoreEntity> findByCode(String code);
    boolean existsByCode(String code);
    boolean existsByID(String id);
}
