package microservice.product_service.Mappers;

import at.backend.drugstore.microservice.common_models.DTO.Supplier.SupplierInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Supplier.SupplierReturnDTO;
import microservice.product_service.Model.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SupplierMapper {

    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "products", ignore = true)
    Supplier insertDtoToSupplier(SupplierInsertDTO supplierInsertDTO);

    SupplierReturnDTO supplierToReturnDTO(Supplier supplier);
}