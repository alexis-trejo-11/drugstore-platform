package at.backend.drugstore.microservice.common_models.DTO.User;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDTO {

    private Long id;

    private String email;

    private Long employeeId;

    private Long clientId;

    private String phoneNumber;


}
