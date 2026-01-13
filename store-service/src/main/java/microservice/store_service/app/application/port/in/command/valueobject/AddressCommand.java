package microservice.store_service.app.application.port.in.command.valueobject;

import lombok.Builder;
import microservice.store_service.app.domain.model.valueobjects.location.Address;

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
