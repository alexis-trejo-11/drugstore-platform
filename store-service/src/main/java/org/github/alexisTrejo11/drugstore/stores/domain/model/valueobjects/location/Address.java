package org.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.location;

import lombok.*;


@Builder
public record Address(String country, String state, String city, String zipCode, String neighborhood, String street,
                      String number) {
    public void validate() {
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

    public static Address NONE = new Address("", "", "", "", "", "", "");

    public static Address create(
            String country,
            String state,
            String city,
            String zipCode,
            String neighborhood,
            String street,
            String number
    ) {
        Address address = new Address(
                country,
                state,
                city,
                zipCode,
                neighborhood,
                street,
                number
        );
        address.validate();
        return address;
    }
}
