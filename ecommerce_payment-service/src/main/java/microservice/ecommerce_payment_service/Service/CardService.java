package microservice.ecommerce_payment_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Payment.CardDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Payment.CardInsertDTO;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface CardService {
    void addCardToClient(CardInsertDTO cardInsertDTO);
    List<CardDTO> getCardByClientId(Long clientId);
    CardDTO getCardById(Long cardId);
    void deleteCardById(Long cardId);
    boolean validateClient(Long clientId);
    boolean validateCardData(Long cardId, Long clientId);
    boolean validateExistingCard(Long cardId);

}
