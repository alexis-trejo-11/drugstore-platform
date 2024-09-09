package at.backend.drugstore.microservice.common_classes.DTOs.Employee.Postion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class PositionInsertDTO {

    @NotBlank(message = "position_name can't be empty")
    @NotNull(message = "position_name is obligatory")
    @JsonProperty("position_name")
    private String positionName;

    @Positive(message = "salary must has a positive number")
    private BigDecimal salary;

    // Valid Enums -> PART_TIME, FULL_TIME, INTERN, TEMPORAL
    @JsonProperty("classification_workday")
    @NotBlank(message = "classification_workday can't be empty")
    @NotNull(message = "classification_workday is obligatory")
    private String classificationWorkday;


}
