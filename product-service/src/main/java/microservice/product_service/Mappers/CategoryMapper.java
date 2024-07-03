package microservice.product_service.Mappers;

import at.backend.drugstore.microservice.common_models.DTO.Product.Category.CategoryDTO;
import microservice.product_service.Model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "productsDTO", ignore = true)
    @Mapping(target = "subcategoriesDTOS", ignore = true)
    CategoryDTO categoryToDTO(Category category);
}
