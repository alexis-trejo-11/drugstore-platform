package at.backend.drugstore.microservice.common_classes.DTOs.Employee.Postion;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class PositionUpdateDTO {

    @JsonProperty("position_id")
    @NotNull(message = "position_id is obligatory")
    @Positive(message = "position_id must be positive")
    private Long positionId;

    @JsonProperty("position_name")
    @NotNull(message = "position_name is obligatory")
    private String positionName;

    @JsonProperty("salary")
    @NotNull(message = "salary is obligatory")
    private BigDecimal salary;

    @JsonProperty("classification_workday")
    @NotNull(message = "classification_workday is obligatory")
    private String classificationWorkday;
}
