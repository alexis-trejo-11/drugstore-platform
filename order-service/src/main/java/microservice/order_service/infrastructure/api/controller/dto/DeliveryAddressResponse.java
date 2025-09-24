package microservice.order_service.infrastructure.api.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DeliveryAddressResponse(
        String street,
        String city,
        String state,
        String zipCode,
        String country,

        // Specific Drugstore Fields
        String apartmentNumber,
        String buildingName,
        String deliveryInstructions,
        String landmark,

        // Store Pickup
        String pharmacyName,
        String pharmacyAddress,
        String pharmacyPhone
) {

    public static DeliveryAddressResponse forDelivery(
            String street, String city, String state, String zipCode, String country,
            String apartmentNumber, String buildingName,
            String deliveryInstructions, String landmark) {
        return new DeliveryAddressResponse(
                street, city, state, zipCode, country,
                apartmentNumber, buildingName, deliveryInstructions, landmark,
                null, null, null
        );
    }

    public static DeliveryAddressResponse forStorePickup(
            String pharmacyName, String pharmacyAddress, String pharmacyPhone) {
        return new DeliveryAddressResponse(
                null, null, null, null, null,
                null, null, null, null,
                pharmacyName, pharmacyAddress, pharmacyPhone
        );
    }

    public boolean isStorePickup() {
        return pharmacyName != null;
    }
}
