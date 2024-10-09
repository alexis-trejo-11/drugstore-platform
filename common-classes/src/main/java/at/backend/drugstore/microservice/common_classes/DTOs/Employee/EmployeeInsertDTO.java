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

    @NotNull(message = "first_name is obligatory")
    @NotBlank(message = "first_name can't be empty")
    @JsonProperty("first_name")
    private String firstName;

    @NotNull(message = "last_name is obligatory")
    @NotBlank(message = "last_name can't be empty")
    @JsonProperty("last_name")
    private String lastName;

    @NotNull(message = "genre is obligatory")
    @NotBlank(message = "genre can't be empty")
    @JsonProperty("genre")
    private String genre;

    @Past(message = "date_of_birth must be on a past date")
    @NotNull(message = "date_of_birth is obligatory")
    @JsonProperty("date_of_birth")
    private Date dateOfBirth;

    @PastOrPresent(message = "hired_at must be date")
    @NotNull(message = "hired_at is obligatory")
    @JsonProperty("hired_at")
    private LocalDateTime hiredAt;

    @NotNull(message = "position_id is obligatory")
    @Positive(message = "position_id can't be negative")
    @JsonProperty("position_id")
    private Long positionId;
}
