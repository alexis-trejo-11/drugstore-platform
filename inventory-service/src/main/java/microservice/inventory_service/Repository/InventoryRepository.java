package microservice.inventory_service.Repository;


import microservice.inventory_service.Model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Inventory> findByCreatedAtAfter(LocalDateTime date);

    List<Inventory> findByProductId(Long productId);

}
