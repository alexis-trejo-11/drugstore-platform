package io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.port;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.Inventory;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.enums.InventoryStatus;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.ProductId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.exception.InventoryNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface InventoryRepository {
    Inventory save(Inventory inventory);
    void bulkSave(List<Inventory> inventories);

    Optional<Inventory> findById(InventoryId id);
    List<Inventory> findByIdIn(List<InventoryId> ids);

    Optional<Inventory> findByProductId(ProductId productId);

    List<Inventory> findAll();

    List<Inventory> findSpec();

    List<Inventory> findByProductIdInOrThrow(Set<ProductId> inventoryIdSet) throws InventoryNotFoundException;


    Page<Inventory> findByStatus(InventoryStatus status, Pageable pageable);

    List<Inventory> findAllLowStock();

    Page<Inventory> findByWarehouseLocation(String location, Pageable pageable);

    void delete(InventoryId id);

    boolean existsByProductId(ProductId productId);
}
