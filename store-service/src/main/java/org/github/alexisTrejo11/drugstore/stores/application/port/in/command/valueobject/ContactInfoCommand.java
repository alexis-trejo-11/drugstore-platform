package org.github.alexisTrejo11.drugstore.stores.application.port.in.command.valueobject;

import org.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.ContactInfo;

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
