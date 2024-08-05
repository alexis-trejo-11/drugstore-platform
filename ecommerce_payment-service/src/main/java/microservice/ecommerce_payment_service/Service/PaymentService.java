package microservice.ecommerce_payment_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentInsertDTO;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface PaymentService {
    CompletableFuture<Optional<PaymentDTO>> getPaymentById(Long paymentId);
    CompletableFuture<List<PaymentDTO>> getCompletedPaymentsByClientId(Long clientId);
    CompletableFuture<Void> processPayment(Long paymentId, boolean isPaid);
    CompletableFuture<PaymentDTO> initPaymentFromCart(PaymentInsertDTO paymentInsertDTO);
}
