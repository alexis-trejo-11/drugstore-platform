package microservice.ecommerce_order_service.Service;

import at.backend.drugstore.microservice.common_models.DTOs.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Client.ClientDTO;
import lombok.extern.slf4j.Slf4j;
import microservice.ecommerce_order_service.Model.ShippingData;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Service
@Slf4j
public class ShippingService {

    public ShippingData generateShippingData(AddressDTO addressDTO, ClientDTO clientDTO) {
        ShippingData shippingData = new ShippingData();
        StringBuilder address = new StringBuilder();
        address.append(addressDTO.getStreet());
        address.append(" #").append(addressDTO.getHouseNumber());

        if (addressDTO.getInnerNumber() != null) {
            address.append(" (interior #").append(addressDTO.getInnerNumber()).append(")");
        }

        shippingData.setAddress(address.toString());
        shippingData.setCity(addressDTO.getCity());
        shippingData.setState(addressDTO.getState());
        shippingData.setCountry(addressDTO.getCountry());
        shippingData.setPostalCode(String.valueOf(addressDTO.getZipCode()));
        shippingData.setPhoneNumber(clientDTO.getPhone());
        shippingData.setRecipientName(clientDTO.getFirstName() + " " + clientDTO.getLastName());
        shippingData.setShippingCost(BigDecimal.ZERO);
        shippingData.setTrackingNumber(generateTrackingNumber());


        return shippingData;
    }

    private String generateTrackingNumber() {
        int[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 0};
        StringBuilder trackingNumber = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            int randomIndex = random.nextInt(numbers.length);
            int randomNumber = numbers[randomIndex];
            trackingNumber.append(randomNumber);
        }

        return trackingNumber.toString();
    }
}