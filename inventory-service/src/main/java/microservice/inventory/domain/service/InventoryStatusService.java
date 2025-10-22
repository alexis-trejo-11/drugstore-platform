package microservice.inventory.domain.service;

import microservice.inventory.domain.entity.enums.InventoryStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class InventoryStatusService {

    public void updateInventoryStatus(Inventory inventory) {
        if (inventory.getAvailableQuantity() <= 0) {
            inventory.setStatus(InventoryStatus.OUT_OF_STOCK);
        } else if (inventory.getAvailableQuantity() <= inventory.getReorderLevel()) {
            inventory.setStatus(InventoryStatus.LOW_STOCK);
        } else {
            inventory.setStatus(InventoryStatus.ACTIVE);
        }

        inventory.setUpdatedAt(LocalDateTime.now());
    }

    public boolean needsReorder(Inventory inventory) {
        return inventory.getAvailableQuantity() <= inventory.getReorderLevel();
    }

    public Integer calculateReorderQuantity(Inventory inventory) {
        return inventory.getMaximumStockLevel() - inventory.getTotalQuantity();
    }
}
