package io.github.alexisTrejo11.drugstore.inventories.inventory.adapter.outbound.persistence.repository.jpa;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.enums.InventoryStatus;
import io.github.alexisTrejo11.drugstore.inventories.inventory.adapter.outbound.persistence.model.InventoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface JpaInventoryRepository extends JpaRepository<InventoryEntity, String> {
    Optional<InventoryEntity> findByProductId(String productId);

    boolean existsByProductId(String productId);

    Page<InventoryEntity> findByStatus(InventoryStatus status, Pageable pageable);

    List<InventoryEntity> findByStatus(InventoryStatus status);

    @Query("SELECT i FROM InventoryEntity i WHERE i.productId IN :productIds")
    List<InventoryEntity> findByProductIdIn(@Param("productIds") Set<String> productIds);

    Page<InventoryEntity> findByWarehouseLocation(String warehouseLocation, Pageable pageable);
}
