package microservice.inventory_service.inventory.core.inventory.port;

import microservice.inventory_service.inventory.core.inventory.domain.entity.Inventory;
import microservice.inventory_service.inventory.core.inventory.domain.entity.enums.InventoryStatus;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.ProductId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository {
    Inventory save(Inventory inventory);
    void bulkSave(List<Inventory> inventories);

    Optional<Inventory> findById(InventoryId id);

    Optional<Inventory> findByProductId(ProductId productId);

    List<Inventory> findAll();

    List<Inventory> findSpec();

    Page<Inventory> findByStatus(InventoryStatus status, Pageable pageable);

    List<Inventory> findAllLowStock();

    Page<Inventory> findByWarehouseLocation(String location, Pageable pageable);

    void delete(InventoryId id);

    boolean existsByProductId(ProductId productId);
}
