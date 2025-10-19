package microservice.store_service.application.dto.command;

import microservice.store_service.domain.model.valueobjects.StoreID;

public record UpdateStoreScheduleCommand(
        StoreID id,
        OrderScheduleCommand scheduleCommand
) {
}
