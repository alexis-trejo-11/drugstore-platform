package microservice.inventory_service.Mapppers;

import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryTransactionDTO;
import microservice.inventory_service.Model.Inventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DtoMappers {

    private final InventoryMapper inventoryMapper;

    @Autowired
    public DtoMappers(InventoryMapper inventoryMapper) {
        this.inventoryMapper = inventoryMapper;
    }

    public InventoryDTO mapInventoryToDTO(Inventory inventory, String productName) {
        InventoryDTO inventoryDTO = inventoryMapper.inventoryToDTO(inventory, productName);
        List<InventoryTransactionDTO> inventoryTransactionDTOS = inventory.getTransactions().stream()
                .map(inventoryMapper::inventoryTransactionToDTO)
                .collect(Collectors.toList());
        inventoryDTO.setInventoryTransactionDTOS(inventoryTransactionDTOS);
        return inventoryDTO;
    }
}
