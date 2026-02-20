package org.github.alexisTrejo11.drugstore.stores.domain.model.schedule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public record WeeklyScheduleConfig(
                TimeRange weekdayHours,
                TimeRange saturdayHours,
                TimeRange sundayHours,
                Set<DayOfWeek> closedDays,   // Días cerrados
                Map<LocalDate, TimeRange> specialHours
) {
    public WeeklyScheduleConfig {
        closedDays = closedDays != null ? Set.copyOf(closedDays) : Set.of();
        specialHours = specialHours != null ? Map.copyOf(specialHours) : Map.of();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TimeRange weekdayHours;
        private TimeRange saturdayHours;
        private TimeRange sundayHours;
        private Set<DayOfWeek> closedDays = new HashSet<>();
        private Map<LocalDate, TimeRange> specialHours = new HashMap<>();

        public Builder weekdayHours(LocalTime open, LocalTime close) {
            this.weekdayHours = TimeRange.of(open, close);
            return this;
        }

        public Builder saturdayHours(LocalTime open, LocalTime close) {
            this.saturdayHours = TimeRange.of(open, close);
            return this;
        }

        public Builder sundayHours(LocalTime open, LocalTime close) {
            this.sundayHours = TimeRange.of(open, close);
            return this;
        }

        public Builder closedOn(DayOfWeek... days) {
            this.closedDays.addAll(Arrays.asList(days));
            return this;
        }

        public Builder specialHours(LocalDate date, LocalTime open, LocalTime close) {
            this.specialHours.put(date, TimeRange.of(open, close));
            return this;
        }

        public Builder closedOn(LocalDate date) {
            this.specialHours.put(date, null);
            return this;
        }

        public WeeklyScheduleConfig build() {
            return new WeeklyScheduleConfig(
                    weekdayHours,
                    saturdayHours,
                    sundayHours,
                    closedDays,
                    specialHours
            );
        }
    }
}
