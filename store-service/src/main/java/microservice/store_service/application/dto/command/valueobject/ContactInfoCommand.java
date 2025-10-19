package microservice.store_service.application.dto.command.valueobject;

import microservice.store_service.domain.model.valueobjects.ContactInfo;

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
