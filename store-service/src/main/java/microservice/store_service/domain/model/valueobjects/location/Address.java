package microservice.store_service.domain.model.valueobjects.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class Address {
    private  String country;
    private  String state;
    private  String city;
    private  String zipCode;
    private  String neighborhood;
    private  String street;
    private  String number;

    public void validate(){
        if (country == null || country.isBlank()) {
            throw new IllegalArgumentException("Country cannot be null or blank");
        }
        if (state == null || state.isBlank()) {
            throw new IllegalArgumentException("State cannot be null or blank");
        }

        if (zipCode == null || zipCode.isBlank()) {
            throw new IllegalArgumentException("ZipCode cannot be null or blank");
        }

        if (street == null || street.isBlank()) {
            throw new IllegalArgumentException("Street cannot be null or blank");
        }

        if (number == null || number.isBlank()) {
            throw new IllegalArgumentException("Number cannot be null or blank");
        }

        if (neighborhood == null || neighborhood.isBlank()) {
            throw new IllegalArgumentException("Neighborhood cannot be null or blank");
        }
    }
}
