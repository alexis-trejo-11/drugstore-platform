package at.backend.drugstore.microservice.common_models.DTO.Product.Category;

import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@NoArgsConstructor
public class MainCategoryDTO {
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    List<ProductDTO> productsDTO;

    List<CategoryDTO> categoryDTOS;

    List<SubcategoryDTO> subcategoriesDTOS;

}
