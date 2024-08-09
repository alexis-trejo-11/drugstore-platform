package microservice.product_service.Mappers;

import at.backend.drugstore.microservice.common_models.DTOs.Product.ProductDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Product.ProductInsertDTO;
import microservice.product_service.Model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "category.name", target = "category")
    @Mapping(source = "mainCategory.name", target = "mainCategory")
    @Mapping(source = "subcategory.name", target = "subcategory")
    @Mapping(source = "brand.name", target = "brand")
    @Mapping(source = "supplier.name", target = "supplier")
    ProductDTO productToDTO(Product product);


    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "mainCategory", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "subcategory", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    Product insertDtoToProduct(ProductInsertDTO productInsertDTO);
}
