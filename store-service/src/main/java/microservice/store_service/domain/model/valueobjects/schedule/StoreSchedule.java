package microservice.store_service.domain.model.valueobjects.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import microservice.store_service.domain.exception.StoreBusinessRuleException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static java.time.DayOfWeek.*;

@Builder
@AllArgsConstructor
@Getter
public class StoreSchedule {
    private final EnumMap<DayOfWeek, TimeRange> regularHours;
    private final Map<LocalDate, TimeRange> specialHours;
    private final Map<DayOfWeek, TimeRange> defaultHours;
    private final Set<DayOfWeek> closedDays;
    private boolean is24Hours;

    public StoreSchedule() {
        this.regularHours = new EnumMap<>(DayOfWeek.class);
        this.specialHours = new HashMap<>();
        this.defaultHours = createDefaultHours();
        this.closedDays = new HashSet<>();
        initializeWithDefaults();
    }

    // Constructor for 24-hour stores
    private StoreSchedule(DayOfWeek dayOfWeek, LocalTime open, LocalTime close) {
        this();
        this.is24Hours = true;
        for (DayOfWeek day : DayOfWeek.values()) {
            this.regularHours.put(day, new TimeRange(LocalTime.MIDNIGHT, LocalTime.MAX));
        }
    }

    private Map<DayOfWeek, TimeRange> createDefaultHours() {
        EnumMap<DayOfWeek, TimeRange> defaults = new EnumMap<>(DayOfWeek.class);

        for (DayOfWeek day : Arrays.asList(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY)) {
            defaults.put(day, new TimeRange(LocalTime.of(7, 0), LocalTime.of(23, 0)));
        }

        defaults.put(SATURDAY, new TimeRange(LocalTime.of(9, 0), LocalTime.of(23, 59)));
        defaults.put(SUNDAY, new TimeRange(LocalTime.of(9, 0), LocalTime.of(21, 0)));

        return defaults;
    }

    public void setHours(DayOfWeek day, LocalTime open, LocalTime close) {
        validateTimeRange(day, open, close);
        regularHours.put(day, new TimeRange(open, close));
        closedDays.remove(day); // If it was closed, now it's open
    }

    public void setHoursForRange(DayOfWeek fromDay, DayOfWeek toDay, LocalTime open, LocalTime close) {
        for (DayOfWeek day = fromDay; day.getValue() <= toDay.getValue(); day = day.plus(1)) {
            setHours(day, open, close);
        }
    }

    public void closeDay(DayOfWeek day) {
        regularHours.remove(day);
        closedDays.add(day);
    }

    public void closeDays(DayOfWeek... days) {
        for (DayOfWeek day : days) {
            closeDay(day);
        }
    }

    public void setSpecialHours(LocalDate date, LocalTime open, LocalTime close) {
        validateTimeRange(date.getDayOfWeek(), open, close);
        specialHours.put(date, new TimeRange(open, close));
    }

    public void closeForDate(LocalDate date, String reason) {
        specialHours.put(date, null); // null indicates closed for that date
    }

    public TimeRange getHours(DayOfWeek day) {
        return regularHours.get(day);
    }

    public TimeRange getSpecialHours(LocalDate date) {
        return specialHours.get(date);
    }

    public boolean isDayClosed(DayOfWeek day) {
        return closedDays.contains(day);
    }

    public boolean isDateClosed(LocalDate date) {
        TimeRange special = specialHours.get(date);
        return special == null || isDayClosed(date.getDayOfWeek());
    }

    public void initializeWithDefaults() {
        regularHours.putAll(defaultHours);
        closedDays.clear(); // Reset closed days when initializing with defaults
    }

    public boolean isOpenAt(LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();

        if (is24Hours) {
            return true;
        }

        TimeRange specialHours = getSpecialHours(date);
        if (specialHours != null) {
            return specialHours.contains(time);
        }

        if (isDateClosed(date)) {
            return false;
        }

        TimeRange regularHours = getHours(date.getDayOfWeek());
        return regularHours != null && regularHours.contains(time);
    }

    public boolean isOpenNow() {
        return isOpenAt(LocalDateTime.now());
    }

    public LocalDateTime getNextOpeningTime(LocalDateTime from) {
        LocalDateTime current = from;

        // Check up to 8 days in advance (in case of long weekends/holidays)
        for (int i = 0; i < 8; i++) {
            LocalDate checkDate = current.toLocalDate();
            DayOfWeek dayOfWeek = checkDate.getDayOfWeek();

            // Skip if day is permanently closed
            if (isDayClosed(dayOfWeek)) {
                current = current.plusDays(1).with(LocalTime.MIN);
                continue;
            }

            TimeRange special = getSpecialHours(checkDate);
            if (special != null) {
                if (special.getStart() != null) {
                    LocalDateTime openingTime = LocalDateTime.of(checkDate, special.getStart());
                    if (openingTime.isAfter(from)) {
                        return openingTime;
                    }
                }
                current = current.plusDays(1).with(LocalTime.MIN);
                continue;
            }

            TimeRange regular = getHours(dayOfWeek);
            if (regular != null && regular.getStart() != null) {
                LocalDateTime openingTime = LocalDateTime.of(checkDate, regular.getStart());
                if (openingTime.isAfter(from)) {
                    return openingTime;
                }
            }

            current = current.plusDays(1).with(LocalTime.MIN);
        }

        throw new StoreBusinessRuleException("No opening time found in the foreseeable future");
    }

    // Get today's hours
    public TimeRange getTodaysHours() {
        return getHoursForDate(LocalDate.now());
    }

    public TimeRange getHoursForDate(LocalDate date) {
        // Check special hours first
        TimeRange special = getSpecialHours(date);
        if (special != null) {
            return special;
        }

        // Check if closed
        if (isDateClosed(date)) {
            return null;
        }

        // Return regular hours
        return getHours(date.getDayOfWeek());
    }

    private void validateTimeRange(DayOfWeek day, LocalTime open, LocalTime close) {
        if (open == null || close == null) {
            throw new StoreBusinessRuleException("Opening and closing times cannot be null");
        }

        if (open.isAfter(close) && !close.equals(LocalTime.MIDNIGHT)) {
            throw new StoreBusinessRuleException("Opening time must be before closing time for " + day);
        }

        // Weekend restrictions
        if ((day == SATURDAY || day == SUNDAY)) {
            if (open.isBefore(LocalTime.of(6, 0))) {
                throw new StoreBusinessRuleException("Weekend opening cannot be before 6 AM");
            }
            if (close.isAfter(LocalTime.of(23, 59)) && !close.equals(LocalTime.MAX)) {
                throw new StoreBusinessRuleException("Weekend closing cannot be after 11:59 PM");
            }
        }
    }

    public Map<DayOfWeek, TimeRange> getWeeklySchedule() {
        return Collections.unmodifiableMap(regularHours);
    }

    public Set<DayOfWeek> getClosedDays() {
        return Collections.unmodifiableSet(closedDays);
    }

    public Map<LocalDate, TimeRange> getSpecialHours() {
        return Collections.unmodifiableMap(specialHours);
    }

    public static StoreSchedule create24HoursStore() {
        StoreSchedule schedule = new StoreSchedule();
        schedule.is24Hours = true;
        for (DayOfWeek day : DayOfWeek.values()) {
            schedule.regularHours.put(day, new TimeRange(LocalTime.MIDNIGHT, LocalTime.MAX));
        }
        return schedule;
    }

    public static StoreSchedule createClosedStore() {
        StoreSchedule schedule = new StoreSchedule();
        for (DayOfWeek day : DayOfWeek.values()) {
            schedule.closeDay(day);
        }
        return schedule;
    }
}