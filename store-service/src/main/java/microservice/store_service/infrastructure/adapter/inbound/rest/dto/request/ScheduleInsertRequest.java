package microservice.store_service.infrastructure.adapter.inbound.rest.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import microservice.store_service.application.command.UpdateStoreScheduleCommand;
import microservice.store_service.application.command.valueobject.ScheduleCommand;
import microservice.store_service.domain.model.valueobjects.StoreID;
import microservice.store_service.domain.model.valueobjects.schedule.TimeRange;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Schema(description = "Schedule Request DTO - regular and special hours")
public record ScheduleInsertRequest(
        @Schema(description = "Whether the store operates 24 hours", example = "false")
        boolean is24Hours,

        @NotNull @Valid
        @Schema(description = "Regular weekly hours mapping (DayOfWeek -> TimeRange)", example = "{MONDAY: {open: '08:00', close: '18:00'}}")
        Map<DayOfWeek, TimeRangeRequest> regularHours,

        @Valid
        @Schema(description = "Special date-specific hours (LocalDate -> TimeRange)", nullable = true)
        Map<LocalDate, TimeRangeRequest> specialHours,

        @Schema(description = "Days of week when the store is closed")
        Set<DayOfWeek> closedDays
) {
    public UpdateStoreScheduleCommand toCommand(StoreID storeID) {
        EnumMap<DayOfWeek, TimeRange> regularHour = new EnumMap<>(DayOfWeek.class);
        for (var entry : this.regularHours.entrySet()) {
            regularHour.put(entry.getKey(), new TimeRange(entry.getValue().open(), entry.getValue().close()));
        }

        Map<LocalDate, TimeRange> specialHour = null;
        if (this.specialHours != null) {
            for (var entry : this.specialHours.entrySet()) {
                if (specialHour == null) specialHour = new HashMap<>();

                specialHour.put(entry.getKey(), new TimeRange(entry.getValue().open(), entry.getValue().close()));
            }
        }
        var scheduleCommand =  new ScheduleCommand(
                regularHour,
                specialHour,
                null,
                this.closedDays
        );

        return new UpdateStoreScheduleCommand(
                storeID,
                scheduleCommand
        );

    }

    public ScheduleCommand toScheduleCommand() {
        EnumMap<DayOfWeek, TimeRange> regularHour = new EnumMap<>(DayOfWeek.class);
        for (var entry : this.regularHours.entrySet()) {
            regularHour.put(entry.getKey(), new TimeRange(entry.getValue().open(), entry.getValue().close()));
        }

        Map<LocalDate, TimeRange> specialHour = null;
        if (this.specialHours != null) {
            for (var entry : this.specialHours.entrySet()) {
                if (specialHour == null) specialHour = new HashMap<>();

                specialHour.put(entry.getKey(), new TimeRange(entry.getValue().open(), entry.getValue().close()));
            }
        }
        return new ScheduleCommand(
                regularHour,
                specialHour,
                null,
                this.closedDays
        );
    }
}
