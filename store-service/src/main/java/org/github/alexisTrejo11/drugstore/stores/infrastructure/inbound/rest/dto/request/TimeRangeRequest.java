package org.github.alexisTrejo11.drugstore.stores.infrastructure.inbound.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

@Schema(description = "Time range with start and end times")
public record TimeRangeRequest(

        @NotNull(message = "Start time is required")
        @Schema(description = "Opening time", example = "09:00:00", type = "string", format = "time")
        LocalTime start,

        @NotNull(message = "End time is required")
        @Schema(description = "Closing time", example = "18:00:00", type = "string", format = "time")
        LocalTime end

) {
    public TimeRangeRequest {
        if (start != null && end != null) {
            if (start.isAfter(end) && !end.equals(LocalTime.MAX)) {
                throw new IllegalArgumentException(
                        "Start time must be before end time"
                );
            }
        }
    }
}
