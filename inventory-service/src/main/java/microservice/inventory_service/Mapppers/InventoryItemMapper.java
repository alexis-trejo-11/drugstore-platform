package microservice.inventory_service.Mapppers;

import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryItemDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryItemInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryTransactionDTO;
import microservice.inventory_service.Model.InventoryItem;
import microservice.inventory_service.Model.InventoryTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface InventoryItemMapper {

    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    InventoryItem insertDtoToEntity(InventoryItemInsertDTO inventoryItemInsertDTO);

    InventoryItemDTO entityToDTO(InventoryItem inventoryItem);
}
