package microservice.product_service.Mappers;

import at.backend.drugstore.microservice.common_models.DTOs.Product.Category.MainCategoryDTO;
import microservice.product_service.Model.MainCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MainCategoryMapper {

    @Mapping(target = "productsDTO", ignore = true)
    @Mapping(target = "categoryDTOS", ignore = true)
    @Mapping(target = "subcategoriesDTOS", ignore = true)
    MainCategoryDTO mainCategoryToDTO(MainCategory mainCategory);

}