package org.github.alexisTrejo11.drugstore.stores.infrastructure.outbound.persistence.repository;

import org.github.alexisTrejo11.drugstore.stores.infrastructure.outbound.persistence.entity.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JpaStoreRepository extends JpaRepository<StoreEntity, String>, JpaSpecificationExecutor<StoreEntity> {
    @Query(value = "SELECT * FROM stores WHERE code = :code AND deleted_at IS NULL", nativeQuery = true)
    Optional<StoreEntity> findByCode(@Param("code") String code);

    @Query(value = "SELECT exists(SELECT 1 FROM stores WHERE code = :code AND deleted_at IS NULL)", nativeQuery = true)
    boolean existsByCode(@Param("code") String code);

    @Query(value = "SELECT exists(SELECT 1 FROM stores WHERE id = :id AND deleted_at IS NULL)", nativeQuery = true)
    boolean existsById(@Param("id") String id);

    @Query(value = "SELECT * FROM stores WHERE id = :id AND deleted_at IS NULL", nativeQuery = true)
    Optional<StoreEntity> findById(@Param("id") String id);

    @Query(value = "SELECT * FROM stores WHERE id = :id", nativeQuery = true)
    Optional<StoreEntity> findByIdIncludeDeleted(@Param("id") String id);

    @Query(value = "SELECT * FROM stores WHERE code = :code", nativeQuery = true)
    Optional<StoreEntity> findByCodeIncludeDeleted(@Param("code") String code);
}
