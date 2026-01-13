package microservice.store_service.app.application.port.in.command;

import microservice.store_service.app.domain.model.valueobjects.StoreID;

public record SetUnderMaintenanceCommand(StoreID id) {
}
