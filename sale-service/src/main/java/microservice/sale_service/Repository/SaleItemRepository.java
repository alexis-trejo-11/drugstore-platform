package microservice.sale_service.Repository;


import microservice.sale_service.Model.PhysicalSaleItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleItemRepository extends JpaRepository<PhysicalSaleItem, Long> {
}