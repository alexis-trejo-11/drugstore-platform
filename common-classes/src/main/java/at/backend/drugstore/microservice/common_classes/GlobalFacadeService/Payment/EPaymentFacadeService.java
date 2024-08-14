package at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Payment;

import at.backend.drugstore.microservice.common_classes.DTOs.Payment.CardDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Payment.PaymentDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Payment.PaymentInsertDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface EPaymentFacadeService {
    CompletableFuture<PaymentDTO> initPayment(PaymentInsertDTO paymentInsertDTO);
    CompletableFuture<Result<List<CardDTO>>> getCardByClientId(Long clientId);
}
