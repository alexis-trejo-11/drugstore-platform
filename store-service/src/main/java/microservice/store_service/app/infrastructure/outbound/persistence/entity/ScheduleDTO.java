package microservice.store_service.app.infrastructure.outbound.persistence.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.store_service.app.domain.model.schedule.TimeRange;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDTO {

    @JsonProperty("is_24_hours")
    private boolean is24Hours;

    @JsonProperty("regular_hours")
    private Map<DayOfWeek, TimeRangeDTO> regularHours;

    @JsonProperty("special_hours")
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    private Map<LocalDate, TimeRangeDTO> specialHours;

    @JsonProperty("closed_days")
    private Set<DayOfWeek> closedDays;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeRangeDTO {
        @JsonProperty("start")
        @JsonFormat(pattern = "HH:mm:ss")
        private LocalTime start;

        @JsonProperty("end")
        @JsonFormat(pattern = "HH:mm:ss")
        private LocalTime end;

        public TimeRange toDomain() {
            return new TimeRange(this.start, this.end);
        }
    }

    @JsonIgnore
    public Map<DayOfWeek, TimeRange> getRegularHoursAsDomain() {
        if (regularHours == null) {
            return null;
        }
        Map<DayOfWeek, TimeRange> result = new java.util.EnumMap<>(DayOfWeek.class);
        for (Map.Entry<DayOfWeek, TimeRangeDTO> entry : regularHours.entrySet()) {
            TimeRangeDTO dto = entry.getValue();
            result.put(entry.getKey(), dto != null ? dto.toDomain() : null);
        }
        return result;
    }

    @JsonIgnore
    public Map<LocalDate, TimeRange> getSpecialHoursAsDomain() {
        if (specialHours == null) {
            return null;
        }
        Map<LocalDate, TimeRange> result = new HashMap<>();
        for (Map.Entry<LocalDate, TimeRangeDTO> entry : specialHours.entrySet()) {
            TimeRangeDTO dto = entry.getValue();
            result.put(entry.getKey(), dto != null ? dto.toDomain() : null);
        }
        return result;
    }
}