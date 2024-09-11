package microservice.inventory_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryTransactionInsertDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import microservice.inventory_service.Mapppers.InventoryTransactionMapper;
import microservice.inventory_service.Model.InventoryItem;
import microservice.inventory_service.Model.InventoryTransaction;
import microservice.inventory_service.Repository.InventoryItemRepository;
import microservice.inventory_service.Repository.InventoryTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class InventoryTransactionServiceImpl implements InventoryTransactionService {

    private final InventoryTransactionMapper inventoryTransactionMapper;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final InventoryItemRepository inventoryItemRepository;

    @Autowired
    public InventoryTransactionServiceImpl(InventoryTransactionMapper inventoryTransactionMapper,
                                           InventoryTransactionRepository inventoryTransactionRepository,
                                           InventoryItemRepository inventoryItemRepository) {
        this.inventoryTransactionMapper = inventoryTransactionMapper;
        this.inventoryTransactionRepository = inventoryTransactionRepository;
        this.inventoryItemRepository = inventoryItemRepository;
    }

    @Override
    public void createInventoryTransaction(InventoryTransactionInsertDTO transactionInsertDTO) {
        // Create Inventory Transaction
        InventoryTransaction inventoryTransaction = inventoryTransactionMapper.insertDtoToEntity(transactionInsertDTO);

        InventoryItem inventoryItem = inventoryItemRepository.findById(transactionInsertDTO.getInventoryItemId())
                .orElseThrow(() -> new RuntimeException("Inventory Item not found"));

        inventoryTransaction.setInventoryItem(inventoryItem);

        inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Update Stock
        switch (transactionInsertDTO.getTransactionType()) {
            case RECEIVED -> updateStock(inventoryTransaction.getInventoryItem(), inventoryTransaction.getQuantity(), "increase");
            case SOLD, EXPIRED, DAMAGED, RETURNED -> updateStock(inventoryTransaction.getInventoryItem(), inventoryTransaction.getQuantity(), "decrease");
            case ADJUSTED ->  updateStock(inventoryTransaction.getInventoryItem(), inventoryTransaction.getQuantity(), "update");
        }
    }

    @Override
    public Result<Void> validateSupplier(Long supplierId) {
        return Result.success();
    }

    private void updateStock(InventoryItem inventoryItem, int quantityChange, String action) {
        // Get Quantity
        int currentQuantity = inventoryItem.getQuantity();

        // Update Quantity By Action
        switch (action) {
            case "increase":
                inventoryItem.setQuantity(currentQuantity + quantityChange);
                break;
            case "decrease":
                inventoryItem.setQuantity(currentQuantity - quantityChange);
                break;
            case "update" :
                inventoryItem.setQuantity(quantityChange);
        }

        // Save Changes
        inventoryItem.setUpdatedAt(LocalDateTime.now());
        inventoryItemRepository.saveAndFlush(inventoryItem);
    }
}
