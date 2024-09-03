package at.backend.drugstore.microservice.common_classes.DTOs.Product.Category;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Locale;

@Data
@NoArgsConstructor
public class CategoryUpdateDTO {
    @NotNull(message = "id is required")
    @Positive(message = "id must be positive")
    private Long id;

    @NotNull(message = "name is required")
    @NotEmpty(message = "name must be positive")
    private String name;

    @NotNull(message = "main_category_id is required")
    @Positive(message = "main_category_id must be positive")
    @JsonProperty("main_category_id")
    private Long mainCategoryID;

}

