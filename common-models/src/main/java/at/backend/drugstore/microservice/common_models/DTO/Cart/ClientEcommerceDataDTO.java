package at.backend.drugstore.microservice.common_models.DTO.Cart;

import at.backend.drugstore.microservice.common_models.DTO.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.CardDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ClientEcommerceDataDTO {
    private CartDTO cartDTO;
    private ClientDTO clientDTO;
    private List<CardDTO> cardDTOS;
    private List<AddressDTO> addressDTOS;

}
