package microservice.ecommerce_cart_service.Mappers;

import at.backend.drugstore.microservice.common_classes.DTOs.Cart.CartDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Payment.CardDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Payment.PaymentInsertDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentDtoMapper {

    public PaymentInsertDTO createPaymentInsertDTO(CartDTO cartDTO, ClientDTO clientDTO, CardDTO cardDTO, Long orderId) {
        PaymentInsertDTO paymentInsertDTO = new PaymentInsertDTO();
        paymentInsertDTO.setPaymentMethod("CARD");
        paymentInsertDTO.setSubtotal(cartDTO.getSubtotal());
        paymentInsertDTO.setTotal(cartDTO.getSubtotal());
        paymentInsertDTO.setDiscount(BigDecimal.ZERO);
        paymentInsertDTO.setClientId(clientDTO.getId());
        paymentInsertDTO.setCardId(cardDTO.getId());
        paymentInsertDTO.setOrderId(orderId);
        return paymentInsertDTO;
    }
}
