package microservice.ecommerce_payment_service.Utils;

import at.backend.drugstore.microservice.common_classes.DTOs.Payment.PaymentInsertDTO;
import microservice.ecommerce_payment_service.Model.Payment;

public interface PaymentProcessor {
    void processPayment(Long paymentId, boolean isSuccess);
    void handleCartData(Payment payment, PaymentInsertDTO paymentInsertDTO);
}
