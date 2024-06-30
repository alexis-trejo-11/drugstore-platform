package at.backend.drugstore.microservice.common_models.DTO.Employee.Postion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class PositionUpdateDTO {

    private Long id;

    @JsonProperty("position_name")
    private String positionName;

    private BigDecimal salary;

    @JsonProperty("classification_workday")
    private String classificationWorkday;

    @JsonProperty("position_id")
    private Long positionId;

}
