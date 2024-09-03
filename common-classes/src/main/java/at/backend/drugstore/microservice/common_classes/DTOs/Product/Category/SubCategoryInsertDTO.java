package at.backend.drugstore.microservice.common_classes.DTOs.Product.Category;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubCategoryInsertDTO {
    @JsonProperty("id")
    @NotNull(message = "ID is obligatory")
    @Positive(message = "ID must be positive")
    private Long id;

    @JsonProperty("name")
    @NotNull(message = "Name is obligatory")
    @NotBlank(message = "Name can't be empty")
    private String name;

}