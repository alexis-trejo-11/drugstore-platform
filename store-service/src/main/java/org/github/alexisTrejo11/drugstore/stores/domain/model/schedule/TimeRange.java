package org.github.alexisTrejo11.drugstore.stores.domain.model.schedule;

import java.time.LocalTime;


import org.github.alexisTrejo11.drugstore.stores.domain.exception.StoreBusinessRuleException;

import java.util.Objects;

public record TimeRange(LocalTime start, LocalTime end) {

    public TimeRange {
        Objects.requireNonNull(start, "Start time cannot be null");
        Objects.requireNonNull(end, "End time cannot be null");
    }

    public static TimeRange of(LocalTime start, LocalTime end) {
        return new TimeRange(start, end);
    }

    public static TimeRange fullDay() {
        return new TimeRange(LocalTime.MIDNIGHT, LocalTime.MAX);
    }

    public void validate() {
        if (start.isAfter(end) && !end.equals(LocalTime.MAX)) {
            throw new StoreBusinessRuleException(
                    "Start time must be before end time");
        }
    }

    public boolean contains(LocalTime time) {
        if (end.equals(LocalTime.MAX)) {
            return !time.isBefore(start);
        }
        return !time.isBefore(start) && !time.isAfter(end);
    }

    public LocalTime getStart() {
        return start;
    }

    public LocalTime getEnd() {
        return end;
    }
}