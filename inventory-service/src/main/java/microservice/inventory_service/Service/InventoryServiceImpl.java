package microservice.inventory_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryItemDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryItemInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryTransactionDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import jakarta.transaction.Transactional;
import microservice.inventory_service.Mapppers.InventoryItemMapper;
import microservice.inventory_service.Model.InventoryItem;
import microservice.inventory_service.Model.InventorySummary;
import microservice.inventory_service.Model.InventoryTransaction;
import microservice.inventory_service.Repository.InventoryItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class InventoryServiceImpl implements  InventoryService {

    private final InventoryItemMapper inventoryMapper;
    private final InventoryItemRepository inventoryItemRepository;

    @Autowired
    public InventoryServiceImpl(InventoryItemMapper inventoryMapper,
                                InventoryItemRepository inventoryItemRepository) {
        this.inventoryMapper = inventoryMapper;
        this.inventoryItemRepository = inventoryItemRepository;
    }

    @Override
    @Transactional
    public Result<Void> createInventoryItem(InventoryItemInsertDTO inventoryItemInsertDTO) {
        InventoryItem inventoryItem = inventoryMapper.insertDtoToEntity(inventoryItemInsertDTO);
        inventoryItemRepository.saveAndFlush(inventoryItem);

        return Result.success();
    }

    @Override
    public void updateInventoryItem(Long inventoryItemId, int quantity) {

    }

    @Override
    public InventoryItem updateInventoryItem(Long itemId, InventoryItemDTO itemDTO) {
        return null;
    }

    @Override
    public Page<InventoryItem> getInventoryByProduct(Long inventoryId) {
        return null;
    }

    @Override
    public InventorySummary getInventorySummary() {
        return null;
    }

    @Override
    public InventoryTransaction recordTransaction(InventoryTransactionDTO transactionDTO) {
        return null;
    }

    @Override
    public Result<Void> validateDataToCreateInventory(Long supplier) {
        return Result.success();
    }

    @Override
    public boolean validateExistingInventoryItem(Long inventoryItemId) {
        return true;
    }

}
