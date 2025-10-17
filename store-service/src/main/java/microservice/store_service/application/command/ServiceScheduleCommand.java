package microservice.store_service.application.command;

import microservice.store_service.domain.model.valueobjects.ServiceSchedule;

import java.time.LocalDateTime;

public record ServiceScheduleCommand(
        LocalDateTime openingTime,
        LocalDateTime closeTime
) {
    public ServiceSchedule toServiceSchedule() {
        return ServiceSchedule.builder()
                .openingTime(this.openingTime)
                .closingTime(this.closeTime)
                .build();
    }
}