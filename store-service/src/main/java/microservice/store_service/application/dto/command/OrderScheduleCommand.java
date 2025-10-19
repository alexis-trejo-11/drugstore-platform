package microservice.store_service.application.dto.command;

import jakarta.validation.Valid;
import microservice.store_service.application.dto.command.valueobject.TimeRangeCommand;
import microservice.store_service.domain.model.schedule.StoreSchedule;
import microservice.store_service.domain.model.schedule.WeeklyScheduleConfig;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.Set;

public record OrderScheduleCommand(
        boolean is24Hours,

        @Valid
        TimeRangeCommand weekdayHours,

        @Valid
        TimeRangeCommand saturdayHours,

        @Valid
        TimeRangeCommand sundayHours,

        Set<DayOfWeek> closedDays,

        Map<LocalDate, TimeRangeCommand> specialHours
) {

    public StoreSchedule toDomain() {
        if (is24Hours) {
            return StoreSchedule.create24Hours();
        }

        WeeklyScheduleConfig.Builder configBuilder = WeeklyScheduleConfig.builder();

        if (weekdayHours != null) {
            configBuilder.weekdayHours(
                    weekdayHours.start(),
                    weekdayHours.end()
            );
        }

        if (saturdayHours != null) {
            configBuilder.saturdayHours(
                    saturdayHours.start(),
                    saturdayHours.end()
            );
        }

        if (sundayHours != null) {
            configBuilder.sundayHours(
                    sundayHours.start(),
                    sundayHours.end()
            );
        }

        if (closedDays != null && !closedDays.isEmpty()) {
            configBuilder.closedOn(closedDays.toArray(new DayOfWeek[0]));
        }

        if (specialHours != null && !specialHours.isEmpty()) {
            specialHours.forEach((date, timeRangeCmd) -> {
                if (timeRangeCmd != null) {
                    configBuilder.specialHours(
                            date,
                            timeRangeCmd.start(),
                            timeRangeCmd.end()
                    );
                } else {
                    // null indica día cerrado especial
                    configBuilder.closedOn(date);
                }
            });
        }

        return StoreSchedule.create(configBuilder.build());
    }

    public static OrderScheduleCommand createStandard() {
        return new OrderScheduleCommand(
                false,
                new TimeRangeCommand(LocalTime.of(9, 0), LocalTime.of(18, 0)),
                new TimeRangeCommand(LocalTime.of(10, 0), LocalTime.of(14, 0)),
                null,
                Set.of(DayOfWeek.SUNDAY),
                null
        );
    }

    public static OrderScheduleCommand create24Hours() {
        return new OrderScheduleCommand(true, null, null, null, null, null);
    }
}