package at.backend.drugstore.microservice.common_models.DTO.Product.Category;

import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    List<ProductDTO> productsDTO;

    List<SubcategoryReturnDTO> subcategoriesDTOS;

}
