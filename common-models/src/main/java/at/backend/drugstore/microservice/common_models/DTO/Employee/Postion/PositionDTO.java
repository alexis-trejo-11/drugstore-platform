package at.backend.drugstore.microservice.common_models.DTO.Employee.Postion;

import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class PositionDTO {

    private Long id;

    private String positionName;

    private BigDecimal salary;

    private String classificationWorkday;

}
