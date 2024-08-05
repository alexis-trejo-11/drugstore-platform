package at.backend.drugstore.microservice.common_models.ExternalService.Payment;

import at.backend.drugstore.microservice.common_models.DTO.Payment.CardDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentInsertDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public interface ExternalPaymentService {
    CompletableFuture<PaymentDTO> initPayment(PaymentInsertDTO paymentInsertDTO);
    CompletableFuture<Result<List<CardDTO>>> getCardByClientId(Long clientId);
}
