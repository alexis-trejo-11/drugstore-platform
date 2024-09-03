package microservice.product_service.Mappers;

import at.backend.drugstore.microservice.common_classes.DTOs.Supplier.SupplierDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Supplier.SupplierInsertDTO;
import microservice.product_service.Model.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SupplierMapper {

    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "products", ignore = true)
    Supplier insertDtoToSupplier(SupplierInsertDTO supplierInsertDTO);

    SupplierDTO entityToDTO(Supplier supplier);
}