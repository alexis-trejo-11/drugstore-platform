package microservice.store_service.application.dto.command.valueobject;

import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record TimeRangeCommand(
        @NotNull(message = "Start time is required")
        LocalTime start,

        @NotNull(message = "End time is required")
        LocalTime end
) {
    public TimeRangeCommand {
        if (start != null && end != null) {
            if (start.isAfter(end) && !end.equals(LocalTime.MAX)) {
                throw new IllegalArgumentException(
                        "Start time must be before end time"
                );
            }
        }
    }
}
