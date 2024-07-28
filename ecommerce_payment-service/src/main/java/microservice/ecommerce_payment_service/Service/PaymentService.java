package microservice.ecommerce_payment_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Order.OrderDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentInsertDTO;

import java.util.List;
import java.util.Optional;

public interface PaymentService {
    Optional<PaymentDTO> getPaymentById(Long paymentId);
    List<PaymentDTO> getCompletedPaymentsByClientId(Long clientId);
    PaymentDTO initPaymentFromCart(PaymentInsertDTO paymentInsertDTO);
    void processPaymentCompleted(Long paymentId);
    void processPaymentFailed(Long paymentId);
    }