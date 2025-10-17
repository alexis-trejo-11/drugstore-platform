package microservice.store_service.application.mapper;

import libs_kernel.mapper.CommandMapper;
import microservice.store_service.application.command.CreateStoreCommand;
import microservice.store_service.domain.model.Store;

public class CreateStoreCommandMapper implements CommandMapper<CreateStoreCommand, Store> {
    @Override
    public Store toTarget(CreateStoreCommand command) {
        return Store.builder()
                .name(command.name())
                .status(command.status())
                .is24Hours(command.is24Hours())
                .contactInfo(command.infoCommand() != null ? command.infoCommand().toContactInfo() : null)
                .address(command.addressCommand() != null ? command.addressCommand().toAddress() : null)
                .serviceSchedule(command.scheduleCommand() != null ? command.scheduleCommand().toServiceSchedule() : null)
                .geolocation(command.geoCommand() != null ? command.geoCommand().toGeolocation() : null)
                .build();
    }
}
