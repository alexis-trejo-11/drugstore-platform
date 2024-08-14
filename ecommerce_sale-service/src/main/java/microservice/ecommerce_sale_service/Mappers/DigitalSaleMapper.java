package microservice.ecommerce_sale_service.Mappers;

import at.backend.drugstore.microservice.common_classes.DTOs.Payment.PaymentDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Sale.DigitalSaleDTO;
import at.backend.drugstore.microservice.common_classes.Models.Sales.SaleStatus;
import microservice.ecommerce_sale_service.Model.DigitalSale;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DigitalSaleMapper {

    @Mapping(target = "saleItemDTOS", ignore = true)
    DigitalSaleDTO entityToDTO(DigitalSale digitalSale);

    @Mapping(target = "saleDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "paymentId", source = "paymentDTO.id")
    @Mapping(target = "saleItems", ignore = true)
    @Mapping(target = "saleStatus", source = "saleStatus")
    DigitalSale insertDTOToEntity(PaymentDTO paymentDTO, SaleStatus saleStatus);
}
