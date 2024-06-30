package microservice.ecommerce_sale_service.Repository;

import microservice.ecommerce_sale_service.Model.DigitalSaleItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleItemRepository extends JpaRepository<DigitalSaleItem, Long> {
}