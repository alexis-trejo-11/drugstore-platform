package at.backend.drugstore.microservice.common_models.DTOs.Cart;

import at.backend.drugstore.microservice.common_models.DTOs.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Payment.CardDTO;
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
