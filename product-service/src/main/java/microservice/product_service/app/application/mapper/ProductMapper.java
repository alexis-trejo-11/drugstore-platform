package microservice.product_service.app.application.mapper;

import microservice.product_service.app.domain.model.Product;
import microservice.product_service.app.domain.model.ProductId;
import microservice.product_service.app.domain.port.in.command.CreateProductCommand;
import microservice.product_service.app.domain.port.in.command.UpdateProductCommand;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Mapper(componentModel = "spring")
@Component
public interface ProductMapper {

    @Mapping(target = "id", expression = "java(generateProductId())")
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "stockQuantity", defaultValue = "0")
    Product createCommandToProduct(CreateProductCommand command);

    @Named("generateProductId")
    default ProductId generateProductId() {
        return new ProductId(UUID.randomUUID());
    }



    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "id", source = "productId")
    @Mapping(target = "createdAt", ignore = true)
    void updateProductFromCommand(UpdateProductCommand command, @MappingTarget Product product);
}