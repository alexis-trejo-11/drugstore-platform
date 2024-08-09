package at.backend.drugstore.microservice.common_models.GlobalFacadeService.Payment;

import at.backend.drugstore.microservice.common_models.DTOs.Payment.CardDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Payment.PaymentDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Payment.PaymentInsertDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public interface EPaymentFacadeService {
    CompletableFuture<PaymentDTO> initPayment(PaymentInsertDTO paymentInsertDTO);
    CompletableFuture<Result<List<CardDTO>>> getCardByClientId(Long clientId);
}
