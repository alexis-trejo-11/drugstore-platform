package microservice.store_service.application.command.valueobject;

import microservice.store_service.domain.model.valueobjects.StoreID;
import microservice.store_service.domain.model.valueobjects.schedule.StoreSchedule;

public record Schedule24HoursCommand(StoreID storeID) {
    public StoreSchedule toServiceSchedule() {
        return StoreSchedule.create24HoursStore();
    }
}