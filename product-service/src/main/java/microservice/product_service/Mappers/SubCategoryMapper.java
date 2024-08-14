package microservice.product_service.Mappers;


import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.SubcategoryDTO;
import microservice.product_service.Model.Subcategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubCategoryMapper {

    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "productInsertDTOS", ignore = true)
    SubcategoryDTO subcategoryToDTO(Subcategory subcategory);
}
