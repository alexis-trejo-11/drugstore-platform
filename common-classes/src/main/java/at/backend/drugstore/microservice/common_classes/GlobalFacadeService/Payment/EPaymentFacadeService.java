package at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Payment;

import at.backend.drugstore.microservice.common_classes.DTOs.Payment.CardDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Payment.PaymentDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Payment.PaymentInsertDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface EPaymentServiceFacadeService {
    CompletableFuture<PaymentDTO> initPayment(PaymentInsertDTO paymentInsertDTO);
    CompletableFuture<Result<List<CardDTO>>> getCardByClientId(Long clientId);
}
