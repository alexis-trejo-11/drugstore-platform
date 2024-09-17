package microservice.inventory_service.Repository;


import microservice.inventory_service.Model.InventoryItem;
import microservice.inventory_service.Utils.ProductStockDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    Page<InventoryItem> findByProductId(Long productId, Pageable pageable);
    List<InventoryItem> findByProductId(Long productId);


    @Query("SELECT i FROM InventoryItem i WHERE i.createdAt BETWEEN :startDate AND :endDate")
    List<InventoryItem> findByCreatedAtBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT COUNT(i) FROM InventoryItem i")
    Integer countAllEntries();

    @Query("SELECT i FROM InventoryItem i WHERE i.quantity < (i.optimalStockLevel * 0.2)")
    List<InventoryItem> findByStockBelowTwentyPercent();

    @Query("SELECT new microservice.inventory_service.Utils.ProductStockDTO(i.productId, SUM(i.quantity)) " +
            "FROM InventoryItem i " +
            "GROUP BY i.productId")
    List<ProductStockDTO> findProductStockSummary();
}
