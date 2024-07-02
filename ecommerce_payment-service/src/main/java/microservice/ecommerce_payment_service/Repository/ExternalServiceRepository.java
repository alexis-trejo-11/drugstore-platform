package microservice.ecommerce_payment_service.Repository;

import at.backend.drugstore.microservice.common_models.DTO.Cart.ClientEcommerceDataDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.ExternalService.Adress.ExternalAddressService;
import at.backend.drugstore.microservice.common_models.ExternalService.Client.ExternalClientService;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ExternalServiceRepository {

    private final ExternalClientService externalClientService;
    private final ExternalAddressService externalAddressService;

    @Autowired
    public ExternalServiceRepository(ExternalClientService externalClientService, ExternalAddressService externalAddressService) {
        this.externalClientService = externalClientService;
        this.externalAddressService = externalAddressService;

    }

    public Result<ClientEcommerceDataDTO> getEcommerceClientDataById(Long userId) {
        ClientEcommerceDataDTO clientEcommerceDataDTO = new ClientEcommerceDataDTO();

        Result<ClientDTO> clientDTOResult = externalClientService.findClientById(userId);
        if (!clientDTOResult.isSuccess()) {
            return null;
        }

        Result<List<AddressDTO>> adressResult = externalAddressService.getAddressByClientId(userId);
        if (!adressResult.isSuccess()) {
            return Result.error("An Error Occurred Getting Addresses");
        }

        clientEcommerceDataDTO.setAddressDTOS(adressResult.getData());

        return Result.success(clientEcommerceDataDTO);
    }


}
