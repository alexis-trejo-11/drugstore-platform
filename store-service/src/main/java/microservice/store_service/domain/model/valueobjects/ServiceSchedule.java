package microservice.store_service.domain.model.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import microservice.store_service.domain.exception.StoreBusinessRuleException;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
public class ServiceSchedule {
    private LocalDateTime openingTime;
    private LocalDateTime closingTime;
    public final static int DEFAULT_OPENING_HOUR = 8; // 8 AM
    public final static int DEFAULT_CLOSING_HOUR = 22; // 10 PM

    public void validate(){
        if (openingTime == null) {
            throw new IllegalArgumentException("Start time cannot be null");
        }
        if (closingTime == null) {
            throw new IllegalArgumentException("End time cannot be null");
        }
        if (closingTime.isBefore(openingTime)) {
            throw new IllegalArgumentException("End time cannot be before start time");
        }
    }

    public static ServiceSchedule get24HoursSchedule() {
        return ServiceSchedule.builder()
                .openingTime(LocalDateTime.of(0,1,1,0,0))
                .closingTime(LocalDateTime.of(0,1,1,23,59))
                .build();
    }

    public void validateScheduleWithinDefaultHours(boolean is24Hours) {
        if (is24Hours) {
            return;
        }

        if (openingTime != null && closingTime != null && !closingTime.isAfter(openingTime)) {
            throw new StoreBusinessRuleException("Closing time must be after opening time.");
        }

        if (openingTime == null || closingTime == null) {
            throw new StoreBusinessRuleException("Opening and closing times must be provided for non-24-hour stores.");
        }

        if (this.openingTime.getHour() < DEFAULT_OPENING_HOUR || this.closingTime.getHour() > DEFAULT_CLOSING_HOUR) {
            throw new StoreBusinessRuleException("Store schedule must be within default operating hours: " +
                    DEFAULT_OPENING_HOUR + ":00 to " + DEFAULT_CLOSING_HOUR + ":00");
        }
    }
}


