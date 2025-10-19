package microservice.store_service.application.command;

import microservice.store_service.domain.model.valueobjects.StoreID;

public record UpdateStoreScheduleCommand(
        StoreID storeID,
        OrderScheduleCommand scheduleCommand
) {
}
