package microservice.store_service.infrastructure.adapter.inbound.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;

@Schema(description = "Time range for opening hours")
public record TimeRangeRequest(
        @Schema(description = "Opening time", example = "08:00")
        LocalTime open,
        @Schema(description = "Closing time", example = "18:00")
        LocalTime close
) {}
