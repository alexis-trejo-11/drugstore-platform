package microservice.inventory_service.Mapppers;

import at.backend.drugstore.microservice.common_models.DTOs.Inventory.InventoryDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Inventory.InventoryTransactionDTO;
import microservice.inventory_service.Model.Inventory;
import microservice.inventory_service.Model.InventoryTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    InventoryMapper INSTANCE = Mappers.getMapper(InventoryMapper.class);

    @Mappings({
            @Mapping(source = "inventory.id", target = "id"),
            @Mapping(source = "inventory.quantity", target = "quantity"),
            @Mapping(source = "inventory.batchNumber", target = "batchNumber"),
            @Mapping(source = "inventory.expirationDate", target = "expirationDate"),
            @Mapping(source = "inventory.location", target = "location"),
            @Mapping(source = "inventory.dateReceived", target = "dateReceived"),
            @Mapping(target = "inventoryTransactionDTOS", ignore = true)
    })
    InventoryDTO inventoryToDTO(Inventory inventory, String productName);

    @Mappings({
            @Mapping(source = "inventoryTransaction.id", target = "id"),
            @Mapping(source = "inventoryTransaction.transactionType", target = "transactionType"),
            @Mapping(source = "inventoryTransaction.employeeId", target = "employeeId"),
            @Mapping(source = "inventoryTransaction.date", target = "date"),
            @Mapping(source = "inventoryTransaction.quantity", target = "quantity")
    })
    InventoryTransactionDTO inventoryTransactionToDTO(InventoryTransaction inventoryTransaction);
}
