package microservice.ecommerce_sale_service.Mappers;

import at.backend.drugstore.microservice.common_models.DTO.Sale.DigitalSaleDTO;
import at.backend.drugstore.microservice.common_models.DTO.Sale.DigitalSaleItemInsertDTO;
import at.backend.drugstore.microservice.common_models.Models.Sales.SaleStatus;
import microservice.ecommerce_sale_service.Model.DigitalSale;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface DigitalSaleMapper {

    @Mapping(target = "saleItemDTOS", ignore = true)
    DigitalSaleDTO entityToDTO(DigitalSale digitalSale);

    @Mapping(target = "saleStatus", source = ".", qualifiedByName = "mapStatus")
    DigitalSale insertDTOToEntity(DigitalSaleItemInsertDTO digitalSaleItemInsertDTO);

    @Named("mapStatus")
    default SaleStatus mapStatus(DigitalSaleItemInsertDTO dto) {
        return SaleStatus.PAID;
    }
}
