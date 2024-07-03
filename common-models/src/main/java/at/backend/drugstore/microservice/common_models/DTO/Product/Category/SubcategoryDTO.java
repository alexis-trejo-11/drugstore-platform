package at.backend.drugstore.microservice.common_models.DTO.Product.Category;

import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubcategoryDTO {
    private Long id;
    private String name;
    private Long categoryId;
    private List<ProductDTO> productInsertDTOS;

}
