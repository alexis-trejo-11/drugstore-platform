package microservice.ecommerce_sale_service.Mappers;

import at.backend.drugstore.microservice.common_models.DTOs.Order.OrderItemDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Sale.SaleItemDTO;
import microservice.ecommerce_sale_service.Model.DigitalSale;
import microservice.ecommerce_sale_service.Model.DigitalSaleItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface DigitalSaleItemMapper {

    @Mapping(target = "itemTotal", source = ".", qualifiedByName = "calculateSubtotal")
    SaleItemDTO entityToDTO(DigitalSaleItem digitalSaleItem);

    @Mapping(target = "productQuantity", source = "orderItemDTO.productQuantity")
    @Mapping(target = "productUnitPrice", source = "orderItemDTO.productUnitPrice")
    @Mapping(target = "productName", source = "orderItemDTO.productName")
    @Mapping(target = "productId", source = "orderItemDTO.productId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "digitalSale", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    DigitalSaleItem toEntity(OrderItemDTO orderItemDTO);

    @Mapping(target = "productQuantity", source = "orderItemDTO.productQuantity")
    @Mapping(target = "productUnitPrice", source = "orderItemDTO.productUnitPrice")
    @Mapping(target = "productName", source = "orderItemDTO.productName")
    @Mapping(target = "productId", source = "orderItemDTO.productId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "digitalSale", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    void updateEntityFromDTO(OrderItemDTO orderItemDTO, @MappingTarget DigitalSaleItem digitalSaleItem);

    default void updateDigitalSale(DigitalSale digitalSale, @MappingTarget DigitalSaleItem digitalSaleItem) {
        digitalSaleItem.setDigitalSale(digitalSale);
    }

        @Named("calculateSubtotal")
    default BigDecimal calculateSubtotal(DigitalSaleItem digitalSaleItem) {
        return digitalSaleItem.getProductUnitPrice().multiply(BigDecimal.valueOf(digitalSaleItem.getProductQuantity()));
    }
}
