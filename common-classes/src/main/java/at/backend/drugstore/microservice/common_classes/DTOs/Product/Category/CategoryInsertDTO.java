package at.backend.drugstore.microservice.common_classes.DTOs.Product.Category;

import at.backend.drugstore.microservice.common_classes.DTOs.Product.ProductDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryInsertDTO {
    @JsonProperty("name")
    @NotNull(message = "Name is obligatory")
    @NotBlank(message = "Name can't be empty")
    private String name;

    @JsonProperty("main_category_id")
    @NotNull(message = "main_category_id is obligatory")
    @Positive(message = "main_category_id must be positive")
    private Long mainCategoryID;

}