package microservice.inventory_service.Service.DomainService;

import at.backend.drugstore.microservice.common_models.DTOs.Inventory.InventoryInsertDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Product.ProductDTO;
import microservice.inventory_service.Model.Inventory;
import microservice.inventory_service.Model.InventoryTransaction;
import microservice.inventory_service.Repository.InventoryRepository;
import microservice.inventory_service.Service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InventoryDomainService {

    public final InventoryRepository inventoryRepository;

    public InventoryDomainService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public void processInventoryInsert(InventoryInsertDTO inventoryInsertDTO, ProductDTO productDTO, Long employeeId) {
        Inventory inventory = new Inventory(inventoryInsertDTO);
        inventory.setProductId(productDTO.getId());

        InventoryTransaction inventoryTransaction = new InventoryTransaction(inventoryInsertDTO.getInventoryTransactionInsertDTO(), employeeId);
        inventoryTransaction.setInventory(inventory);

        if (inventory.getTransactions() == null) {
            List<InventoryTransaction> inventoryTransactionList = new ArrayList<>();
            inventoryTransactionList.add(inventoryTransaction);
            inventory.setTransactions(inventoryTransactionList);
        } else {
            inventory.getTransactions().add(inventoryTransaction);
        }

        inventoryRepository.saveAndFlush(inventory);
    }

}
