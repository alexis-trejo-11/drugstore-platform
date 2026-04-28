package org.github.alexisTrejo11.drugstore.stores.application.port.in.command.valueobject;

import lombok.Builder;
import org.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.location.Address;

@Builder
public record AddressCommand(
       String country,
       String state,
       String city,
       String zipCode,
       String neighborhood,
       String street,
       String number
) {
    public Address toAddress() {
        return new Address(
                this.country,
                this.state,
                this.city,
                this.zipCode,
                this.neighborhood,
                this.street,
                this.number
        );
    }
}
