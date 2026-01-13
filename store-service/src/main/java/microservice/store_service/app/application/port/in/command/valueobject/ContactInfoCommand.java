package microservice.store_service.app.application.port.in.command.valueobject;

import microservice.store_service.app.domain.model.valueobjects.ContactInfo;

public record ContactInfoCommand(
        String email,
        String phoneNumber
) {

    public ContactInfo toContactInfo() {
        return new ContactInfo(
                this.email,
                this.phoneNumber
        );
    }
}
