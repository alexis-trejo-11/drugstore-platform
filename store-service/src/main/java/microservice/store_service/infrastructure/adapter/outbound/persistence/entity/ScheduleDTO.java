package microservice.store_service.infrastructure.adapter.outbound.persistence.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
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
    }
}