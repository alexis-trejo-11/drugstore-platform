package at.backend.drugstore.microservice.common_classes.DTOs.Employee;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
public class EmployeeInsertDTO {

    @NotNull
    @NotBlank
    @JsonProperty("first_name")
    private String firstName;

    @NotNull @NotBlank
    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("genre")
    private String genre;

    @Past
    @JsonProperty("birth_date")
    private Date birthDate;

    @PastOrPresent
    @JsonProperty("hired_at")
    private LocalDateTime hiredAt;

    @JsonProperty("address")
    private String address;

}
