package at.backend.drugstore.microservice.common_classes.DTOs.Employee;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
public class EmployeeDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("genre")
    private String genre;

    @JsonProperty("date_of_birth")
    private Date dateOfBirth;

    @JsonProperty("company_email")
    private String companyEmail;

    @JsonProperty("company_phone")
    private String companyPhone;

    @JsonProperty("hired_at")
    private LocalDateTime hiredAt;

    @JsonProperty("position")
    private String position;


}
