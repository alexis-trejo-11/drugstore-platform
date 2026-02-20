package org.github.alexisTrejo11.drugstore.stores.domain.model;

import org.github.alexisTrejo11.drugstore.stores.domain.model.schedule.TimeRange;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class TimeRangeTest {

    @Test
    void containsAndValidation() {
        TimeRange tr = TimeRange.of(LocalTime.of(8,0), LocalTime.of(18,0));
        assertTrue(tr.contains(LocalTime.of(9,0)));
        assertFalse(tr.contains(LocalTime.of(7,59)));

        // invalid range (start after end) should throw on validate
        TimeRange bad = TimeRange.of(LocalTime.of(18,0), LocalTime.of(8,0));
        assertThrows(Exception.class, bad::validate);
    }

    @Test
    void fullDayContains() {
        TimeRange full = TimeRange.fullDay();
        assertTrue(full.contains(LocalTime.of(0,0)));
        assertTrue(full.contains(LocalTime.of(23,59)));
    }
}

