package microservice.store_service.infrastructure.adapter.outbound.persistence.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import microservice.store_service.domain.model.valueobjects.schedule.StoreSchedule;
import microservice.store_service.domain.model.valueobjects.schedule.TimeRange;
import microservice.store_service.infrastructure.adapter.outbound.persistence.entity.ScheduleDTO;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@Component
public class ScheduleJsonMapper {
    private final ObjectMapper objectMapper;

    public ScheduleJsonMapper() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.findAndRegisterModules();
    }

    public String toJson(StoreSchedule schedule) {
        if (schedule == null) {
            return null;
        }

        try {
            ScheduleDTO dto = ScheduleDTO.builder()
                    .is24Hours(schedule.is24Hours())
                    .regularHours(mapRegularHours(schedule))
                    .specialHours(mapSpecialHours(schedule))
                    .closedDays(schedule.getClosedDays())
                    .build();

            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize StoreSchedule to JSON", e);
        }
    }


    public StoreSchedule fromJson(String json) {
        if (json == null || json.isBlank()) {
            return new StoreSchedule(); // Default schedule
        }

        try {
            ScheduleDTO dto = objectMapper.readValue(json, ScheduleDTO.class);
            return reconstructSchedule(dto);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize StoreSchedule from JSON: " + json, e);
        }
    }


    private Map<DayOfWeek, ScheduleDTO.TimeRangeDTO> mapRegularHours(StoreSchedule schedule) {
        Map<DayOfWeek, TimeRange> regularHours = schedule.getWeeklySchedule();

        Map<DayOfWeek, ScheduleDTO.TimeRangeDTO> result = new EnumMap<>(DayOfWeek.class);

        regularHours.forEach((day, timeRange) -> {
            if (timeRange != null) {
                result.put(day, ScheduleDTO.TimeRangeDTO.builder()
                        .start(timeRange.getStart())
                        .end(timeRange.getEnd())
                        .build());
            }
        });

        return result;
    }

    private Map<LocalDate, ScheduleDTO.TimeRangeDTO> mapSpecialHours(StoreSchedule schedule) {
        Map<LocalDate, TimeRange> specialHours = schedule.getSpecialHours();

        Map<LocalDate, ScheduleDTO.TimeRangeDTO> result = new HashMap<>();

        specialHours.forEach((date, timeRange) -> {
            if (timeRange != null) {
                result.put(date, ScheduleDTO.TimeRangeDTO.builder()
                        .start(timeRange.getStart())
                        .end(timeRange.getEnd())
                        .build());
            } else {
                result.put(date, null); // Means closed for that date
            }
        });

        return result;
    }

    private StoreSchedule reconstructSchedule(ScheduleDTO dto) {
        if (dto.is24Hours()) {
            return StoreSchedule.create24HoursStore();
        }

        StoreSchedule schedule = new StoreSchedule();

        // Reconstruct schedule details
        if (dto.getRegularHours() != null) {
            dto.getRegularHours().forEach((day, timeRangeDTO) -> {
                if (timeRangeDTO != null && timeRangeDTO.getStart() != null && timeRangeDTO.getEnd() != null) {
                    schedule.setHours(day, timeRangeDTO.getStart(), timeRangeDTO.getEnd());
                }
            });
        }

        if (dto.getSpecialHours() != null) {
            dto.getSpecialHours().forEach((date, timeRangeDTO) -> {
                if (timeRangeDTO != null && timeRangeDTO.getStart() != null && timeRangeDTO.getEnd() != null) {
                    schedule.setSpecialHours(date, timeRangeDTO.getStart(), timeRangeDTO.getEnd());
                } else {
                    schedule.closeForDate(date, "Special closure");
                }
            });
        }

        if (dto.getClosedDays() != null && !dto.getClosedDays().isEmpty()) {
            dto.getClosedDays().forEach(schedule::closeDay);
        }

        return schedule;
    }
}
