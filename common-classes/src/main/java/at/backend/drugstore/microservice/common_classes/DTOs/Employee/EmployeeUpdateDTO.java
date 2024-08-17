package at.backend.drugstore.microservice.common_classes.DTOs.Employee;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
public class EmployeeUpdateDTO {
    private Long id;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    private String genre;

    @JsonProperty("birth_date")
    private Date birthDate;

    @JsonProperty("company_email")
    private String companyEmail;

    @JsonProperty("company_phone")
    private String companyPhone;

    @JsonProperty("hired_at")
    private LocalDateTime hiredAt;

    @JsonProperty("fired_at")
    private LocalDateTime firedAt;

    private String address;

    @JsonProperty("is_employee_active")
    private boolean isEmployeeActive;

    @JsonProperty("position_id")
    @Positive(message = "Position Id must be positive")
    private Long positionId;
}
