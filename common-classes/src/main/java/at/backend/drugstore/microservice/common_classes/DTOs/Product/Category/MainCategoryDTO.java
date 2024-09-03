package at.backend.drugstore.microservice.common_classes.DTOs.Product.Category;

import at.backend.drugstore.microservice.common_classes.DTOs.PaginatedResponseDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.ProductDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class MainCategoryDTO {
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    PaginatedResponseDTO<ProductDTO> productsDTO;

    PaginatedResponseDTO<CategoryDTO> categoryDTOS;

    PaginatedResponseDTO<SubcategoryDTO> subcategoriesDTOS;

}
