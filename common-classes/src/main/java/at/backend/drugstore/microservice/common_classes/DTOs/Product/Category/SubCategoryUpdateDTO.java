package at.backend.drugstore.microservice.common_classes.DTOs.Product.Category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubCategoryUpdateDTO {
    @NotNull(message = "Name is required")
    @Positive(message = "Name must be positive")
    private Long id;

    @NotNull(message = "Name is required")
    @NotBlank(message = "Name is required")
    private String name;
}

