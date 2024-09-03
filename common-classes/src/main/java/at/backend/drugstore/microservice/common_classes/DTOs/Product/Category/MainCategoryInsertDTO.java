package at.backend.drugstore.microservice.common_classes.DTOs.Product.Category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MainCategoryInsertDTO {

    @NotBlank(message = "Name can't be blank")
    @NotNull(message = "Name is obligatory")
    private String name;

}

