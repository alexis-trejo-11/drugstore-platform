package at.backend.drugstore.microservice.common_models.DTO.Employee.Postion;

import lombok.Data;
import lombok.NoArgsConstructor;


import javax.swing.text.Position;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class PositionReturnDTO {

    private Long id;

    private String positionName;

    private BigDecimal salary;

    private String classificationWorkday;

}
