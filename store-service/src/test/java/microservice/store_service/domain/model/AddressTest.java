package microservice.store_service.domain.model;

import microservice.store_service.app.domain.model.valueobjects.location.Address;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AddressTest {

    @Test
    void validateRequiredFields() {
        Address a = Address.builder()
                .country("C")
                .state("S")
                .city("City")
                .zipCode("0000")
                .neighborhood("N")
                .street("St")
                .number("1")
                .build();

        a.validate(); // should not throw

        Address invalid = Address.builder().country("").state("").city("").zipCode("").neighborhood("").street("").number("").build();
        assertThrows(IllegalArgumentException.class, invalid::validate);
    }
}

