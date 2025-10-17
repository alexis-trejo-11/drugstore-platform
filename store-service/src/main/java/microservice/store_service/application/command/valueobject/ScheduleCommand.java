package microservice.store_service.application.command.schedule;

import microservice.store_service.domain.model.valueobjects.StoreSchedule;
import microservice.store_service.domain.model.valueobjects.TimeRange;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public record ServiceScheduleCommand(
        EnumMap<DayOfWeek, TimeRange> regularHour,
        Map<LocalDate, TimeRange>specialHours,
        Map<DayOfWeek, TimeRange> defaultHours,
        Set<DayOfWeek> closedDays
) {
    public StoreSchedule toServiceSchedule() {
        return StoreSchedule.builder()
                .regularHours(this.regularHour)
                .specialHours(this.specialHours)
                .defaultHours(this.defaultHours)
                .closedDays(this.closedDays)
                .build();
    }
}