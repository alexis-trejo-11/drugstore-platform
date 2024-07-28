package microservice.ecommerce_cart_service.Service.Factory;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.CardDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentInsertDTO;

import java.math.BigDecimal;

public class PaymentDTOFactory {
    public static PaymentInsertDTO createPaymentInsertDTO(CartDTO cartDTO, ClientDTO clientDTO, CardDTO cardDTO, Long orderId) {
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