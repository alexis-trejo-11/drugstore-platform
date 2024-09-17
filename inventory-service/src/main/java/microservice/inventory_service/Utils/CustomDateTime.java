package microservice.inventory_service.Utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CustomDateTime {

    @JsonProperty("start_time")
    @NotNull(message = "start_time is obligatory")
    private LocalDateTime startTime;

    @JsonProperty("end_time")
    @NotNull(message = "end_time is obligatory")
    private LocalDateTime endTime;
}
