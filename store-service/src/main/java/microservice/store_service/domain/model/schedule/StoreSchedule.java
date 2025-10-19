package microservice.store_service.domain.model.schedule;

import lombok.Getter;
import lombok.NoArgsConstructor;
import microservice.store_service.domain.exception.StoreBusinessRuleException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static java.time.DayOfWeek.*;

@NoArgsConstructor
public class StoreSchedule {
    private Map<DayOfWeek, TimeRange> regularHours;
    private Map<LocalDate, TimeRange> specialHours;
    private Set<DayOfWeek> closedDays;
    @Getter
    private boolean is24Hours;
    @Getter
    private ScheduleType type;

    private StoreSchedule(
            Map<DayOfWeek, TimeRange> regularHours,
            Map<LocalDate, TimeRange> specialHours,
            Set<DayOfWeek> closedDays,
            boolean is24Hours,
            ScheduleType type) {

        this.regularHours = new EnumMap<>(DayOfWeek.class);
        if (regularHours != null) {
            this.regularHours.putAll(regularHours);
        }

        this.specialHours = specialHours != null ? new HashMap<>(specialHours) : new HashMap<>();
        this.closedDays = closedDays != null ? new HashSet<>(closedDays) : new HashSet<>();
        this.is24Hours = is24Hours;
        this.type = type;
    }


    public static StoreSchedule createDefault() {
        EnumMap<DayOfWeek, TimeRange> hours = createDefaultHours();
        return new StoreSchedule(hours, null, null, false, ScheduleType.STANDARD);
    }

    public static StoreSchedule create24Hours() {
        EnumMap<DayOfWeek, TimeRange> hours = new EnumMap<>(DayOfWeek.class);
        for (DayOfWeek day : DayOfWeek.values()) {
            hours.put(day, TimeRange.fullDay());
        }
        return new StoreSchedule(hours, null, null, true, ScheduleType.TWENTY_FOUR_HOURS);
    }

    public static StoreSchedule createClosed() {
        Set<DayOfWeek> allDaysClosed = EnumSet.allOf(DayOfWeek.class);
        return new StoreSchedule(null, null, allDaysClosed, false, ScheduleType.CLOSED);
    }

    public static StoreSchedule create(WeeklyScheduleConfig config) {
        validateWeeklyConfig(config);

        EnumMap<DayOfWeek, TimeRange> regularHours = new EnumMap<>(DayOfWeek.class);

        if (config.weekdayHours() != null) {
            for (DayOfWeek day : Arrays.asList(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY)) {
                if (!config.closedDays().contains(day)) {
                    regularHours.put(day, config.weekdayHours());
                }
            }
        }

        if (config.saturdayHours() != null && !config.closedDays().contains(SATURDAY)) {
            regularHours.put(SATURDAY, config.saturdayHours());
        }

        if (config.sundayHours() != null && !config.closedDays().contains(SUNDAY)) {
            regularHours.put(SUNDAY, config.sundayHours());
        }

        return new StoreSchedule(
                regularHours,
                config.specialHours(),
                config.closedDays(),
                false,
                ScheduleType.CUSTOM
        );
    }

    public static StoreSchedule reconstruct(
            Map<DayOfWeek, TimeRange> regularHours,
            Map<LocalDate, TimeRange> specialHours,
            Set<DayOfWeek> closedDays,
            boolean is24Hours) {

        ScheduleType type = determineScheduleType(regularHours, closedDays, is24Hours);

        return new StoreSchedule(
                regularHours,
                specialHours,
                closedDays,
                is24Hours,
                type
        );
    }

    public void validateForPersist() {
        if (is24Hours) {
            return;
        }

        if (type == ScheduleType.CLOSED) {
            if (closedDays.size() != 7) {
                throw new StoreBusinessRuleException(
                        "Closed schedule must have all days closed");
            }
            return;
        }

        if (regularHours.isEmpty() && specialHours.isEmpty()) {
            throw new StoreBusinessRuleException(
                    "Schedule must have at least one day with operating hours");
        }

        // Validar que días cerrados no tengan horarios
        for (DayOfWeek closedDay : closedDays) {
            if (regularHours.containsKey(closedDay)) {
                throw new StoreBusinessRuleException(
                        "Day " + closedDay + " is marked as closed but has operating hours");
            }
        }

        regularHours.values().forEach(TimeRange::validate);
        specialHours.values().stream()
                .filter(Objects::nonNull)
                .forEach(TimeRange::validate);
    }

    private static void validateWeeklyConfig(WeeklyScheduleConfig config) {
        Objects.requireNonNull(config, "Weekly schedule config cannot be null");

        boolean allDaysClosed = config.closedDays().size() == 7;
        boolean hasAnyHours = config.weekdayHours() != null ||
                config.saturdayHours() != null ||
                config.sundayHours() != null;

        if (!allDaysClosed && !hasAnyHours) {
            throw new StoreBusinessRuleException(
                    "Must provide operating hours for at least one day");
        }

        // Validar TimeRanges si existen
        if (config.weekdayHours() != null) {
            validateTimeRangeForWeekdays(config.weekdayHours());
        }
        if (config.saturdayHours() != null) {
            validateTimeRangeForWeekend(config.saturdayHours(), SATURDAY);
        }
        if (config.sundayHours() != null) {
            validateTimeRangeForWeekend(config.sundayHours(), SUNDAY);
        }
    }

    private static void validateTimeRangeForWeekdays(TimeRange range) {
        range.validate();
    }

    private static void validateTimeRangeForWeekend(TimeRange range, DayOfWeek day) {
        range.validate();

        if (range.getStart().isBefore(LocalTime.of(6, 0))) {
            throw new StoreBusinessRuleException(
                    "Weekend opening cannot be before 6:00 AM");
        }
        if (range.getEnd().isAfter(LocalTime.of(23, 59)) &&
                !range.getEnd().equals(LocalTime.MAX)) {
            throw new StoreBusinessRuleException(
                    "Weekend closing cannot be after 11:59 PM");
        }
    }


    public StoreSchedule withWeeklySchedule(WeeklyScheduleConfig newConfig) {
        validateWeeklyConfig(newConfig);

        return create(newConfig);
    }

    public StoreSchedule convertTo24Hours() {
        if (is24Hours) {
            return this;
        }
        return create24Hours();
    }

    public StoreSchedule withSpecialHours(LocalDate date, TimeRange hours) {
        Objects.requireNonNull(date, "Date cannot be null");
        if (hours != null) {
            hours.validate();
        }

        Map<LocalDate, TimeRange> newSpecialHours = new HashMap<>(this.specialHours);
        newSpecialHours.put(date, hours);

        return new StoreSchedule(
                this.regularHours,
                newSpecialHours,
                this.closedDays,
                this.is24Hours,
                this.type
        );
    }


    public StoreSchedule withDateClosed(LocalDate date) {
        return withSpecialHours(date, null);
    }

    public StoreSchedule withoutSpecialHours(LocalDate date) {
        Map<LocalDate, TimeRange> newSpecialHours = new HashMap<>(this.specialHours);
        newSpecialHours.remove(date);

        return new StoreSchedule(
                this.regularHours,
                newSpecialHours,
                this.closedDays,
                this.is24Hours,
                this.type
        );
    }

    public boolean isOpenAt(LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();

        if (is24Hours) {
            return true;
        }

        // Primero verificar horarios especiales
        if (specialHours.containsKey(date)) {
            TimeRange special = specialHours.get(date);
            return special != null && special.contains(time);
        }

        // Verificar si el día está cerrado
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (closedDays.contains(dayOfWeek)) {
            return false;
        }

        // Verificar horario regular
        TimeRange regular = regularHours.get(dayOfWeek);
        return regular != null && regular.contains(time);
    }

    public boolean isOpenNow() {
        return isOpenAt(LocalDateTime.now());
    }

    public LocalDateTime getNextOpeningTime(LocalDateTime from) {
        LocalDateTime current = from;

        for (int i = 0; i < 8; i++) {
            LocalDate checkDate = current.toLocalDate();
            DayOfWeek dayOfWeek = checkDate.getDayOfWeek();

            // Verificar horario especial
            if (specialHours.containsKey(checkDate)) {
                TimeRange special = specialHours.get(checkDate);
                if (special != null) {
                    LocalDateTime openingTime = LocalDateTime.of(checkDate, special.getStart());
                    if (openingTime.isAfter(from)) {
                        return openingTime;
                    }
                }
                current = current.plusDays(1).with(LocalTime.MIN);
                continue;
            }

            // Skip días cerrados
            if (closedDays.contains(dayOfWeek)) {
                current = current.plusDays(1).with(LocalTime.MIN);
                continue;
            }

            TimeRange regular = regularHours.get(dayOfWeek);
            if (regular != null) {
                LocalDateTime openingTime = LocalDateTime.of(checkDate, regular.getStart());
                if (openingTime.isAfter(from)) {
                    return openingTime;
                }
            }

            current = current.plusDays(1).with(LocalTime.MIN);
        }

        throw new StoreBusinessRuleException(
                "No opening time found in the next 8 days");
    }

    public TimeRange getTodayHours() {
        return getHoursForDate(LocalDate.now());
    }

    public TimeRange getHoursForDate(LocalDate date) {
        if (specialHours.containsKey(date)) {
            return specialHours.get(date);
        }

        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (closedDays.contains(dayOfWeek)) {
            return null;
        }

        return regularHours.get(dayOfWeek);
    }

    public boolean isDayClosed(DayOfWeek day) {
        return closedDays.contains(day);
    }

    public boolean isDateClosed(LocalDate date) {
        if (specialHours.containsKey(date)) {
            return specialHours.get(date) == null;
        }
        return closedDays.contains(date.getDayOfWeek());
    }

    public Map<DayOfWeek, TimeRange> getWeeklySchedule() {
        return Collections.unmodifiableMap(regularHours);
    }

    public Map<LocalDate, TimeRange> getSpecialHours() {
        return Collections.unmodifiableMap(specialHours);
    }

    public Set<DayOfWeek> getClosedDays() {
        return Collections.unmodifiableSet(closedDays);
    }


    private static EnumMap<DayOfWeek, TimeRange> createDefaultHours() {
        EnumMap<DayOfWeek, TimeRange> defaults = new EnumMap<>(DayOfWeek.class);

        TimeRange weekdayHours = TimeRange.of(LocalTime.of(7, 0), LocalTime.of(23, 0));
        for (DayOfWeek day : Arrays.asList(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY)) {
            defaults.put(day, weekdayHours);
        }

        defaults.put(SATURDAY, TimeRange.of(LocalTime.of(9, 0), LocalTime.of(23, 59)));
        defaults.put(SUNDAY, TimeRange.of(LocalTime.of(9, 0), LocalTime.of(21, 0)));

        return defaults;
    }

    private static ScheduleType determineScheduleType(
            Map<DayOfWeek, TimeRange> regularHours,
            Set<DayOfWeek> closedDays,
            boolean is24Hours) {

        if (is24Hours) {
            return ScheduleType.TWENTY_FOUR_HOURS;
        }
        if (closedDays != null && closedDays.size() == 7) {
            return ScheduleType.CLOSED;
        }
        if (regularHours == null || regularHours.isEmpty()) {
            return ScheduleType.CLOSED;
        }
        return ScheduleType.CUSTOM;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoreSchedule that = (StoreSchedule) o;
        return is24Hours == that.is24Hours &&
                Objects.equals(regularHours, that.regularHours) &&
                Objects.equals(specialHours, that.specialHours) &&
                Objects.equals(closedDays, that.closedDays);
    }

    @Override
    public int hashCode() {
        return Objects.hash(regularHours, specialHours, closedDays, is24Hours);
    }
}


