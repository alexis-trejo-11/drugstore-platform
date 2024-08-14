package microservice.ecommerce_cart_service.Utils;

import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import org.springframework.stereotype.Component;

@Component
public class AddressValidator {
    public Result<Void> validateAddress(ClientDTO clientDTO, AddressDTO addressDTO) {
        boolean isAddressCorrect = clientDTO.getId().equals(addressDTO.getClientId());
        if (!isAddressCorrect) {
            return Result.error("Invalid Address");
        }
        return Result.success();
    }
}