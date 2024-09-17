package microservice.inventory_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryItemDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryItemInsertDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import jakarta.transaction.Transactional;
import microservice.inventory_service.Mapppers.InventoryItemMapper;
import microservice.inventory_service.Model.InventoryItem;
import microservice.inventory_service.Service.DomainService.InventoryDomainService;
import microservice.inventory_service.Utils.InventorySummary;
import microservice.inventory_service.Repository.InventoryItemRepository;
import microservice.inventory_service.Utils.ProductStockStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
public class InventoryItemServiceImpl implements InventoryItemService {

    private final InventoryItemMapper inventoryItemMapper;
    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryDomainService inventoryDomainService;

    @Autowired
    public InventoryItemServiceImpl(InventoryItemMapper inventoryItemMapper,
                                    InventoryItemRepository inventoryItemRepository,
                                    InventoryDomainService inventoryDomainService) {
        this.inventoryItemMapper = inventoryItemMapper;
        this.inventoryItemRepository = inventoryItemRepository;
        this.inventoryDomainService = inventoryDomainService;
    }

    @Override
    @Cacheable(value = "inventoryItems", key = "#inventoryItemId")
    public InventoryItemDTO getInventoryItemById(Long inventoryItemId) {
        InventoryItem inventoryItem = inventoryItemRepository.findById(inventoryItemId).orElse(null);
        return inventoryItemMapper.entityToDTO(inventoryItem);
    }

    @Override
    @Cacheable(value = "inventoryItemsByProduct", key = "#inventoryItemId")
    public Page<InventoryItemDTO> getInventoryItemByProductId(Long inventoryItemId, Pageable pageable) {
        Page<InventoryItem> inventoryItemPage = inventoryItemRepository.findByProductId(inventoryItemId, pageable);
        return inventoryItemPage.map(inventoryItemMapper::entityToDTO);
    }

    @Override
    public ProductStockStatus getProductTotalStock(Long productId) {
        List<InventoryItem> inventoryItems = inventoryItemRepository.findByProductId(productId);
        return inventoryDomainService.generateProductStockStatus(inventoryItems, productId);
    }

    @Override
    @Cacheable(value = "inventorySummary", key = "'currentMonthSummary'")
    public InventorySummary getInventorySummary() {
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime firstDay = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime lastDay = currentMonth.atEndOfMonth().atTime(LocalDateTime.MAX.toLocalTime());

        return inventoryDomainService.generateInventorySummary(firstDay, lastDay);
    }

    @Override
    @Transactional
    public Result<Void> createInventoryItem(InventoryItemInsertDTO inventoryItemInsertDTO) {
        InventoryItem inventoryItem = inventoryItemMapper.insertDtoToEntity(inventoryItemInsertDTO);
        inventoryItemRepository.saveAndFlush(inventoryItem);

        return Result.success();
    }
    
    @Override
    public void updateInventoryItem(Long inventoryItemId, InventoryItemInsertDTO itemInsertDTO) {
        InventoryItem inventoryItemUpdated = inventoryItemMapper.insertDtoToEntity(itemInsertDTO);
        inventoryItemUpdated.setId(inventoryItemId);

        inventoryItemRepository.saveAndFlush(inventoryItemUpdated);
    }

    @Override
    public void deleteInventoryItem(Long inventoryItemId) {
        inventoryItemRepository.deleteById(inventoryItemId);
    }
}
