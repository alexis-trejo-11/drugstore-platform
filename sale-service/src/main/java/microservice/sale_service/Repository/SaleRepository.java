package microservice.sale_service.Repository;

import at.backend.drugstore.microservice.common_models.Models.Sales.SaleStatus;
import microservice.sale_service.Model.PhysicalSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<PhysicalSale, Long> {

        // Find sales based on sale date and sale status
        @Query("SELECT s FROM PhysicalSale s WHERE s.saleDate >= :startOfDay AND s.saleDate < :endOfDay AND s.saleStatus = :status")
        List<PhysicalSale> findPhysicalSalesByDateAndStatus(
                @Param("startOfDay") LocalDateTime startOfDay,
                @Param("endOfDay") LocalDateTime endOfDay,
                @Param("status") SaleStatus status
        );
}