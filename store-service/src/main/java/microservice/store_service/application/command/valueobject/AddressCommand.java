package microservice.store_service.application.command.address;

import microservice.store_service.domain.model.valueobjects.Address;

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
