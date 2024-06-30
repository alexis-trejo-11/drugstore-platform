package microservice.ecommerce_payment_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentInsertDTO;

import java.util.List;

public interface PaymentService {
    PaymentDTO getPaymentById(Long paymentId);
    List<PaymentDTO> getCompletedPaymentsByClientId(Long clientId);
    PaymentDTO validPayment(Long paymentId, boolean isPaymentPaid);
    void initPaymentFromCart(PaymentInsertDTO paymentInsertDTO);
}
