package microservice.ecommerce_sale_service.Mappers;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartItemDTO;
import at.backend.drugstore.microservice.common_models.DTO.Sale.SaleItemDTO;
import microservice.ecommerce_sale_service.Model.DigitalSale;
import microservice.ecommerce_sale_service.Model.DigitalSaleItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface DigitalSaleItemMapper {

    @Mapping(target = "quantity", source = "productQuantity")
    @Mapping(target = "unitPrice", source = "productUnitPrice")
    @Mapping(target = "subtotal", source = ".", qualifiedByName = "calculateSubtotal")
    SaleItemDTO entityToDTO(DigitalSaleItem digitalSaleItem);

    @Mapping(target = "productQuantity", source = "cartItemDTO.quantity")
    @Mapping(target = "productUnitPrice", source = "cartItemDTO.productPrice")
    @Mapping(target = "productName", source = "cartItemDTO.productName")
    @Mapping(target = "productId", source = "cartItemDTO.productId")
    @Mapping(target = "digitalSale", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    DigitalSaleItem toEntity(CartItemDTO cartItemDTO);

    @Mapping(target = "productQuantity", source = "cartItemDTO.quantity")
    @Mapping(target = "productUnitPrice", source = "cartItemDTO.productPrice")
    @Mapping(target = "productName", source = "cartItemDTO.productName")
    @Mapping(target = "productId", source = "cartItemDTO.productId")
    @Mapping(target = "digitalSale", ignore = true)  // Ignoring this field for now
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    void updateEntityFromDTO(CartItemDTO cartItemDTO, @MappingTarget DigitalSaleItem digitalSaleItem);

    default void updateDigitalSale(DigitalSale digitalSale, @MappingTarget DigitalSaleItem digitalSaleItem) {
        digitalSaleItem.setDigitalSale(digitalSale);
    }

        @Named("calculateSubtotal")
    default BigDecimal calculateSubtotal(DigitalSaleItem digitalSaleItem) {
        return digitalSaleItem.getProductUnitPrice().multiply(BigDecimal.valueOf(digitalSaleItem.getProductQuantity()));
    }
}
