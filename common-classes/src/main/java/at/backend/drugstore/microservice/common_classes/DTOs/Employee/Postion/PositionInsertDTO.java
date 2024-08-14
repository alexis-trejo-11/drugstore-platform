package at.backend.drugstore.microservice.common_classes.DTOs.Employee.Postion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class PositionInsertDTO {

    @NotBlank @NotNull @Length(min = 2, message = "position_name is required")
    @JsonProperty("position_name")
    private String positionName;

    @Positive(message = "salary must has a positive number")
    private BigDecimal salary;

    @JsonProperty("classification_workday")
    private String classificationWorkday;


}
