package microservice.inventory_service.Repository;


import microservice.inventory_service.Model.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {


}
