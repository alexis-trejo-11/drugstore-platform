package microservice.test_service.Utils;

import at.backend.drugstore.microservice.common_models.DTOs.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;
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