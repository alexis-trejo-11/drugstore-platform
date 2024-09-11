package microservice.inventory_service.Mapppers;

import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryItemDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryTransactionDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryTransactionInsertDTO;
import microservice.inventory_service.Model.InventoryTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryTransactionMapper {

    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "inventoryItem", ignore = true)
    InventoryTransaction insertDtoToEntity(InventoryTransactionInsertDTO transactionInsertDTO);

    @Mapping(target = "inventoryItemId", source = "inventoryTransaction.inventoryItem.id")
    @Mapping(target = "transactionType", source = "transactionType")
    InventoryTransactionDTO entityToDTO(InventoryTransaction inventoryTransaction);
}
