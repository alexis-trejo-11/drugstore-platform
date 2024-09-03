package at.backend.drugstore.microservice.common_classes.DTOs.Product.Category;

import at.backend.drugstore.microservice.common_classes.DTOs.PaginatedResponseDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.ProductDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubcategoryDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("category_id")
    private Long categoryId;

    @JsonProperty("products_data")
    private PaginatedResponseDTO<ProductDTO> productDTOS;
}
