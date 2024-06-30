package at.backend.drugstore.microservice.common_models.DTO.Client;

import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ClientInsertDTO {

    private String firstName;

    private String lastName;

    private LocalDate birthdate;

    private String phone;
}
