package microservice.ecommerce_sale_service.Repository;

import microservice.ecommerce_sale_service.Model.DigitalSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DigitalSaleRepository extends JpaRepository<DigitalSale, Long> {

        @Query("SELECT s FROM DigitalSale s WHERE s.saleDate >= :startOfDay AND s.saleDate < :endOfDay")
        List<DigitalSale> findDigitalSalesByDate(
                @Param("startOfDay") LocalDateTime startOfDay,
                @Param("endOfDay") LocalDateTime endOfDay
        );
}