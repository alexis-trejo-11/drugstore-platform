package at.backend.drugstore.microservice.common_classes.DTOs.Employee;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
public class EmployeeUpdateDTO {
    @JsonProperty("employee_id")
    @NotNull(message = "employee_id is obligatory")
    @Positive(message = "employee_id must be positive")
    private Long EmployeeId;

    @JsonProperty("first_name")
    @NotNull(message = "first_name is obligatory")
    @NotEmpty(message = "first_name cant be empty")
    private String firstName;

    @JsonProperty("last_name")
    @NotNull(message = "last_name is obligatory")
    @NotEmpty(message = "last_name cant be empty")
    private String lastName;

    @JsonProperty("genre")
    @NotNull(message = "genre is obligatory")
    @NotEmpty(message = "genre cant be empty")
    private String genre;

    @JsonProperty("birth_date")
    @NotNull(message = "birth_date is obligatory")
    private Date birthDate;

    @JsonProperty("hired_at")
    private LocalDateTime hiredAt;

    @JsonProperty("fired_at")
    private LocalDateTime firedAt;

    @JsonProperty("is_employee_active")
    @NotNull(message = "is_employee_active is obligatory")
    private boolean isEmployeeActive;
}
