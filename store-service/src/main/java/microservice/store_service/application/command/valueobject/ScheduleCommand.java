package microservice.store_service.application.command.valueobject;

import microservice.store_service.domain.model.valueobjects.schedule.StoreSchedule;
import microservice.store_service.domain.model.valueobjects.schedule.TimeRange;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public record ScheduleCommand(
        EnumMap<DayOfWeek, TimeRange> regularHour,
        Map<LocalDate, TimeRange> specialHours,
        Map<DayOfWeek, TimeRange> defaultHours,
        Set<DayOfWeek> closedDays
) {
    public StoreSchedule toDomain() {
        return new StoreSchedule(
                regularHour,
                specialHours,
                defaultHours,
                closedDays,
                false
        );
    }
}