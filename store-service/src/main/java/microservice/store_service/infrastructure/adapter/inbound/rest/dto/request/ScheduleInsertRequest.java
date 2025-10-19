package microservice.store_service.infrastructure.adapter.inbound.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import microservice.store_service.application.dto.command.OrderScheduleCommand;
import microservice.store_service.application.dto.command.UpdateStoreScheduleCommand;
import microservice.store_service.application.dto.command.valueobject.TimeRangeCommand;
import microservice.store_service.domain.model.valueobjects.StoreID;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

@Schema(description = "Schedule configuration for a store")
public record ScheduleInsertRequest(

        @Schema(description = "Whether the store operates 24/7", example = "false")
        boolean is24Hours,

        @Valid
        @Schema(
                description = "Operating hours for weekdays (Monday-Friday)",
                example = "{\"start\": \"09:00:00\", \"end\": \"18:00:00\"}",
                nullable = true
        )
        TimeRangeRequest weekdayHours,

        @Valid
        @Schema(
                description = "Operating hours for Saturday",
                example = "{\"start\": \"10:00:00\", \"end\": \"14:00:00\"}",
                nullable = true
        )
        TimeRangeRequest saturdayHours,

        @Valid
        @Schema(
                description = "Operating hours for Sunday",
                example = "{\"start\": \"11:00:00\", \"end\": \"15:00:00\"}",
                nullable = true
        )
        TimeRangeRequest sundayHours,

        @Schema(
                description = "Days of the week when the store is closed",
                example = "[\"SUNDAY\"]",
                nullable = true
        )
        Set<DayOfWeek> closedDays,

        @Valid
        @Schema(
                description = "Special hours for specific dates (e.g., holidays). Null value means closed.",
                example = "{\"2025-12-25\": null, \"2025-12-31\": {\"start\": \"09:00:00\", \"end\": \"15:00:00\"}}",
                nullable = true
        )
        Map<@NotNull LocalDate, @Valid @NotNull TimeRangeRequest> specialHours

) {


    public UpdateStoreScheduleCommand toCommand(StoreID storeID) {
        return new UpdateStoreScheduleCommand(
                storeID,
                toScheduleCommand()
        );
    }


    public OrderScheduleCommand toScheduleCommand() {
        if (is24Hours) {
            return OrderScheduleCommand.create24Hours();
        }

        return new OrderScheduleCommand(
                false,
                weekdayHours != null ? new TimeRangeCommand(weekdayHours.start(), weekdayHours.end()) : null,
                saturdayHours != null ? new TimeRangeCommand(saturdayHours.start(), saturdayHours.end()) : null,
                sundayHours != null ? new TimeRangeCommand(sundayHours.start(), sundayHours.end()) : null,
                closedDays,
                mapSpecialHours()
        );
    }

    private Map<LocalDate, TimeRangeCommand> mapSpecialHours() {
        if (specialHours == null || specialHours.isEmpty()) {
            return null;
        }

        Map<LocalDate, TimeRangeCommand> result = new java.util.HashMap<>();
        for (var entry : specialHours.entrySet()) {
            LocalDate key = entry.getKey();
            if (key == null) {
                continue;
            }
            TimeRangeRequest tr = entry.getValue();
            result.put(key, tr != null ? new TimeRangeCommand(tr.start(), tr.end()) : null);
        }
        return result;
    }
}








