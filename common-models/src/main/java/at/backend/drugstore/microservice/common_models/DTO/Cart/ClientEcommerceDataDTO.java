package at.backend.drugstore.microservice.common_models.DTO.Cart;

import at.backend.drugstore.microservice.common_models.DTO.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_models.DTO.Order.OrderDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.CardDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UserEcommerceDataDTO {
    public CartDTO cartDTO;
    public List<CardDTO> cardDTOS;
    public List<AddressDTO> addressDTOS;
    public List<OrderDTO> orderDTOS;
    public List<PaymentDTO> paymentDTOS;
}
