package microservice.ecommerce_payment_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Payment.CardDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Payment.CardInsertDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;

import java.util.List;

public interface CardService {
    Result<Void> addCardToClient(CardInsertDTO cardInsertDTO, Long clientId);
    void deleteCardById(Long cardId);
    Result<Void> updateCard(CardInsertDTO cardInsertDTO, Long clientId, Long cardId);
    List<CardDTO> getCardByClientId(Long clientId);

    CardDTO getCardById(Long cardId);
}
