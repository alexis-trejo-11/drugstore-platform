package microservice.ecommerce_payment_service.Service;

import at.backend.drugstore.microservice.common_models.DTOs.Payment.CardDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Payment.CardInsertDTO;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface CardService {
    CompletableFuture<Void> addCardToClient(CardInsertDTO cardInsertDTO);
    CompletableFuture<List<CardDTO> >getCardByClientId(Long clientId);
    CompletableFuture<Optional<CardDTO>> getCardById(Long cardId);
    CompletableFuture<Boolean> deleteCardById(Long cardId);
    CompletableFuture<Boolean> validateClient(Long clientId);
    CompletableFuture<Boolean> validateCardData(Long cardId, Long clientId);
    }
