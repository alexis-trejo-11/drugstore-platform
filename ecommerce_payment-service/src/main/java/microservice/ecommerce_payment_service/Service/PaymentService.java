package microservice.ecommerce_payment_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Payment.PaymentDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Payment.PaymentInsertDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface PaymentService {
    PaymentDTO getPaymentById(Long paymentId);
    Page<PaymentDTO> getCompletedPaymentsByClientId(Long clientId, Pageable pageable);
    void processPayment(Long paymentId, boolean isPaid);
    PaymentDTO initPaymentFromCart(PaymentInsertDTO paymentInsertDTO);
    boolean validExistingPayment(Long paymentId);
}
